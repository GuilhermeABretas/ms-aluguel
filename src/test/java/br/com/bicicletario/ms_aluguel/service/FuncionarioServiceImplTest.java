package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.FuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoFuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.exception.RecursoNaoEncontradoException;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.Funcao;
import br.com.bicicletario.ms_aluguel.domain.model.Funcionario;
import br.com.bicicletario.ms_aluguel.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Habilita o Mockito para esta classe de teste
@ExtendWith(MockitoExtension.class)
class FuncionarioServiceImplTest {

    // Cria um "Mock" (simulação) do Repository
    @Mock
    private FuncionarioRepository repository;

    // Injeta os Mocks (como o repository) na nossa classe de serviço
    @InjectMocks
    private FuncionarioServiceImpl service;

    // --- Variáveis de Teste ---
    private Funcionario funcionarioTeste;
    private NovoFuncionarioDTO novoFuncionarioDTO;
    private final Long ID_EXISTENTE = 1L;
    private final Long ID_INEXISTENTE = 99L;
    private final String CPF_TESTE = "12345678900";
    private final String EMAIL_TESTE = "teste@email.com";

    /**
     * Roda antes de CADA teste (@Test)
     */
    @BeforeEach
    void setUp() {
        // Cria um funcionário padrão para os testes
        funcionarioTeste = new Funcionario(
                ID_EXISTENTE,
                "Funcionario Teste",
                EMAIL_TESTE,
                "senha123",
                30,
                CPF_TESTE,
                Funcao.ADMINISTRATIVO
        );

        // Cria um DTO padrão para os testes
        novoFuncionarioDTO = new NovoFuncionarioDTO();
        novoFuncionarioDTO.setNome("Funcionario Teste");
        novoFuncionarioDTO.setEmail(EMAIL_TESTE);
        novoFuncionarioDTO.setSenha("senha123");
        novoFuncionarioDTO.setIdade(30);
        novoFuncionarioDTO.setCpf(CPF_TESTE);
        novoFuncionarioDTO.setFuncao(Funcao.ADMINISTRATIVO);
    }

    // --- Testes de Listagem e Busca ---

