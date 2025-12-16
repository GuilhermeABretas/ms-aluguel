package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.AluguelDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovaDevolucaoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoAluguelDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.exception.RecursoNaoEncontradoException;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.Aluguel;
import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;
import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import br.com.bicicletario.ms_aluguel.domain.repository.AluguelRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CartaoDeCreditoRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CiclistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AluguelServiceImpl implements AluguelService {

    private final AluguelRepository aluguelRepository;
    private final CiclistaRepository ciclistaRepository;
    private final CartaoDeCreditoRepository cartaoRepository;
    private final CiclistaService ciclistaService; // Reusar validações
    private final EquipamentoService equipamentoService; // Mock externo
    private final PagamentoService pagamentoService; // Mock externo
    private final EmailService emailService; // Mock externo

    public AluguelServiceImpl(AluguelRepository aluguelRepository,
                              CiclistaRepository ciclistaRepository,
                              CartaoDeCreditoRepository cartaoRepository,
                              CiclistaService ciclistaService,
                              EquipamentoService equipamentoService,
                              PagamentoService pagamentoService,
                              EmailService emailService) {
        this.aluguelRepository = aluguelRepository;
        this.ciclistaRepository = ciclistaRepository;
        this.cartaoRepository = cartaoRepository;
        this.ciclistaService = ciclistaService;
        this.equipamentoService = equipamentoService;
        this.pagamentoService = pagamentoService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public AluguelDTO realizarAluguel(NovoAluguelDTO dto) {

        if (!ciclistaService.permiteAluguel(dto.getCiclista())) {
            throw new ValidacaoException("Ciclista não apto para alugar (Inativo ou com aluguel pendente).");
        }


        Ciclista ciclista = ciclistaRepository.findById(dto.getCiclista())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ciclista não encontrado"));

        CartaoDeCredito cartao = cartaoRepository.findByCiclistaId(dto.getCiclista())
                .orElseThrow(() -> new ValidacaoException("Ciclista não possui cartão de crédito cadastrado para cobrança."));


        Long idBicicleta = equipamentoService.recuperarBicicletaPorTranca(dto.getTrancaInicio());

        // 4. Realiza Cobrança  - Mock

        NovoCartaoDeCreditoDTO cartaoDTO = new NovoCartaoDeCreditoDTO();
        cartaoDTO.setNomeTitular(cartao.getNomeTitular());
        cartaoDTO.setNumero(cartao.getNumero());
        cartaoDTO.setValidade(cartao.getValidade());
        cartaoDTO.setCvv(cartao.getCvv());

        pagamentoService.validarCartao(cartaoDTO); // Se falhar, lança exceção


        Aluguel aluguel = new Aluguel();
        aluguel.setCiclista(ciclista);
        aluguel.setIdTrancaInicio(dto.getTrancaInicio());
        aluguel.setIdBicicleta(idBicicleta);
        aluguel.setDataHoraRetirada(LocalDateTime.now());


        aluguelRepository.save(aluguel);


        equipamentoService.destrancarTranca(dto.getTrancaInicio());


        emailService.enviarEmail(ciclista.getEmail(), "Aluguel Realizado", "Você alugou a bicicleta " + idBicicleta);

        return new AluguelDTO(aluguel);
    }

    @Override
    @Transactional
    public AluguelDTO realizarDevolucao(NovaDevolucaoDTO dto) {


        Aluguel aluguel = aluguelRepository.findByBicicletaAndDevolucaoNull(dto.getIdBicicleta())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum aluguel em aberto encontrado para a bicicleta " + dto.getIdBicicleta()));


        LocalDateTime agora = LocalDateTime.now();
        long minutos = Duration.between(aluguel.getDataHoraRetirada(), agora).toMinutes();

        Double valorExtra = 0.0;
        if (minutos > 120) {
            valorExtra = (minutos - 120) * 0.50;

            NovoCartaoDeCreditoDTO cartaoDTO = new NovoCartaoDeCreditoDTO();
            CartaoDeCredito cartao = aluguel.getCiclista().getCartaoDeCredito();
            cartaoDTO.setNumero(cartao.getNumero());
            pagamentoService.validarCartao(cartaoDTO);
        }


        aluguel.setDataHoraDevolucao(agora);
        aluguel.setIdTrancaFim(dto.getIdTranca());
        aluguel.setValorCobrado(10.0 + valorExtra);

        aluguelRepository.save(aluguel);


        emailService.enviarEmail(aluguel.getCiclista().getEmail(), "Devolução Realizada", "Valor total: " + aluguel.getValorCobrado());

        return new AluguelDTO(aluguel);
    }
}