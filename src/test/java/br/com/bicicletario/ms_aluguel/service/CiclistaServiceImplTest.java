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
    @Mock private AluguelRepository aluguelRepository;
    @Mock private PagamentoService pagamentoService;
    @Mock private EmailService emailService;

    @InjectMocks
    private CiclistaServiceImpl service;

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

        cartaoTeste = new CartaoDeCredito();
        cartaoTeste.setId(10L);
        cartaoTeste.setNomeTitular("Ciclista Teste");
        cartaoTeste.setNumero("1111222233334444");
        cartaoTeste.setCiclista(ciclistaTeste);
        ciclistaTeste.setCartaoDeCredito(cartaoTeste);

        novoCartaoDTO = new NovoCartaoDeCreditoDTO();
        novoCartaoDTO.setNomeTitular("Nome Novo");
        novoCartaoDTO.setNumero("5555666677778888");
        novoCartaoDTO.setValidade(LocalDate.now().plusYears(2));
        novoCartaoDTO.setCvv("321");

        NovoCiclistaDTO.DadosCiclista dadosCiclista = new NovoCiclistaDTO.DadosCiclista();
        dadosCiclista.setNome("Novo Ciclista");
        dadosCiclista.setEmail(EMAIL_TESTE);
        dadosCiclista.setCpf(CPF_TESTE);
        dadosCiclista.setNacionalidade(Nacionalidade.BRASILEIRO);
        dadosCiclista.setSenha("senhaNova");
        dadosCiclista.setNascimento(LocalDate.of(2000, 1, 1));
        dadosCiclista.setUrlFotoDocumento("http://foto.com");

        novoCiclistaDTO = new NovoCiclistaDTO();
        novoCiclistaDTO.setCiclista(dadosCiclista);
        novoCiclistaDTO.setMeioDePagamento(novoCartaoDTO);
    }

    @Test
    void testCadastrarCiclista_Sucesso() {
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        when(ciclistaRepository.findByCpf(CPF_TESTE)).thenReturn(Optional.empty());
        doNothing().when(pagamentoService).validarCartao(any());
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
        doThrow(new ValidacaoException("Cartão Recusado")).when(pagamentoService).validarCartao(any());

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
        novoCiclistaDTO.getCiclista().setNacionalidade(Nacionalidade.ESTRANGEIRO);
        novoCiclistaDTO.getCiclista().setPassaporte(passaporteDTO);
        novoCiclistaDTO.getCiclista().setCpf(null);

        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        doNothing().when(pagamentoService).validarCartao(any());
        when(ciclistaRepository.save(any(Ciclista.class))).thenAnswer(i -> i.getArgument(0));

        CiclistaDTO resultado = service.cadastrarCiclista(novoCiclistaDTO);
        assertEquals(Nacionalidade.ESTRANGEIRO, resultado.getNacionalidade());
    }

    @Test
    void testCadastrarCiclista_Falha_EstrangeiroSemPassaporte() {
        novoCiclistaDTO.getCiclista().setNacionalidade(Nacionalidade.ESTRANGEIRO);
        novoCiclistaDTO.getCiclista().setPassaporte(null);
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> service.cadastrarCiclista(novoCiclistaDTO));
    }

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
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        novoCiclistaDTO.getCiclista().setNome("Nome Alterado");
        CiclistaDTO resultado = service.atualizarCiclista(ID_EXISTENTE, novoCiclistaDTO);

        assertEquals("Nome Alterado", resultado.getNome());
        verify(ciclistaRepository).save(ciclistaTeste);
        verify(emailService, times(1)).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testAtualizarCiclista_EmailDuplicado() {
        Ciclista outro = new Ciclista();
        outro.setId(555L);
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(ciclistaRepository.findByEmail(anyString())).thenReturn(Optional.of(outro));

        assertThrows(ValidacaoException.class, () -> service.atualizarCiclista(ID_EXISTENTE, novoCiclistaDTO));
    }

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
        doNothing().when(pagamentoService).validarCartao(any());
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.of(cartaoTeste));
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenReturn(cartaoTeste);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));
        verify(cartaoRepository).save(any(CartaoDeCredito.class));
        verify(emailService, times(1)).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testAtualizarCartao_PagamentoReprovado() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        doThrow(new ValidacaoException("Cartão Recusado")).when(pagamentoService).validarCartao(any());

        assertThrows(ValidacaoException.class, () -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));
        verify(cartaoRepository, never()).save(any(CartaoDeCredito.class));
    }

    @Test
    void testAtualizarCartao_CiclistaSemCartao() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        doNothing().when(pagamentoService).validarCartao(any());
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.empty());
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenReturn(new CartaoDeCredito());
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));
        verify(cartaoRepository).save(any(CartaoDeCredito.class));
    }
}