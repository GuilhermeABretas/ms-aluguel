package br.com.bicicletario.ms_aluguel.api.controller;

import br.com.bicicletario.ms_aluguel.api.dto.CiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCiclistaDTO;
import br.com.bicicletario.ms_aluguel.service.CiclistaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ciclista")
public class CiclistaController {

    private final CiclistaService ciclistaService;

    public CiclistaController(CiclistaService ciclistaService) {
        this.ciclistaService = ciclistaService;
    }

    @PostMapping
    public ResponseEntity<CiclistaDTO> cadastrarCiclista(@Valid @RequestBody NovoCiclistaDTO dto) {
        CiclistaDTO ciclistaSalvo = ciclistaService.cadastrarCiclista(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ciclistaSalvo);
    }

    @GetMapping("/existeEmail/{email}")
    public ResponseEntity<Boolean> existeEmail(@PathVariable String email) {
        return ResponseEntity.ok(ciclistaService.existeEmail(email));
    }

    @PostMapping("/{idCiclista}/ativar")
    public ResponseEntity<CiclistaDTO> ativarCiclista(@PathVariable Long idCiclista) {
        return ResponseEntity.ok(ciclistaService.ativarCiclista(idCiclista));
    }

    @GetMapping("/{idCiclista}")
    public ResponseEntity<CiclistaDTO> buscarPorId(@PathVariable Long idCiclista) {
        return ResponseEntity.ok(ciclistaService.buscarPorId(idCiclista));
    }

    @PutMapping("/{idCiclista}")
    public ResponseEntity<CiclistaDTO> atualizarCiclista(@PathVariable Long idCiclista, @Valid @RequestBody NovoCiclistaDTO dto) {
        return ResponseEntity.ok(ciclistaService.atualizarCiclista(idCiclista, dto));
    }

    @GetMapping("/{idCiclista}/permiteAluguel")
    public ResponseEntity<Boolean> permiteAluguel(@PathVariable Long idCiclista) {
        return ResponseEntity.ok(ciclistaService.permiteAluguel(idCiclista));
    }

    @GetMapping("/{idCiclista}/bicicletaAlugada")
    public ResponseEntity<CiclistaDTO> obterBicicletaAlugada(@PathVariable Long idCiclista) {
        return ResponseEntity.ok(ciclistaService.obterBicicletaAlugada(idCiclista));
    }
}