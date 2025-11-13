package br.com.bicicletario.ms_aluguel.api.controller;

import br.com.bicicletario.ms_aluguel.api.dto.FuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoFuncionarioDTO;
import br.com.bicicletario.ms_aluguel.service.FuncionarioService; // Importe o service
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    // Agora sim, injetamos o Service

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    private FuncionarioService funcionarioService;

    /**
     * UC15 - Incluir Funcionário
     */
    @PostMapping
    public ResponseEntity<FuncionarioDTO> incluirFuncionario(
            @Valid @RequestBody NovoFuncionarioDTO dto) {

        FuncionarioDTO novoFuncionario = funcionarioService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoFuncionario);
    }

    /**
     * UC15 - Listar Funcionários
     */
    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> listarFuncionarios() {
        List<FuncionarioDTO> funcionarios = funcionarioService.listarTodos();
        return ResponseEntity.ok(funcionarios);
    }

    /**
     * UC15 - Obter Funcionário (Implícito no Editar/Remover)
     */
    @GetMapping("/{idFuncionario}")
    public ResponseEntity<FuncionarioDTO> obterFuncionario(
            @PathVariable Long idFuncionario) {

        FuncionarioDTO funcionario = funcionarioService.buscarPorId(idFuncionario);
        return ResponseEntity.ok(funcionario);
    }

    /**
     * UC1AF - Editar Funcionário
     */
    @PutMapping("/{idFuncionario}")
    public ResponseEntity<FuncionarioDTO> editarFuncionario(
            @PathVariable Long idFuncionario,
            @Valid @RequestBody NovoFuncionarioDTO dto) {

        FuncionarioDTO funcionarioAtualizado = funcionarioService.atualizar(idFuncionario, dto);
        return ResponseEntity.ok(funcionarioAtualizado);
    }

    /**
     * UC15 - Remover Funcionário
     */
    @DeleteMapping("/{idFuncionario}")
    public ResponseEntity<Void> removerFuncionario(
            @PathVariable Long idFuncionario) {

        funcionarioService.deletar(idFuncionario);
        return ResponseEntity.noContent().build();
    }
}