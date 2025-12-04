package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.AluguelDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovaDevolucaoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoAluguelDTO;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.Aluguel;
import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;
import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import br.com.bicicletario.ms_aluguel.domain.repository.AluguelRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CartaoDeCreditoRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CiclistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AluguelServiceImplTest {

    @Mock private AluguelRepository aluguelRepository;
    @Mock private CiclistaRepository ciclistaRepository;
    @Mock private CartaoDeCreditoRepository cartaoRepository;
    @Mock private CiclistaService ciclistaService;
    @Mock private EquipamentoService equipamentoService;
    @Mock private PagamentoService pagamentoService;
    @Mock private EmailService emailService;

    @InjectMocks
    private AluguelServiceImpl service;

    private Ciclista ciclista;
    private CartaoDeCredito cartao;
    private Aluguel aluguelAtivo;
    private NovoAluguelDTO novoAluguelDTO;
    private NovaDevolucaoDTO novaDevolucaoDTO;

    @BeforeEach
    void setUp() {
        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setEmail("teste@teste.com");

        cartao = new CartaoDeCredito();
        cartao.setNumero("1111222233334444");
        ciclista.setCartaoDeCredito(cartao);

        novoAluguelDTO = new NovoAluguelDTO();
        novoAluguelDTO.setCiclista(1L);
        novoAluguelDTO.setTrancaInicio(10L);

        aluguelAtivo = new Aluguel();
        aluguelAtivo.setId(50L);
        aluguelAtivo.setCiclista(ciclista);
        aluguelAtivo.setIdBicicleta(100L);
        aluguelAtivo.setDataHoraRetirada(LocalDateTime.now().minusMinutes(30)); // Pegou há 30 min

        novaDevolucaoDTO = new NovaDevolucaoDTO();
        novaDevolucaoDTO.setIdBicicleta(100L);
        novaDevolucaoDTO.setIdTranca(20L);
    }

    // --- TESTES DE ALUGUEL (UC03) ---

    @Test
    void testRealizarAluguel_Sucesso() {
        // GIVEN
        when(ciclistaService.permiteAluguel(1L)).thenReturn(true);
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.of(ciclista));
        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartao));
        when(equipamentoService.recuperarBicicletaPorTranca(10L)).thenReturn(100L);
        when(pagamentoService.validarCartao(any())).thenReturn(true);

        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> {
            Aluguel a = i.getArgument(0);
            a.setId(1L);
            return a;
        });

        // WHEN
        AluguelDTO resultado = service.realizarAluguel(novoAluguelDTO);

        // THEN
        assertNotNull(resultado.getId());
        assertEquals(100L, resultado.getBicicleta());

        verify(equipamentoService).destrancarTranca(10L); // Tranca abriu?
        verify(emailService).enviarEmail(anyString(), anyString(), anyString()); // Email enviado?
    }

    @Test
    void testRealizarAluguel_CiclistaNaoApto() {
        // GIVEN: Ciclista tem pendência
        when(ciclistaService.permiteAluguel(1L)).thenReturn(false);

        // WHEN & THEN
        assertThrows(ValidacaoException.class, () -> service.realizarAluguel(novoAluguelDTO));

        verify(aluguelRepository, never()).save(any());
        verify(equipamentoService, never()).destrancarTranca(anyLong());
    }

    // --- TESTES DE DEVOLUÇÃO (UC04) ---

    @Test
    void testRealizarDevolucao_NoPrazo() {
        // GIVEN: Aluguel de 30 min atrás (dentro das 2h)
        aluguelAtivo.setDataHoraRetirada(LocalDateTime.now().minusMinutes(30));

        when(aluguelRepository.findByBicicletaAndDevolucaoNull(100L)).thenReturn(Optional.of(aluguelAtivo));
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        AluguelDTO resultado = service.realizarDevolucao(novaDevolucaoDTO);

        // THEN
        assertNotNull(resultado.getDataHoraDevolucao());
        assertEquals(10.0, resultado.getValorCobrado()); // Apenas taxa fixa

        // Verifica que NÃO cobrou extra
        verify(pagamentoService, never()).validarCartao(any());
    }

    @Test
    void testRealizarDevolucao_ComAtraso() {
        // GIVEN: Aluguel de 3 horas atrás (180 min)
        // 180 min totais - 120 min franquia = 60 min extra
        // 60 min * 0.50 = R$ 30.00 extra
        // Total esperado: 10.0 + 30.0 = 40.0
        aluguelAtivo.setDataHoraRetirada(LocalDateTime.now().minusMinutes(180));

        when(aluguelRepository.findByBicicletaAndDevolucaoNull(100L)).thenReturn(Optional.of(aluguelAtivo));
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));
        when(pagamentoService.validarCartao(any())).thenReturn(true); // Cobrança extra passa

        // WHEN
        AluguelDTO resultado = service.realizarDevolucao(novaDevolucaoDTO);

        // THEN
        assertEquals(40.0, resultado.getValorCobrado());

        // Verifica se cobrou o extra
        verify(pagamentoService).validarCartao(any());
    }
}