package br.com.bicicletario.ms_aluguel.api.controller;

import br.com.bicicletario.ms_aluguel.api.dto.CiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCiclistaDTO;
import br.com.bicicletario.ms_aluguel.service.CiclistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ciclista")
public class CiclistaController {

    public CiclistaController(CiclistaService ciclistaService) {
        this.ciclistaService = ciclistaService;
    }

    private CiclistaService ciclistaService;

    /**
     * UC01 - Cadastrar um ciclista
     */
    @PostMapping
    public ResponseEntity<CiclistaDTO> cadastrarCiclista(@Valid @RequestBody NovoCiclistaDTO dto) {
        CiclistaDTO ciclistaSalvo = ciclistaService.cadastrarCiclista(dto);
        // Retorna 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(ciclistaSalvo);
    }

    /**
     * UC01 - Verifica se o e-mail já foi utilizado
     */
    @GetMapping("/existeEmail/{email}")
    public ResponseEntity<Boolean> existeEmail(@PathVariable String email) {
        boolean existe = ciclistaService.existeEmail(email);
        return ResponseEntity.ok(existe);
    }

    /**
     * UC02 - Ativar cadastro do ciclista
     */
    @PostMapping("/{idCiclista}/ativar")
    public ResponseEntity<CiclistaDTO> ativarCiclista(@PathVariable Long idCiclista) {

        CiclistaDTO ciclistaAtivado = ciclistaService.ativarCiclista(idCiclista);
        return ResponseEntity.ok(ciclistaAtivado);
    }
}