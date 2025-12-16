package br.com.bicicletario.ms_aluguel.api.controller;

import br.com.bicicletario.ms_aluguel.domain.model.Funcao;
import br.com.bicicletario.ms_aluguel.domain.model.Funcionario;
import br.com.bicicletario.ms_aluguel.domain.repository.AluguelRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CartaoDeCreditoRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CiclistaRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.FuncionarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurarBancoController {

    private final AluguelRepository aluguelRepository;
    private final CartaoDeCreditoRepository cartaoRepository;
    private final CiclistaRepository ciclistaRepository;
    private final FuncionarioRepository funcionarioRepository;

    public RestaurarBancoController(AluguelRepository aluguelRepository,
                                    CartaoDeCreditoRepository cartaoRepository,
                                    CiclistaRepository ciclistaRepository,
                                    FuncionarioRepository funcionarioRepository) {
        this.aluguelRepository = aluguelRepository;
        this.cartaoRepository = cartaoRepository;
        this.ciclistaRepository = ciclistaRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping("/restaurarBanco")
    @Transactional // Essencial para operações em lote
    public ResponseEntity<String> restaurarBanco() {
        // 1. Limpar todas as tabelas (Ordem importa por causa das chaves estrangeiras)
        aluguelRepository.deleteAll();
        cartaoRepository.deleteAll();
        ciclistaRepository.deleteAll();
        funcionarioRepository.deleteAll();

        // 2. Inserir dados iniciais (Seed) para não deixar o sistema vazio
        criarFuncionarioAdmin();

        return ResponseEntity.ok("Banco restaurado com sucesso");
    }

    private void criarFuncionarioAdmin() {
        Funcionario admin = new Funcionario();
        admin.setEmail("admin@bicicletario.com");
        admin.setNome("Administrador Inicial");
        admin.setSenha("admin123");
        admin.setCpf("00000000000"); // CPF Fictício válido para testes
        admin.setFuncao(Funcao.ADMINISTRATIVO);
        admin.setIdade(30);
        admin.setDocumento("MG-00.000.000"); // Campo obrigatório
        funcionarioRepository.save(admin);
    }
}