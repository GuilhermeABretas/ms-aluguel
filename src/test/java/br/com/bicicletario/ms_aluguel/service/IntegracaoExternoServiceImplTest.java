package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.client.EquipamentoClient;
import br.com.bicicletario.ms_aluguel.api.client.ExternoClient;
import br.com.bicicletario.ms_aluguel.api.dto.BicicletaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.externo.CobrancaRequest;
import br.com.bicicletario.ms_aluguel.api.dto.externo.ValidacaoCartaoRequest;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;
import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegracaoExternoServiceImplTest {

    @Mock
    private EquipamentoClient equipamentoClient;
    @Mock
    private ExternoClient externoClient;

    @InjectMocks
    private IntegracaoExternoServiceImpl service;

    private NovoCartaoDeCreditoDTO cartaoDTO;
    private CartaoDeCredito cartaoEntidade;
    private BicicletaDTO bicicletaDTO;

    @BeforeEach
    void setUp() {
        cartaoDTO = new NovoCartaoDeCreditoDTO(
                "Titular Teste",
                "1111222233334444",
                LocalDate.now().plusYears(1),
                "123"
        );

        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);

        cartaoEntidade = new CartaoDeCredito();
        cartaoEntidade.setCiclista(ciclista);
        cartaoEntidade.setNumero("1111222233334444");

        bicicletaDTO = new BicicletaDTO(
                100L,
                "Caloi",
                "Urbana",
                "1A2B3C",
                "NOVA"
        );
    }



    @Test
    void testRecuperarBicicletaPorTranca_Sucesso() {
        when(equipamentoClient.recuperarBicicletaPorTranca(1L)).thenReturn(bicicletaDTO);
        Long resultado = service.recuperarBicicletaPorTranca(1L);
        assertEquals(100L, resultado);
    }

    @Test
    void testRecuperarBicicletaPorTranca_NaoEncontrada_RetornaNull() {
        when(equipamentoClient.recuperarBicicletaPorTranca(1L)).thenReturn(null);
        Long resultado = service.recuperarBicicletaPorTranca(1L);
        assertNull(resultado);
    }

    @Test
    void testRecuperarBicicletaPorTranca_FalhaIntegracao_RetornaNull() {
        // Simula qualquer erro de rede ou HTTP do Feign
        when(equipamentoClient.recuperarBicicletaPorTranca(any())).thenThrow(FeignException.class);
        Long resultado = service.recuperarBicicletaPorTranca(1L);
        assertNull(resultado);
    }

    @Test
    void testDestrancarTranca_Sucesso() {
        service.destrancarTranca(1L);
        verify(equipamentoClient, times(1)).destrancarTranca(1L);
    }



    @Test
    void testValidarCartao_Sucesso() {

        assertDoesNotThrow(() -> service.validarCartao(cartaoDTO));
        verify(externoClient, times(1)).validarCartao(any(ValidacaoCartaoRequest.class));
    }

    @Test
    void testValidarCartao_Falha_LancaValidacaoException() {

        doThrow(FeignException.class).when(externoClient).validarCartao(any(ValidacaoCartaoRequest.class));
        ValidacaoException ex = assertThrows(ValidacaoException.class, () -> service.validarCartao(cartaoDTO));
        assertEquals("Cartão inválido ou reprovado pela operadora.", ex.getMessage());
    }



    @Test
    void testRealizarCobranca_Sucesso() {

        service.realizarCobranca(cartaoEntidade, 50.0);
        verify(externoClient, times(1)).realizarCobranca(any(CobrancaRequest.class));
    }

    @Test
    void testRealizarCobranca_Falha_LancaValidacaoException() {
        doThrow(FeignException.class).when(externoClient).realizarCobranca(any(CobrancaRequest.class));
        ValidacaoException ex = assertThrows(ValidacaoException.class, () -> service.realizarCobranca(cartaoEntidade, 50.0));
        assertEquals("Falha ao realizar cobrança no cartão.", ex.getMessage());
    }


    @Test
    void testEnviarEmail_Sucesso() {

        assertDoesNotThrow(() -> service.enviarEmail("a@a.com", "Assunto", "Mensagem"));
        verify(externoClient, times(1)).enviarEmail(any());
    }

    @Test
    void testEnviarEmail_Falha_NaoLancaExcecao() {

        doThrow(FeignException.class).when(externoClient).enviarEmail(any());
        assertDoesNotThrow(() -> service.enviarEmail("a@a.com", "Assunto", "Mensagem"));
    }
}