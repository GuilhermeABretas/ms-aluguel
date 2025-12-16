package br.com.bicicletario.ms_aluguel.api.client;

import br.com.bicicletario.ms_aluguel.api.dto.BicicletaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// 'name' é o nome interno (pode ser qualquer um)
// 'url' pega o endereço definido no application.properties
@FeignClient(name = "ms-equipamento", url = "${scb.ms-equipamento.url}")
public interface EquipamentoClient {

    // Pergunta ao MS-Equipamento: "Qual bicicleta está na tranca X?"
    // Ajuste a rota ("/tranca/...") conforme o Swagger do outro grupo
    @GetMapping("/tranca/{idTranca}/bicicleta")
    BicicletaDTO recuperarBicicletaPorTranca(@PathVariable("idTranca") Long idTranca);

    // Manda o MS-Equipamento destrancar a tranca X
    @PostMapping("/tranca/{idTranca}/destrancar")
    void destrancarTranca(@PathVariable("idTranca") Long idTranca);

    // Busca dados detalhados da bicicleta (usado no endpoint /bicicletaAlugada)
    @GetMapping("/bicicleta/{idBicicleta}")
    BicicletaDTO buscarBicicleta(@PathVariable("idBicicleta") Long idBicicleta);
}