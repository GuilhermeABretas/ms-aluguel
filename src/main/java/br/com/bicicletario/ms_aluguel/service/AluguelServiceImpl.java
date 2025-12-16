package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.*;
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

    private static final Double CUSTO_FIXO_INICIAL = 10.00;
    private static final Double CUSTO_HORA_EXTRA = 5.00;

    private final AluguelRepository aluguelRepository;
    private final CiclistaRepository ciclistaRepository;
    private final CartaoDeCreditoRepository cartaoRepository;
    private final EquipamentoService equipamentoService;
    private final PagamentoService pagamentoService;
    private final EmailService emailService;
    private final CiclistaService ciclistaService;

    public AluguelServiceImpl(
            AluguelRepository aluguelRepository,
            CiclistaRepository ciclistaRepository,
            CartaoDeCreditoRepository cartaoRepository,
            EquipamentoService equipamentoService,
            PagamentoService pagamentoService,
            EmailService emailService,
            CiclistaService ciclistaService) {
        this.aluguelRepository = aluguelRepository;
        this.ciclistaRepository = ciclistaRepository;
        this.cartaoRepository = cartaoRepository;
        this.equipamentoService = equipamentoService;
        this.pagamentoService = pagamentoService;
        this.emailService = emailService;
        this.ciclistaService = ciclistaService;
    }

    @Override
    @Transactional
    public AluguelDTO realizarAluguel(NovoAluguelDTO dto) {
        // 1. Validar se ciclista pode alugar
        if (!ciclistaService.permiteAluguel(dto.getCiclista())) {
            throw new ValidacaoException("Ciclista não apto para alugar (pendente, inativo ou já possui aluguel).");
        }

        // 2. Buscar Entidade Ciclista (Necessário para salvar no Aluguel)
        Ciclista ciclista = ciclistaRepository.findById(dto.getCiclista())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ciclista não encontrado"));

        // 3. Verificar bicicleta na tranca
        Long idBicicleta = equipamentoService.recuperarBicicletaPorTranca(dto.getTrancaInicio());
        if (idBicicleta == null) {
            throw new ValidacaoException("Não há bicicleta na tranca informada.");
        }

        // 4. Cobrança
        CartaoDeCredito cartao = cartaoRepository.findByCiclistaId(dto.getCiclista())
                .orElseThrow(() -> new ValidacaoException("Ciclista não possui cartão para cobrança."));

        pagamentoService.realizarCobranca(cartao, CUSTO_FIXO_INICIAL);

        // 5. Destrancar
        equipamentoService.destrancarTranca(dto.getTrancaInicio());

        // 6. Criar Aluguel
        Aluguel aluguel = new Aluguel();
        aluguel.setCiclista(ciclista); // Define o objeto Ciclista
        aluguel.setTrancaInicioId(dto.getTrancaInicio());
        aluguel.setBicicletaId(idBicicleta);
        aluguel.setDataHoraInicio(LocalDateTime.now()); // Usa o nome correto do campo
        aluguel.setValorCobrado(CUSTO_FIXO_INICIAL);

        aluguelRepository.save(aluguel);

        // 7. Notificar
        emailService.enviarEmail(ciclista.getEmail(), "Aluguel Realizado",
                "Sua bicicleta foi liberada! Bom passeio.");

        return new AluguelDTO(aluguel);
    }

    @Override
    @Transactional
    public DevolucaoDTO realizarDevolucao(NovaDevolucaoDTO dto) {
        // 1. Buscar aluguel ativo pela bicicleta
        Aluguel aluguel = aluguelRepository.findByBicicletaIdAndDataHoraDevolucaoIsNull(dto.getIdBicicleta())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum aluguel ativo encontrado para esta bicicleta."));

        // 2. Registrar devolução
        LocalDateTime agora = LocalDateTime.now();
        aluguel.setDataHoraDevolucao(agora);
        aluguel.setTrancaFimId(dto.getIdTranca());

        // 3. Calcular custos
        Double valorExtra = calcularCustoExtra(aluguel.getDataHoraInicio(), agora);

        if (valorExtra > 0) {
            CartaoDeCredito cartao = cartaoRepository.findByCiclistaId(aluguel.getCiclista().getId())
                    .orElseThrow(() -> new ValidacaoException("Erro ao processar pagamento extra: Cartão não encontrado."));

            pagamentoService.realizarCobranca(cartao, valorExtra);
            aluguel.setValorCobrado(aluguel.getValorCobrado() + valorExtra);
        }

        aluguelRepository.save(aluguel);

        // 4. Notificar
        String corpoEmail = "Devolução confirmada em " + agora + ". ";
        if (valorExtra > 0) {
            corpoEmail += "Cobrança extra de R$ " + valorExtra + " realizada.";
        }
        emailService.enviarEmail(aluguel.getCiclista().getEmail(), "Devolução Realizada", corpoEmail);

        return new DevolucaoDTO(aluguel, valorExtra, aluguel.getValorCobrado());
    }

    private Double calcularCustoExtra(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) return 0.0;

        Duration duracao = Duration.between(inicio, fim);
        long horasTotais = duracao.toHours();

        if (horasTotais > 2) {
            long horasExtras = horasTotais - 2;
            if (duracao.toMinutes() % 60 > 0) {
                horasExtras++;
            }
            return horasExtras * CUSTO_HORA_EXTRA;
        }
        return 0.0;
    }
}