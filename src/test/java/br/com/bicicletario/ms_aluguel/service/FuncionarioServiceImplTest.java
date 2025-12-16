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

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceImplTest {

    @Mock
    private FuncionarioRepository repository;

    @InjectMocks
    private FuncionarioServiceImpl service;

    private Funcionario funcionarioTeste;
    private NovoFuncionarioDTO novoFuncionarioDTO;
    private final Long ID_EXISTENTE = 1L;
    private final Long ID_INEXISTENTE = 99L;
    private final String CPF_TESTE = "12345678900";
    private final String EMAIL_TESTE = "teste@email.com";
    private final String DOCUMENTO_TESTE = "RG-1234567";

    @BeforeEach
    void setUp() {
        funcionarioTeste = new Funcionario(
                ID_EXISTENTE,
                "Funcionario Teste",
                EMAIL_TESTE,
                "senha123",
                30,
                CPF_TESTE,
                Funcao.ADMINISTRATIVO,
                DOCUMENTO_TESTE
        );

        novoFuncionarioDTO = new NovoFuncionarioDTO();
        novoFuncionarioDTO.setNome("Funcionario Teste");
        novoFuncionarioDTO.setEmail(EMAIL_TESTE);
        novoFuncionarioDTO.setSenha("senha123");
        novoFuncionarioDTO.setIdade(30);
        novoFuncionarioDTO.setCpf(CPF_TESTE);
        novoFuncionarioDTO.setFuncao(Funcao.ADMINISTRATIVO);
        novoFuncionarioDTO.setDocumento(DOCUMENTO_TESTE);
    }

    @Test
    void testListarTodos() {
        when(repository.findAll()).thenReturn(List.of(funcionarioTeste));

        List<FuncionarioDTO> resultado = service.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(EMAIL_TESTE, resultado.get(0).getEmail());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Sucesso() {
        when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(funcionarioTeste));

        FuncionarioDTO resultado = service.buscarPorId(ID_EXISTENTE);

        assertNotNull(resultado);
        assertEquals(ID_EXISTENTE, resultado.getId());
        verify(repository, times(1)).findById(ID_EXISTENTE);
    }

    @Test
    void testBuscarPorId_NaoEncontrado() {
        when(repository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException excecao = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(ID_INEXISTENTE)
        );

        assertEquals("Funcionário não encontrado com ID: " + ID_INEXISTENTE, excecao.getMessage());
        verify(repository, times(1)).findById(ID_INEXISTENTE);
    }

    @Test
    void testCadastrarFuncionario_Sucesso() {
        when(repository.findByCpf(CPF_TESTE)).thenReturn(Optional.empty());
        when(repository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.empty());
        when(repository.save(any(Funcionario.class))).thenReturn(funcionarioTeste);

        FuncionarioDTO resultado = service.cadastrarFuncionario(novoFuncionarioDTO);

        assertNotNull(resultado);
        assertEquals(ID_EXISTENTE, resultado.getId());
        verify(repository, times(1)).save(any(Funcionario.class));
    }

    @Test
    void testCadastrarFuncionario_CpfDuplicado() {
        when(repository.findByCpf(CPF_TESTE)).thenReturn(Optional.of(funcionarioTeste));

        ValidacaoException excecao = assertThrows(
                ValidacaoException.class,
                () -> service.cadastrarFuncionario(novoFuncionarioDTO)
        );

        assertEquals("CPF já cadastrado.", excecao.getMessage());
        verify(repository, never()).save(any(Funcionario.class));
    }

    @Test
    void testCadastrarFuncionario_EmailDuplicado() {
        when(repository.findByCpf(CPF_TESTE)).thenReturn(Optional.empty());
        when(repository.findByEmail(EMAIL_TESTE)).thenReturn(Optional.of(funcionarioTeste));

        ValidacaoException excecao = assertThrows(
                ValidacaoException.class,
                () -> service.cadastrarFuncionario(novoFuncionarioDTO)
        );

        assertEquals("Email já cadastrado.", excecao.getMessage());
        verify(repository, never()).save(any(Funcionario.class));
    }

    @Test
    void testAtualizarFuncionario_Sucesso() {
        NovoFuncionarioDTO dtoAtualizado = new NovoFuncionarioDTO();
        dtoAtualizado.setNome("Nome Atualizado");
        dtoAtualizado.setEmail("novoemail@email.com");
        dtoAtualizado.setSenha("novaSenha");
        dtoAtualizado.setIdade(31);
        dtoAtualizado.setCpf(CPF_TESTE);
        dtoAtualizado.setFuncao(Funcao.REPARADOR);
        dtoAtualizado.setDocumento(DOCUMENTO_TESTE);

        when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(funcionarioTeste));
        when(repository.findByCpf(CPF_TESTE)).thenReturn(Optional.of(funcionarioTeste));
        when(repository.findByEmail("novoemail@email.com")).thenReturn(Optional.empty());
        when(repository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FuncionarioDTO resultado = service.atualizarFuncionario(ID_EXISTENTE, dtoAtualizado);

        assertNotNull(resultado);
        assertEquals("Nome Atualizado", resultado.getNome());
        assertEquals("novoemail@email.com", resultado.getEmail());
        assertEquals(Funcao.REPARADOR, resultado.getFuncao());
        verify(repository, times(1)).save(any(Funcionario.class));
    }

    @Test
    void testAtualizarFuncionario_TentativaMudarCpf() {
        NovoFuncionarioDTO dtoCpfDiferente = new NovoFuncionarioDTO();
        dtoCpfDiferente.setNome("Nome");
        dtoCpfDiferente.setEmail(EMAIL_TESTE);
        dtoCpfDiferente.setSenha("senha");
        dtoCpfDiferente.setIdade(30);
        dtoCpfDiferente.setCpf("00011122233");
        dtoCpfDiferente.setFuncao(Funcao.ADMINISTRATIVO);
        dtoCpfDiferente.setDocumento(DOCUMENTO_TESTE);

        when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(funcionarioTeste));
        when(repository.findByCpf("00011122233")).thenReturn(Optional.empty());

        ValidacaoException excecao = assertThrows(
                ValidacaoException.class,
                () -> service.atualizarFuncionario(ID_EXISTENTE, dtoCpfDiferente)
        );

        assertEquals("CPF não pode ser alterado.", excecao.getMessage());
        verify(repository, never()).save(any(Funcionario.class));
    }

    @Test
    void testRemoverFuncionario_Sucesso() {
        when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(funcionarioTeste));
        doNothing().when(repository).delete(funcionarioTeste);

        assertDoesNotThrow(() -> service.removerFuncionario(ID_EXISTENTE));

        verify(repository, times(1)).findById(ID_EXISTENTE);
        verify(repository, times(1)).delete(funcionarioTeste);
    }

    @Test
    void testRemoverFuncionario_NaoEncontrado() {
        when(repository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException excecao = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.removerFuncionario(ID_INEXISTENTE)
        );

        assertEquals("Funcionário não encontrado com ID: " + ID_INEXISTENTE, excecao.getMessage());
        verify(repository, never()).delete(any(Funcionario.class));
    }
}