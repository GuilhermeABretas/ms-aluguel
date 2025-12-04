package br.com.bicicletario.ms_aluguel.api.controller;

import br.com.bicicletario.ms_aluguel.domain.repository.AluguelRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CartaoDeCreditoRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CiclistaRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.FuncionarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurarBancoController {

    private final CiclistaRepository ciclistaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final AluguelRepository aluguelRepository;
    private final CartaoDeCreditoRepository cartaoRepository;

    public RestaurarBancoController(CiclistaRepository ciclistaRepository,
                                    FuncionarioRepository funcionarioRepository,
                                    AluguelRepository aluguelRepository,
                                    CartaoDeCreditoRepository cartaoRepository) {
        this.ciclistaRepository = ciclistaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.aluguelRepository = aluguelRepository;
        this.cartaoRepository = cartaoRepository;
    }

    @GetMapping("/restaurarBanco")
    public ResponseEntity<Void> restaurarBanco() {
        // Apaga os dados na ordem correta para não dar erro de chave estrangeira
        aluguelRepository.deleteAll();
        cartaoRepository.deleteAll();
        ciclistaRepository.deleteAll();
        funcionarioRepository.deleteAll();

        System.out.println("--- BANCO DE DADOS RESTAURADO ---");
        return ResponseEntity.ok().build();
    }
}