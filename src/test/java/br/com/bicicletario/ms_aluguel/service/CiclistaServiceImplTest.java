package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.*;
import br.com.bicicletario.ms_aluguel.api.exception.RecursoNaoEncontradoException;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.*;
import br.com.bicicletario.ms_aluguel.domain.repository.AluguelRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CartaoDeCreditoRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CiclistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CiclistaServiceImplTest {

    @Mock private CiclistaRepository ciclistaRepository;
    @Mock private CartaoDeCreditoRepository cartaoRepository;
    @Mock private AluguelRepository aluguelRepository; // Novo Mock para o Helper
    @Mock private PagamentoService pagamentoService;
    @Mock private EmailService emailService;

    @InjectMocks
    private CiclistaServiceImpl service;

    // Dados de Teste
    private Ciclista ciclistaTeste;
    private CartaoDeCredito cartaoTeste;
    private NovoCartaoDeCreditoDTO novoCartaoDTO;
    private NovoCiclistaDTO novoCiclistaDTO;
    private final Long ID_EXISTENTE = 1L;
    private final Long ID_INEXISTENTE = 99L;
    private final String EMAIL_TESTE = "ciclista@teste.com";
    private final String CPF_TESTE = "11122233344";

    @BeforeEach
    void setUp() {
        // Ciclista Base
        ciclistaTeste = new Ciclista();
        ciclistaTeste.setId(ID_EXISTENTE);
        ciclistaTeste.setNome("Ciclista Teste");
        ciclistaTeste.setEmail(EMAIL_TESTE);
        ciclistaTeste.setSenha("senha123");
        ciclistaTeste.setNascimento(LocalDate.of(1990, 1, 1));
        ciclistaTeste.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclistaTeste.setCpf(CPF_TESTE);
        ciclistaTeste.setStatus(StatusCiclista.AGUARDANDO_CONFIRMACAO);
        ciclistaTeste.setUrlFotoDocumento("http://foto.com");

        // Cartão Base
        cartaoTeste = new CartaoDeCredito();
        cartaoTeste.setId(10L);
        cartaoTeste.setNomeTitular("Ciclista Teste");
        cartaoTeste.setNumero("1111222233334444");
        cartaoTeste.setCiclista(ciclistaTeste);
        ciclistaTeste.setCartaoDeCredito(cartaoTeste);

        // DTOs
        novoCartaoDTO = new NovoCartaoDeCreditoDTO();
        novoCartaoDTO.setNomeTitular("Nome Novo");
        novoCartaoDTO.setNumero("5555666677778888");
        novoCartaoDTO.setValidade(LocalDate.now().plusYears(2));
        novoCartaoDTO.setCvv("321");

        novoCiclistaDTO = new NovoCiclistaDTO();
        novoCiclistaDTO.setNome("Novo Ciclista");
        novoCiclistaDTO.setEmail(EMAIL_TESTE);
        novoCiclistaDTO.setCpf(CPF_TESTE);
        novoCiclistaDTO.setNacionalidade(Nacionalidade.BRASILEIRO);
        novoCiclistaDTO.setSenha("senhaNova");
        novoCiclistaDTO.setNascimento(LocalDate.of(2000, 1, 1));
        novoCiclistaDTO.setUrlFotoDocumento("http://foto.com");
        novoCiclistaDTO.setMeioDePagamento(novoCartaoDTO);
    }

    // =========================================================================
    // TESTES UC01 (CADASTRO)
    // =========================================================================
    @Test
    void testCadastrarCiclista_Sucesso() {
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        when(ciclistaRepository.findByCpf(CPF_TESTE)).thenReturn(Optional.empty());
        when(pagamentoService.validarCartao(any())).thenReturn(true);
        when(ciclistaRepository.save(any(Ciclista.class))).thenAnswer(i -> {
            Ciclista c = i.getArgument(0);
            c.setId(100L);
            return c;
        });
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        CiclistaDTO resultado = service.cadastrarCiclista(novoCiclistaDTO);

        assertEquals(100L, resultado.getId());
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
    }

    @Test
    void testCadastrarCiclista_EmailDuplicado() {
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.of(ciclistaTeste));
        ValidacaoException ex = assertThrows(ValidacaoException.class, () -> service.cadastrarCiclista(novoCiclistaDTO));
        assertEquals("Email já cadastrado.", ex.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    void testCadastrarCiclista_CpfDuplicado() {
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        when(ciclistaRepository.findByCpf(CPF_TESTE)).thenReturn(Optional.of(ciclistaTeste));
        ValidacaoException ex = assertThrows(ValidacaoException.class, () -> service.cadastrarCiclista(novoCiclistaDTO));
        assertEquals("CPF já cadastrado.", ex.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    void testCadastrarCiclista_PagamentoReprovado() {
        when(ciclistaRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(ciclistaRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(pagamentoService.validarCartao(any())).thenThrow(new ValidacaoException("Cartão Recusado"));

        assertThrows(ValidacaoException.class, () -> service.cadastrarCiclista(novoCiclistaDTO));
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    void testExisteEmail_True() {
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.of(ciclistaTeste));
        assertTrue(service.existeEmail(EMAIL_TESTE));
    }

    @Test
    void testExisteEmail_False() {
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        assertFalse(service.existeEmail(EMAIL_TESTE));
    }

    @Test
    void testCadastrarCiclista_Sucesso_Estrangeiro() {
        NovoPassaporteDTO passaporteDTO = new NovoPassaporteDTO();
        passaporteDTO.setNumero("G12345678");
        passaporteDTO.setValidade(LocalDate.now().plusYears(5));
        passaporteDTO.setPais("US");
        novoCiclistaDTO.setNacionalidade(Nacionalidade.ESTRANGEIRO);
        novoCiclistaDTO.setPassaporte(passaporteDTO);
        novoCiclistaDTO.setCpf(null);

        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        when(pagamentoService.validarCartao(any())).thenReturn(true);
        when(ciclistaRepository.save(any(Ciclista.class))).thenAnswer(i -> i.getArgument(0));

        CiclistaDTO resultado = service.cadastrarCiclista(novoCiclistaDTO);
        assertEquals(Nacionalidade.ESTRANGEIRO, resultado.getNacionalidade());
    }

    @Test
    void testCadastrarCiclista_Falha_EstrangeiroSemPassaporte() {
        novoCiclistaDTO.setNacionalidade(Nacionalidade.ESTRANGEIRO);
        novoCiclistaDTO.setPassaporte(null);
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> service.cadastrarCiclista(novoCiclistaDTO));
    }

    // =========================================================================
    // TESTES UC02 (ATIVAR)
    // =========================================================================
    @Test
    void testAtivarCiclista_Sucesso() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(ciclistaRepository.save(any(Ciclista.class))).thenAnswer(i -> i.getArgument(0));

        CiclistaDTO resultado = service.ativarCiclista(ID_EXISTENTE);
        assertEquals(StatusCiclista.ATIVO, resultado.getStatus());
    }

    @Test
    void testAtivarCiclista_NaoEncontrado() {
        when(ciclistaRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.ativarCiclista(ID_INEXISTENTE));
    }

    @Test
    void testAtivarCiclista_StatusInvalido() {
        ciclistaTeste.setStatus(StatusCiclista.ATIVO);
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        assertThrows(ValidacaoException.class, () -> service.ativarCiclista(ID_EXISTENTE));
    }

    // =========================================================================
    // TESTES UC06 (BUSCAR E ALTERAR)
    // =========================================================================
    @Test
    void testBuscarPorId_Sucesso() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        CiclistaDTO resultado = service.buscarPorId(ID_EXISTENTE);
        assertEquals("Ciclista Teste", resultado.getNome());
    }

    @Test
    void testAtualizarCiclista_Sucesso() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(ciclistaRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(ciclistaRepository.save(any(Ciclista.class))).thenAnswer(i -> i.getArgument(0));

        novoCiclistaDTO.setNome("Nome Alterado");
        CiclistaDTO resultado = service.atualizarCiclista(ID_EXISTENTE, novoCiclistaDTO);

        assertEquals("Nome Alterado", resultado.getNome());
        verify(ciclistaRepository).save(ciclistaTeste);
    }

    @Test
    void testAtualizarCiclista_EmailDuplicado() {
        Ciclista outro = new Ciclista();
        outro.setId(555L);
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(ciclistaRepository.findByEmail(anyString())).thenReturn(Optional.of(outro));

        assertThrows(ValidacaoException.class, () -> service.atualizarCiclista(ID_EXISTENTE, novoCiclistaDTO));
    }

    // =========================================================================
    // TESTES HELPERS (PERMITE ALUGUEL) - NOVOS!
    // =========================================================================
    @Test
    void testPermiteAluguel_True() {
        ciclistaTeste.setStatus(StatusCiclista.ATIVO);
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(aluguelRepository.findByCiclistaIdAndDataHoraDevolucaoIsNull(ID_EXISTENTE)).thenReturn(Optional.empty());

        assertTrue(service.permiteAluguel(ID_EXISTENTE));
    }

    @Test
    void testPermiteAluguel_False_Inativo() {
        ciclistaTeste.setStatus(StatusCiclista.AGUARDANDO_CONFIRMACAO);
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        assertFalse(service.permiteAluguel(ID_EXISTENTE));
    }

    @Test
    void testPermiteAluguel_False_TemAluguel() {
        ciclistaTeste.setStatus(StatusCiclista.ATIVO);
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(aluguelRepository.findByCiclistaIdAndDataHoraDevolucaoIsNull(ID_EXISTENTE)).thenReturn(Optional.of(new Aluguel()));

        assertFalse(service.permiteAluguel(ID_EXISTENTE));
    }

    // =========================================================================
    // TESTES UC07 (CARTÃO)
    // =========================================================================
    @Test
    void testBuscarCartao_Sucesso() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.of(cartaoTeste));
        assertNotNull(service.buscarCartao(ID_EXISTENTE));
    }

    @Test
    void testBuscarCartao_CiclistaNaoEncontrado() {
        when(ciclistaRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarCartao(ID_INEXISTENTE));
    }

    @Test
    void testBuscarCartao_CartaoNaoEncontrado() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarCartao(ID_EXISTENTE));
    }

    @Test
    void testAtualizarCartao_Sucesso() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(pagamentoService.validarCartao(any())).thenReturn(true);
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.of(cartaoTeste));
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenReturn(cartaoTeste);

        assertDoesNotThrow(() -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));
        verify(cartaoRepository).save(any(CartaoDeCredito.class));
    }

    @Test
    void testAtualizarCartao_PagamentoReprovado() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(pagamentoService.validarCartao(any())).thenThrow(new ValidacaoException("Cartão Recusado"));

        assertThrows(ValidacaoException.class, () -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));
        verify(cartaoRepository, never()).save(any(CartaoDeCredito.class));
    }

    @Test
    void testAtualizarCartao_CiclistaSemCartao() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(pagamentoService.validarCartao(any())).thenReturn(true);
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.empty()); // Não tem cartão
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenReturn(new CartaoDeCredito()); // Cria novo

        assertDoesNotThrow(() -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));
        verify(cartaoRepository).save(any(CartaoDeCredito.class));
    }
}