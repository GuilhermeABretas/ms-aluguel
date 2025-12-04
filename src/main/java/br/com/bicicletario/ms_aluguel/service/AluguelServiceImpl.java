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
        // 1. Valida se ciclista pode alugar (Ativo e sem pendências)
        if (!ciclistaService.permiteAluguel(dto.getCiclista())) {
            throw new ValidacaoException("Ciclista não apto para alugar (Inativo ou com aluguel pendente).");
        }

        // 2. Recupera dados
        Ciclista ciclista = ciclistaRepository.findById(dto.getCiclista())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ciclista não encontrado"));

        CartaoDeCredito cartao = cartaoRepository.findByCiclistaId(dto.getCiclista())
                .orElseThrow(() -> new ValidacaoException("Ciclista não possui cartão de crédito cadastrado para cobrança."));

        // 3. Consulta MS Equipamento para saber qual bike está na tranca
        Long idBicicleta = equipamentoService.recuperarBicicletaPorTranca(dto.getTrancaInicio());

        // 4. Realiza Cobrança (Ex: R$ 10,00 fixo) - Mock
        // Precisamos transformar a entidade Cartao em DTO para o serviço de pagamento
        NovoCartaoDeCreditoDTO cartaoDTO = new NovoCartaoDeCreditoDTO();
        cartaoDTO.setNomeTitular(cartao.getNomeTitular());
        cartaoDTO.setNumero(cartao.getNumero());
        cartaoDTO.setValidade(cartao.getValidade());
        cartaoDTO.setCvv(cartao.getCvv());

        pagamentoService.validarCartao(cartaoDTO); // Se falhar, lança exceção

        // 5. Cria registro de Aluguel
        Aluguel aluguel = new Aluguel();
        aluguel.setCiclista(ciclista);
        aluguel.setIdTrancaInicio(dto.getTrancaInicio());
        aluguel.setIdBicicleta(idBicicleta);
        aluguel.setDataHoraRetirada(LocalDateTime.now());
        // Devolução e Valor ficam nulos por enquanto

        aluguelRepository.save(aluguel);

        // 6. Libera a tranca (Mock)
        equipamentoService.destrancarTranca(dto.getTrancaInicio());

        // 7. Envia email
        emailService.enviarEmail(ciclista.getEmail(), "Aluguel Realizado", "Você alugou a bicicleta " + idBicicleta);

        return new AluguelDTO(aluguel);
    }

    @Override
    @Transactional
    public AluguelDTO realizarDevolucao(NovaDevolucaoDTO dto) {
        // 1. Busca qual aluguel está ativo para essa bicicleta
        // (Lógica simplificada: buscamos o último aluguel aberto dessa bike.
        // Num sistema real, o MS Equipamento mandaria o ID do aluguel ou do ciclista, mas o swagger manda só bike e tranca)

        // Vamos buscar pelo ciclista? Não temos o ID do ciclista no DTO.
        // Vamos assumir que temos que encontrar o aluguel pela bicicleta que está voltando.
        // Precisaríamos de um metodo no repository: findByBicicletaIdAndDataHoraDevolucaoIsNull
        // Como não criamos, vamos fazer uma busca "mockada" ou varrer (não ideal, mas serve pro protótipo).
        // Melhor: Vamos adicionar o metodo no Repository agora (eu aviso abaixo).

        Aluguel aluguel = aluguelRepository.findByBicicletaAndDevolucaoNull(dto.getIdBicicleta())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum aluguel em aberto encontrado para a bicicleta " + dto.getIdBicicleta()));

        // 2. Calcula valor extra (Regra de Negócio)
        // Ex: Se passou de 2 horas, cobra extra.
        LocalDateTime agora = LocalDateTime.now();
        long minutos = Duration.between(aluguel.getDataHoraRetirada(), agora).toMinutes();

        Double valorExtra = 0.0;
        if (minutos > 120) {
            valorExtra = (minutos - 120) * 0.50; // R$ 0,50 por minuto extra
            // Cobra o extra
            NovoCartaoDeCreditoDTO cartaoDTO = new NovoCartaoDeCreditoDTO();
            CartaoDeCredito cartao = aluguel.getCiclista().getCartaoDeCredito();
            cartaoDTO.setNumero(cartao.getNumero()); // Simplificado
            pagamentoService.validarCartao(cartaoDTO);
        }

        // 3. Atualiza o Aluguel
        aluguel.setDataHoraDevolucao(agora);
        aluguel.setIdTrancaFim(dto.getIdTranca());
        aluguel.setValorCobrado(10.0 + valorExtra); // 10 fixo + extra

        aluguelRepository.save(aluguel);

        // 4. Tranca a tranca (Mock - assumimos que o hardware fez isso)

        // 5. Envia email
        emailService.enviarEmail(aluguel.getCiclista().getEmail(), "Devolução Realizada", "Valor total: " + aluguel.getValorCobrado());

        return new AluguelDTO(aluguel);
    }
}