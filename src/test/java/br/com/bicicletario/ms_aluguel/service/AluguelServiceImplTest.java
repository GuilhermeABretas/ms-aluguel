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
        aluguelAtivo.setBicicletaId(100L);
        aluguelAtivo.setDataHoraInicio(LocalDateTime.now().minusMinutes(30));
        aluguelAtivo.setValorCobrado(10.0);

        novaDevolucaoDTO = new NovaDevolucaoDTO();
        novaDevolucaoDTO.setIdBicicleta(100L);
        novaDevolucaoDTO.setIdTranca(20L);
    }

    @Test
    void testRealizarAluguel_Sucesso() {
        when(ciclistaService.permiteAluguel(1L)).thenReturn(true);
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.of(ciclista));
        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartao));
        when(equipamentoService.recuperarBicicletaPorTranca(10L)).thenReturn(100L);
        doNothing().when(pagamentoService).realizarCobranca(any(), any());

        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> {
            Aluguel a = i.getArgument(0);
            a.setId(1L);
            return a;
        });

        service.realizarAluguel(novoAluguelDTO);

        verify(equipamentoService).destrancarTranca(10L);
        verify(emailService).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testRealizarAluguel_CiclistaNaoApto() {
        when(ciclistaService.permiteAluguel(1L)).thenReturn(false);

        assertThrows(ValidacaoException.class, () -> service.realizarAluguel(novoAluguelDTO));

        verify(aluguelRepository, never()).save(any());
        verify(equipamentoService, never()).destrancarTranca(anyLong());
    }

    @Test
    void testRealizarDevolucao_NoPrazo() {
        aluguelAtivo.setDataHoraInicio(LocalDateTime.now().minusMinutes(30));

        when(aluguelRepository.findByBicicletaIdAndDataHoraDevolucaoIsNull(100L)).thenReturn(Optional.of(aluguelAtivo));
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

        service.realizarDevolucao(novaDevolucaoDTO);

        assertNotNull(aluguelAtivo.getDataHoraDevolucao());
        assertEquals(10.0, aluguelAtivo.getValorCobrado());

        verify(pagamentoService, never()).realizarCobranca(any(), any());
    }

    @Test
    void testRealizarDevolucao_ComAtraso() {
        aluguelAtivo.setDataHoraInicio(LocalDateTime.now().minusMinutes(180));

        when(aluguelRepository.findByBicicletaIdAndDataHoraDevolucaoIsNull(100L)).thenReturn(Optional.of(aluguelAtivo));
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));
        when(cartaoRepository.findByCiclistaId(aluguelAtivo.getCiclista().getId())).thenReturn(Optional.of(cartao));

        doNothing().when(pagamentoService).realizarCobranca(any(), eq(5.0));

        service.realizarDevolucao(novaDevolucaoDTO);

        assertEquals(15.0, aluguelAtivo.getValorCobrado());

        verify(pagamentoService).realizarCobranca(any(), eq(5.0));
    }

    @Test
    void testRealizarDevolucao_FalhaNaCobrancaExtra() {
        aluguelAtivo.setDataHoraInicio(LocalDateTime.now().minusMinutes(180));

        when(aluguelRepository.findByBicicletaIdAndDataHoraDevolucaoIsNull(100L)).thenReturn(Optional.of(aluguelAtivo));
        when(cartaoRepository.findByCiclistaId(aluguelAtivo.getCiclista().getId())).thenReturn(Optional.of(cartao));

        // Simula a falha no processamento de pagamento
        doThrow(new ValidacaoException("Pagamento Extra Recusado")).when(pagamentoService).realizarCobranca(any(), anyDouble());

        // Espera-se que a exceção seja propagada
        assertThrows(ValidacaoException.class, () -> service.realizarDevolucao(novaDevolucaoDTO));

        // Verifica se a devolução não foi salva
        verify(aluguelRepository, never()).save(any());
        // Verifica se o email de devolução não foi enviado
        verify(emailService, never()).enviarEmail(anyString(), anyString(), eq("Devolução Realizada"));
    }
}