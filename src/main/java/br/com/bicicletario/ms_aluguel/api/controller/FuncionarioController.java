package br.com.bicicletario.ms_aluguel.api.controller;

import br.com.bicicletario.ms_aluguel.api.dto.FuncionarioDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoFuncionarioDTO;
import br.com.bicicletario.ms_aluguel.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> listarFuncionarios() {
        return ResponseEntity.ok(funcionarioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> cadastrarFuncionario(@Valid @RequestBody NovoFuncionarioDTO dto) {
        FuncionarioDTO novoFuncionario = funcionarioService.cadastrarFuncionario(dto);

        return ResponseEntity.ok(novoFuncionario);
    }

    @GetMapping("/{idFuncionario}")
    public ResponseEntity<FuncionarioDTO> buscarPorId(@PathVariable Long idFuncionario) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(idFuncionario));
    }

    @PutMapping("/{idFuncionario}")
    public ResponseEntity<FuncionarioDTO> atualizarFuncionario(
            @PathVariable Long idFuncionario,
            @Valid @RequestBody NovoFuncionarioDTO dto) {
        return ResponseEntity.ok(funcionarioService.atualizarFuncionario(idFuncionario, dto));
    }

    @DeleteMapping("/{idFuncionario}")
    public ResponseEntity<Void> removerFuncionario(@PathVariable Long idFuncionario) {
        funcionarioService.removerFuncionario(idFuncionario);
        return ResponseEntity.ok().build();
    }
}