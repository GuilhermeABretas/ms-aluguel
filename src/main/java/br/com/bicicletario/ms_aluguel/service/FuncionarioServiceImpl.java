package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.FuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoFuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.exception.RecursoNaoEncontradoException;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.Funcionario;
import br.com.bicicletario.ms_aluguel.domain.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioServiceImpl(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(FuncionarioDTO::new)
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
    public FuncionarioDTO cadastrarFuncionario(NovoFuncionarioDTO dto) {
        validarCpfEEmail(dto.getCpf(), dto.getEmail(), null);

        Funcionario entidade = new Funcionario();
        mapearDtoParaEntidade(dto, entidade);

        Funcionario funcionarioSalvo = repository.save(entidade);
        return new FuncionarioDTO(funcionarioSalvo);
    }

    @Override
    @Transactional
    public FuncionarioDTO atualizarFuncionario(Long idFuncionario, NovoFuncionarioDTO dto) {
        Funcionario entidade = buscarFuncionarioPeloId(idFuncionario);

        validarCpfEEmail(dto.getCpf(), dto.getEmail(), idFuncionario);


        if (!entidade.getCpf().equals(dto.getCpf())) {
            throw new ValidacaoException("CPF não pode ser alterado.");
        }

        mapearDtoParaEntidade(dto, entidade);

        Funcionario funcionarioAtualizado = repository.save(entidade);
        return new FuncionarioDTO(funcionarioAtualizado);
    }

    @Override
    @Transactional
    public void removerFuncionario(Long idFuncionario) {
        Funcionario entidade = buscarFuncionarioPeloId(idFuncionario);
        repository.delete(entidade);
    }

    private Funcionario buscarFuncionarioPeloId(Long idFuncionario) {
        return repository.findById(idFuncionario)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Funcionário não encontrado com ID: " + idFuncionario));
    }

    private void mapearDtoParaEntidade(NovoFuncionarioDTO dto, Funcionario entidade) {
        entidade.setNome(dto.getNome());
        entidade.setEmail(dto.getEmail());
        entidade.setSenha(dto.getSenha());
        entidade.setIdade(dto.getIdade());
        entidade.setCpf(dto.getCpf());
        entidade.setFuncao(dto.getFuncao());
        entidade.setDocumento(dto.getDocumento());
    }

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