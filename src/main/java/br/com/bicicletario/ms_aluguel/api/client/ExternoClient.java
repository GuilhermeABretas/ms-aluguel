package br.com.bicicletario.ms_aluguel.api.client; // PACOTE CORRIGIDO


import br.com.bicicletario.ms_aluguel.api.dto.externo.CobrancaRequest;
import br.com.bicicletario.ms_aluguel.api.dto.externo.CobrancaResponse;
import br.com.bicicletario.ms_aluguel.api.dto.externo.EnviarEmailRequest;
import br.com.bicicletario.ms_aluguel.api.dto.externo.ValidacaoCartaoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-externo", url = "${app.ms-externo.url}")
public interface ExternoClient {

    @PostMapping("/validaCartaoDeCredito")
    void validarCartao(@RequestBody ValidacaoCartaoRequest request);

    @PostMapping("/enviarEmail")
    void enviarEmail(@RequestBody EnviarEmailRequest request);

    @PostMapping("/cobranca")
    CobrancaResponse realizarCobranca(@RequestBody CobrancaRequest request);

    @GetMapping("/cobranca/{id}")
    CobrancaResponse buscarCobranca(@PathVariable("id") Long id);
}