package br.com.bicicletario.seumicrosservico.client;

import br.com.bicicletario.seumicrosservico.dto.externo.CobrancaRequest;
import br.com.bicicletario.seumicrosservico.dto.externo.CobrancaResponse;
import br.com.bicicletario.seumicrosservico.dto.externo.EnviarEmailRequest;
import br.com.bicicletario.seumicrosservico.dto.externo.ValidacaoCartaoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// A URL deve vir do application.properties para facilitar a troca entre local e nuvem
@FeignClient(name = "ms-externo", url = "${app.ms-externo.url}")
public interface ExternoClient {

    // Validação de Cartão
    @PostMapping("/validaCartaoDeCredito")
    void validarCartao(@RequestBody ValidacaoCartaoRequest request);

    // Envio de Email
    @PostMapping("/enviarEmail")
    void enviarEmail(@RequestBody EnviarEmailRequest request);

    // Cobranças
    @PostMapping("/cobranca")
    CobrancaResponse realizarCobranca(@RequestBody CobrancaRequest request);

    @GetMapping("/cobranca/{id}")
    CobrancaResponse buscarCobranca(@PathVariable("id") Long id);
}