    @Test
    void testListarTodos() {
        // Configuração do Mock:
        // QUANDO o repository.findAll() for chamado, ENTÃO retorne uma lista com nosso funcionário
        when(repository.findAll()).thenReturn(List.of(funcionarioTeste));

        // Execução
        List<FuncionarioDTO> resultado = service.listarTodos();

        // Verificação
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(EMAIL_TESTE, resultado.get(0).getEmail());
        // Verifica se o repository foi chamado exatamente 1 vez
        verify(repository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Sucesso() {
        // Configuração do Mock:
        when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(funcionarioTeste));

        // Execução
        FuncionarioDTO resultado = service.buscarPorId(ID_EXISTENTE);

        // Verificação
        assertNotNull(resultado);
        assertEquals(ID_EXISTENTE, resultado.getMatricula());
        verify(repository, times(1)).findById(ID_EXISTENTE);
    }

    @Test
    void testBuscarPorId_NaoEncontrado() {
        // Configuração do Mock:
        when(repository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        // Verificação
        // Verifica se o service lança a exceção correta
        RecursoNaoEncontradoException excecao = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(ID_INEXISTENTE) // Execução
        );

        assertEquals("Funcionário não encontrado com ID: " + ID_INEXISTENTE, excecao.getMessage());
        verify(repository, times(1)).findById(ID_INEXISTENTE);
    }

    // --- Testes de Criação (Salvar) ---

    @Test
    void testSalvar_Sucesso() {
        // Configuração do Mock:
        // Validações de duplicidade passam (retornam vazio)
        when(repository.findByCpf(CPF_TESTE)).thenReturn(Optional.empty());
        when(repository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        // Simula o salvamento (retorna o funcionário com ID)
        when(repository.save(any(Funcionario.class))).thenReturn(funcionarioTeste);

        // Execução
        FuncionarioDTO resultado = service.salvar(novoFuncionarioDTO);

        // Verificação
        assertNotNull(resultado);
        assertEquals(ID_EXISTENTE, resultado.getMatricula());
        verify(repository, times(1)).save(any(Funcionario.class));
    }

    @Test
    void testSalvar_CpfDuplicado() {
        // Configuração do Mock:
        // Validação de CPF falha (encontra um funcionário)
        when(repository.findByCpf(CPF_TESTE)).thenReturn(Optional.of(funcionarioTeste));

        // Verificação
        ValidacaoException excecao = assertThrows(
                ValidacaoException.class,
                () -> service.salvar(novoFuncionarioDTO) // Execução
        );

        assertEquals("CPF já cadastrado.", excecao.getMessage());
        // Garante que o service NUNCA chamou o save()
        verify(repository, never()).save(any(Funcionario.class));
    }

    @Test
    void testSalvar_EmailDuplicado() {
        // Configuração do Mock:
        // Validação de CPF passa
        when(repository.findByCpf(CPF_TESTE)).thenReturn(Optional.empty());
        // Validação de Email falha
        when(repository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.of(funcionarioTeste));

        // Verificação
        ValidacaoException excecao = assertThrows(
                ValidacaoException.class,
                () -> service.salvar(novoFuncionarioDTO) // Execução
        );

        assertEquals("Email já cadastrado.", excecao.getMessage());
        verify(repository, never()).save(any(Funcionario.class));
    }

    // --- Testes de Atualização ---

    @Test
    void testAtualizar_Sucesso() {
        // DTO com dados atualizados (email diferente)
        NovoFuncionarioDTO dtoAtualizado = new NovoFuncionarioDTO();
        dtoAtualizado.setNome("Nome Atualizado");
        dtoAtualizado.setEmail("novoemail@email.com"); // Email mudou
        dtoAtualizado.setSenha("novaSenha");
        dtoAtualizado.setIdade(31);
        dtoAtualizado.setCpf(CPF_TESTE); // CPF (não pode mudar)
        dtoAtualizado.setFuncao(Funcao.REPARADOR);

        // Configuração do Mock:
        // 1. Encontra o funcionário original
        when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(funcionarioTeste));
        // 2. Validação de CPF (encontra ele mesmo, o que é permitido)
        when(repository.findByCpf(CPF_TESTE)).thenReturn(Optional.of(funcionarioTeste));
        // 3. Validação do novo email (está livre)
        when(repository.findByEmail("novoemail@email.com")).thenReturn(Optional.empty());
        // 4. Salva a entidade atualizada
        when(repository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execução
        FuncionarioDTO resultado = service.atualizar(ID_EXISTENTE, dtoAtualizado);

        // Verificação
        assertNotNull(resultado);
        assertEquals("Nome Atualizado", resultado.getNome());
        assertEquals("novoemail@email.com", resultado.getEmail());
        assertEquals(Funcao.REPARADOR, resultado.getFuncao());
        verify(repository, times(1)).save(any(Funcionario.class));
    }

    @Test
    void testAtualizar_TentativaMudarCpf() {
        // DTO com CPF diferente
        NovoFuncionarioDTO dtoCpfDiferente = new NovoFuncionarioDTO();
        dtoCpfDiferente.setNome("Nome");
        dtoCpfDiferente.setEmail(EMAIL_TESTE);
        dtoCpfDiferente.setSenha("senha");
        dtoCpfDiferente.setIdade(30);
        dtoCpfDiferente.setCpf("00011122233"); // CPF Diferente
        dtoCpfDiferente.setFuncao(Funcao.ADMINISTRATIVO);

        // Configuração do Mock:
        // 1. Encontra o funcionário original
        when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(funcionarioTeste));
        // 2. Validação de CPF (encontra o CPF antigo como sendo dele mesmo)
        when(repository.findByCpf("00011122233")).thenReturn(Optional.empty());

        // Verificação (Regra R2 do UC15 - Não pode editar documento)
        ValidacaoException excecao = assertThrows(
                ValidacaoException.class,
                () -> service.atualizar(ID_EXISTENTE, dtoCpfDiferente) // Execução
        );

        assertEquals("CPF não pode ser alterado.", excecao.getMessage());
        verify(repository, never()).save(any(Funcionario.class));
    }

    // --- Testes de Deleção ---

    @Test
    void testDeletar_Sucesso() {
        // Configuração do Mock:
        // 1. Encontra o funcionário
        when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(funcionarioTeste));
        // 2. Simula a deleção (não faz nada e não retorna erro)
        doNothing().when(repository).delete(funcionarioTeste);

        // Execução
        assertDoesNotThrow(() -> service.deletar(ID_EXISTENTE));

        // Verificação
        verify(repository, times(1)).findById(ID_EXISTENTE);
        verify(repository, times(1)).delete(funcionarioTeste);
    }

    @Test
    void testDeletar_NaoEncontrado() {
        // Configuração do Mock:
        when(repository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        // Verificação
        RecursoNaoEncontradoException excecao = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.deletar(ID_INEXISTENTE) // Execução
        );

        assertEquals("Funcionário não encontrado com ID: " + ID_INEXISTENTE, excecao.getMessage());
        verify(repository, never()).delete(any(Funcionario.class));
    }
}