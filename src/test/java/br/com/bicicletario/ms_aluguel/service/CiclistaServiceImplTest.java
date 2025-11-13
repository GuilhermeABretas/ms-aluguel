package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.*;
import br.com.bicicletario.ms_aluguel.api.exception.RecursoNaoEncontradoException;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.*;
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

    // --- Mocks ---
    @Mock
    private CiclistaRepository ciclistaRepository;
    @Mock
    private CartaoDeCreditoRepository cartaoRepository;
    @Mock
    private PagamentoService pagamentoService;
    @Mock
    private EmailService emailService;

    // --- Service ---
    @InjectMocks
    private CiclistaServiceImpl service;

    // --- Dados de Teste ---
    private Ciclista ciclistaTeste;
    private CartaoDeCredito cartaoTeste;
    private NovoCartaoDeCreditoDTO novoCartaoDTO;
    private NovoCiclistaDTO novoCiclistaDTO;
    private final Long ID_EXISTENTE = 1L;
    private final Long ID_INEXISTENTE = 99L;
    private final String EMAIL_TESTE = "ciclista@teste.com";
    private final String CPF_TESTE = "12345678900";

    @BeforeEach
    void setUp() {
        // --- Ciclista e Cartão (para UC02 e UC07) ---
        ciclistaTeste = new Ciclista();
        ciclistaTeste.setId(ID_EXISTENTE);
        ciclistaTeste.setNome("Ciclista Teste");
        ciclistaTeste.setEmail(EMAIL_TESTE);
        ciclistaTeste.setSenha("senha123");
        ciclistaTeste.setNascimento(LocalDate.of(1990, 1, 1));
        ciclistaTeste.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclistaTeste.setCpf(CPF_TESTE);
        ciclistaTeste.setStatus(StatusCiclista.AGUARDANDO_CONFIRMACAO);

        cartaoTeste = new CartaoDeCredito();
        cartaoTeste.setId(10L);
        cartaoTeste.setNomeTitular("Ciclista Teste");
        cartaoTeste.setNumero("1111222233334444");
        cartaoTeste.setValidade(LocalDate.now().plusYears(1));
        cartaoTeste.setCvv("123");
        cartaoTeste.setCiclista(ciclistaTeste);
        ciclistaTeste.setCartaoDeCredito(cartaoTeste);

        novoCartaoDTO = new NovoCartaoDeCreditoDTO();
        novoCartaoDTO.setNomeTitular("Nome Novo");
        novoCartaoDTO.setNumero("5555666677778888");
        novoCartaoDTO.setValidade(LocalDate.now().plusYears(2));
        novoCartaoDTO.setCvv("321");

        // --- DTO Novo Ciclista (para UC01) ---
        novoCiclistaDTO = new NovoCiclistaDTO();
        novoCiclistaDTO.setNome("Novo Ciclista");
        novoCiclistaDTO.setNascimento(LocalDate.of(1995, 5, 10));
        novoCiclistaDTO.setCpf(CPF_TESTE);
        novoCiclistaDTO.setNacionalidade(Nacionalidade.BRASILEIRO);
        novoCiclistaDTO.setEmail(EMAIL_TESTE);
        novoCiclistaDTO.setSenha("senhaNova123");
        novoCiclistaDTO.setUrlFotoDocumento("http://foto.com/doc.png");
        novoCiclistaDTO.setMeioDePagamento(novoCartaoDTO);
    }

    // --- Testes UC01: Cadastrar Ciclista ---

    @Test
    void testCadastrarCiclista_Sucesso() {
        // Configuração do Mock:
        // 1. Validações de duplicidade (passam)
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        when(ciclistaRepository.findByCpf(CPF_TESTE)).thenReturn(Optional.empty());
        // 2. Validação de pagamento (passa)
        when(pagamentoService.validarCartao(novoCartaoDTO)).thenReturn(true);
        // 3. Mock do Save (retorna o ciclista com ID)
        when(ciclistaRepository.save(any(Ciclista.class))).thenAnswer(invocation -> {
            Ciclista ciclistaSalvo = invocation.getArgument(0);
            ciclistaSalvo.setId(2L); // Simula o ID gerado pelo banco
            // Verifica se o status inicial está correto
            assertEquals(StatusCiclista.AGUARDANDO_CONFIRMACAO, ciclistaSalvo.getStatus());
            // Verifica se o cartão foi associado
            assertNotNull(ciclistaSalvo.getCartaoDeCredito());
            assertEquals("5555666677778888", ciclistaSalvo.getCartaoDeCredito().getNumero());
            return ciclistaSalvo;
        });
        // 4. Mock do Email (não faz nada)
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        // Execução
        CiclistaDTO resultado = service.cadastrarCiclista(novoCiclistaDTO);

        // Verificação
        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals("Novo Ciclista", resultado.getNome());
        assertEquals(StatusCiclista.AGUARDANDO_CONFIRMACAO, resultado.getStatus());

        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
        verify(emailService, times(1)).enviarEmail(eq(EMAIL_TESTE), eq("Confirme seu cadastro"), anyString());
    }

    @Test
    void testCadastrarCiclista_EmailDuplicado() {
        // Configuração do Mock:
        // 1. Validação de email falha
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.of(ciclistaTeste));

        // Verificação
        ValidacaoException ex = assertThrows(
                ValidacaoException.class,
                () -> service.cadastrarCiclista(novoCiclistaDTO) // Execução
        );

        assertEquals("Email já cadastrado.", ex.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    void testCadastrarCiclista_CpfDuplicado() {
        // Configuração do Mock:
        // 1. Validação de email passa
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        // 2. Validação de CPF falha
        when(ciclistaRepository.findByCpf(CPF_TESTE)).thenReturn(Optional.of(ciclistaTeste));

        // Verificação
        ValidacaoException ex = assertThrows(
                ValidacaoException.class,
                () -> service.cadastrarCiclista(novoCiclistaDTO) // Execução
        );

        assertEquals("CPF já cadastrado.", ex.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    void testCadastrarCiclista_PagamentoReprovado() {
        // Configuração do Mock:
        // 1. Validações de duplicidade (passam)
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        when(ciclistaRepository.findByCpf(CPF_TESTE)).thenReturn(Optional.empty());
        // 2. Validação de pagamento (falha)
        when(pagamentoService.validarCartao(novoCartaoDTO))
                .thenThrow(new ValidacaoException("Cartão de crédito reprovado pela administradora."));

        // Verificação
        ValidacaoException ex = assertThrows(
                ValidacaoException.class,
                () -> service.cadastrarCiclista(novoCiclistaDTO) // Execução
        );

        assertEquals("Cartão de crédito reprovado pela administradora.", ex.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
        verify(emailService, never()).enviarEmail(any(), any(), any());
    }

    // --- Testes UC01: Existe Email ---

    @Test
    void testExisteEmail_True() {
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.of(ciclistaTeste));
        boolean resultado = service.existeEmail(EMAIL_TESTE);
        assertTrue(resultado);
    }

    @Test
    void testExisteEmail_False() {
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        boolean resultado = service.existeEmail(EMAIL_TESTE);
        assertFalse(resultado);
    }

    // --- Testes UC02: Ativar Ciclista (Já existentes) ---

    @Test
    void testAtivarCiclista_Sucesso() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(ciclistaRepository.save(any(Ciclista.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CiclistaDTO resultado = service.ativarCiclista(ID_EXISTENTE);

        assertEquals(StatusCiclista.ATIVO, resultado.getStatus());
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
    }

    // ... (Os outros testes de AtivarCiclista_NaoEncontrado e AtivarCiclista_StatusInvalido
    //      que já escrevemos antes também estão aqui e passam)

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

    // --- Testes UC07: Buscar Cartão (Já existentes) ---

    @Test
    void testBuscarCartao_Sucesso() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.of(cartaoTeste));

        CartaoDeCreditoDTO resultado = service.buscarCartao(ID_EXISTENTE);

        assertNotNull(resultado);
        assertEquals("1111222233334444", resultado.getNumero());
    }

    // ... (Os outros testes de BuscarCartao_... que já escrevemos antes também estão aqui)

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

    // --- Testes UC07: Atualizar Cartão (Já existentes) ---

    @Test
    void testAtualizarCartao_Sucesso() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(pagamentoService.validarCartao(novoCartaoDTO)).thenReturn(true);
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.of(cartaoTeste));
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenReturn(cartaoTeste);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));

        verify(cartaoRepository, times(1)).save(any(CartaoDeCredito.class));
        verify(emailService, times(1)).enviarEmail(any(), any(), any());
    }

    // ... (Os outros testes de AtualizarCartao_... que já escrevemos antes também estão aqui)

    @Test
    void testAtualizarCartao_CiclistaNaoEncontrado() {
        when(ciclistaRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizarCartao(ID_INEXISTENTE, novoCartaoDTO));
    }

    @Test
    void testAtualizarCartao_PagamentoReprovado() {
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        when(pagamentoService.validarCartao(novoCartaoDTO))
                .thenThrow(new ValidacaoException("Cartão reprovado."));

        assertThrows(ValidacaoException.class, () -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));
    }
    @Test
    void testAtualizarCartao_Sucesso_CiclistaSemCartao() {
        // Cenário: Ciclista existe, mas não tem cartão.
        // O método deve criar um cartão novo.

        // Configuração do Mock:
        // 1. Encontra o ciclista
        when(ciclistaRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(ciclistaTeste));
        // 2. Mock do Pagamento (retorna true)
        when(pagamentoService.validarCartao(novoCartaoDTO)).thenReturn(true);
        // 3. NÃO encontra cartão antigo (retorna vazio)
        when(cartaoRepository.findByCiclistaId(ID_EXISTENTE)).thenReturn(Optional.empty());
        // 4. Mock do Save
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenReturn(new CartaoDeCredito()); // Retorna um cartão novo
        // 5. Mock do Email
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        // Execução
        assertDoesNotThrow(() -> service.atualizarCartao(ID_EXISTENTE, novoCartaoDTO));

        // Verificação
        verify(pagamentoService, times(1)).validarCartao(novoCartaoDTO);
        // Verifica se o save foi chamado (para criar o novo cartão)
        verify(cartaoRepository, times(1)).save(any(CartaoDeCredito.class));
        verify(emailService, times(1)).enviarEmail(any(), any(), any());
    }
    // NOVO TESTE 1: Caminho feliz do Estrangeiro (Cobre o Bloco 1)
    @Test
    void testCadastrarCiclista_Sucesso_Estrangeiro() {
        // --- Configuração Específica ---
        NovoPassaporteDTO passaporteDTO = new NovoPassaporteDTO();
        passaporteDTO.setNumero("G12345678");
        passaporteDTO.setValidade(LocalDate.now().plusYears(5));
        passaporteDTO.setPais("US");

        novoCiclistaDTO.setNacionalidade(Nacionalidade.ESTRANGEIRO);
        novoCiclistaDTO.setPassaporte(passaporteDTO);
        novoCiclistaDTO.setCpf(null); // Estrangeiro não tem CPF

        // --- Mocks ---
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        when(pagamentoService.validarCartao(novoCartaoDTO)).thenReturn(true);
        when(ciclistaRepository.save(any(Ciclista.class))).thenAnswer(invocation -> {
            Ciclista ciclistaSalvo = invocation.getArgument(0);
            ciclistaSalvo.setId(2L);
            // Verifica se o passaporte FOI mapeado
            assertNotNull(ciclistaSalvo.getPassaporte());
            assertEquals("G12345678", ciclistaSalvo.getPassaporte().getNumero());
            return ciclistaSalvo;
        });
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        // --- Execução ---
        CiclistaDTO resultado = service.cadastrarCiclista(novoCiclistaDTO);

        // --- Verificação ---
        assertNotNull(resultado);
        assertEquals(Nacionalidade.ESTRANGEIRO, novoCiclistaDTO.getNacionalidade());
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
    }

    // NOVO TESTE 2: Caminho triste do Estrangeiro (Cobre o Bloco 2)
    @Test
    void testCadastrarCiclista_Falha_EstrangeiroSemPassaporte() {
        // --- Configuração Específica ---
        novoCiclistaDTO.setNacionalidade(Nacionalidade.ESTRANGEIRO);
        novoCiclistaDTO.setPassaporte(null); // <-- O erro (passaporte nulo)
        novoCiclistaDTO.setCpf(null);

        // --- Mocks ---
        when(ciclistaRepository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());

        // --- Execução e Verificação ---
        ValidacaoException ex = assertThrows(
                ValidacaoException.class,
                () -> service.cadastrarCiclista(novoCiclistaDTO)
        );

        // Verifica se a exceção correta foi lançada
        assertEquals("Dados do passaporte são obrigatórios para estrangeiros.", ex.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }
}