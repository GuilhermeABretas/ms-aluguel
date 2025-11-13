package br.com.bicicletario.ms_aluguel.api.controller;

import br.com.bicicletario.ms_aluguel.api.dto.CartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.service.CiclistaService; // Usamos o CiclistaService
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cartaoDeCredito") // Rota base correta
public class CartaoDeCreditoController {


    private CiclistaService ciclistaService; // O CiclistaService ainda controla a lógica

    public CartaoDeCreditoController(CiclistaService ciclistaService) {
        this.ciclistaService = ciclistaService;
    }

    /**
     * UC07 - Recupera dados de cartão de crédito de um ciclista
     */
    @GetMapping("/{idCiclista}")
    public ResponseEntity<CartaoDeCreditoDTO> getCartaoDeCredito(@PathVariable Long idCiclista) {

        CartaoDeCreditoDTO cartao = ciclistaService.buscarCartao(idCiclista);
        return ResponseEntity.ok(cartao);
    }

    /**
     * UC07 - Alterar dados de cartão de crédito de um ciclista
     */
    @PutMapping("/{idCiclista}")
    public ResponseEntity<Void> atualizarCartaoDeCredito(
            @PathVariable Long idCiclista,
            @Valid @RequestBody NovoCartaoDeCreditoDTO cartaoDTO) {

        ciclistaService.atualizarCartao(idCiclista, cartaoDTO);
        // O Swagger diz que a resposta é 200 OK (sem corpo)
        return ResponseEntity.ok().build();
    }
}