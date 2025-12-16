package br.com.bicicletario.ms_aluguel.api.controller;

import br.com.bicicletario.ms_aluguel.api.dto.AluguelDTO;
import br.com.bicicletario.ms_aluguel.api.dto.DevolucaoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovaDevolucaoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoAluguelDTO;
import br.com.bicicletario.ms_aluguel.service.AluguelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AluguelController {

    private final AluguelService aluguelService;

    public AluguelController(AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }

    @PostMapping("/aluguel")
    public ResponseEntity<AluguelDTO> realizarAluguel(@Valid @RequestBody NovoAluguelDTO dto) {
        return ResponseEntity.ok(aluguelService.realizarAluguel(dto));
    }

    @PostMapping("/devolucao")
    public ResponseEntity<DevolucaoDTO> realizarDevolucao(@Valid @RequestBody NovaDevolucaoDTO dto) {
        return ResponseEntity.ok(aluguelService.realizarDevolucao(dto));
    }
}