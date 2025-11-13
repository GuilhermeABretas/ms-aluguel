package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.FuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoFuncionarioDTO;

import java.util.List;

public interface FuncionarioService {

    /**
     * Lista todos os funcionários cadastrados.
     * (GET /funcionario)
     */
    List<FuncionarioDTO> listarTodos();

    /**
     * Busca um funcionário específico pela sua matrícula (ID).
     * (GET /funcionario/{idFuncionario})
     */
    FuncionarioDTO buscarPorId(Long idFuncionario);

    /**
     * Cadastra um novo funcionário no sistema.
     * (POST /funcionario)
     */
    FuncionarioDTO salvar(NovoFuncionarioDTO dto);

    /**
     * Atualiza os dados de um funcionário existente.
     * (PUT /funcionario/{idFuncionario})
     */
    FuncionarioDTO atualizar(Long idFuncionario, NovoFuncionarioDTO dto);

    /**
     * Remove um funcionário do sistema.
     * (DELETE /funcionario/{idFuncionario})
     */
    void deletar(Long idFuncionario);

}