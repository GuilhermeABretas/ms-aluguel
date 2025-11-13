package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.FuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoFuncionarioDTO;

import java.util.List;

public interface FuncionarioService {


    List<FuncionarioDTO> listarTodos();


    FuncionarioDTO buscarPorId(Long idFuncionario);


    FuncionarioDTO salvar(NovoFuncionarioDTO dto);


    FuncionarioDTO atualizar(Long idFuncionario, NovoFuncionarioDTO dto);


    void deletar(Long idFuncionario);

}