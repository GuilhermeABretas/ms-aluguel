package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.FuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoFuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.exception.RecursoNaoEncontradoException;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.Funcionario;
import br.com.bicicletario.ms_aluguel.domain.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    @Autowired
    private FuncionarioRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(FuncionarioDTO::new) // Converte Entidade para DTO
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FuncionarioDTO buscarPorId(Long idFuncionario) {
        Funcionario entidade = buscarFuncionarioPeloId(idFuncionario);
        return new FuncionarioDTO(entidade);
    }

    @Override
    @Transactional
    public FuncionarioDTO salvar(NovoFuncionarioDTO dto) {
        // Validação de negócio: (CPF e Email não podem ser duplicados)
        validarCpfEEmail(dto.getCpf(), dto.getEmail(), null);

        Funcionario entidade = new Funcionario();
        // A Regra R2 (UC15) diz que a matrícula é gerada automaticamente.
        // O @GeneratedValue no 'id' da entidade já faz isso.
        mapearDtoParaEntidade(dto, entidade);

        Funcionario funcionarioSalvo = repository.save(entidade);
        return new FuncionarioDTO(funcionarioSalvo);
    }

    @Override
    @Transactional
    public FuncionarioDTO atualizar(Long idFuncionario, NovoFuncionarioDTO dto) {
        Funcionario entidade = buscarFuncionarioPeloId(idFuncionario);

        // Validação de negócio: (CPF e Email não podem ser duplicados por OUTRO funcionário)
        validarCpfEEmail(dto.getCpf(), dto.getEmail(), idFuncionario);

        // A Regra R2 (UC15) diz que Matrícula (id) e Documento (cpf) não podem ser editados.
        // O CPF estamos validando acima, mas vamos garantir que ele não seja alterado.
        if (!entidade.getCpf().equals(dto.getCpf())) {
            throw new ValidacaoException("CPF não pode ser alterado.");
        }

        mapearDtoParaEntidade(dto, entidade); // Atualiza os dados

        Funcionario funcionarioAtualizado = repository.save(entidade);
        return new FuncionarioDTO(funcionarioAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long idFuncionario) {
        Funcionario entidade = buscarFuncionarioPeloId(idFuncionario);
        repository.delete(entidade);
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Busca o funcionário ou lança uma exceção 404.
     */
    private Funcionario buscarFuncionarioPeloId(Long idFuncionario) {
        return repository.findById(idFuncionario)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Funcionário não encontrado com ID: " + idFuncionario));
    }

    /**
     * Mapeia os dados do DTO de entrada para a Entidade do banco.
     */
    private void mapearDtoParaEntidade(NovoFuncionarioDTO dto, Funcionario entidade) {
        entidade.setNome(dto.getNome());
        entidade.setEmail(dto.getEmail());
        entidade.setSenha(dto.getSenha()); // (Em um projeto real, criptografaríamos isso)
        entidade.setIdade(dto.getIdade());
        entidade.setCpf(dto.getCpf());
        entidade.setFuncao(dto.getFuncao());
    }

    /**
     * Valida se o CPF ou Email já estão em uso por outro funcionário.
     */
    private void validarCpfEEmail(String cpf, String email, Long idAtual) {
        Optional<Funcionario> porCpf = repository.findByCpf(cpf);
        if (porCpf.isPresent() && (idAtual == null || !porCpf.get().getId().equals(idAtual))) {
            throw new ValidacaoException("CPF já cadastrado.");
        }

        Optional<Funcionario> porEmail = repository.findByEmail(email);
        if (porEmail.isPresent() && (idAtual == null || !porEmail.get().getId().equals(idAtual))) {
            throw new ValidacaoException("Email já cadastrado.");
        }
    }
}