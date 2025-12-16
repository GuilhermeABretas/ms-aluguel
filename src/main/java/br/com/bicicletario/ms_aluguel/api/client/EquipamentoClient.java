package br.com.bicicletario.ms_aluguel.api.client;

import br.com.bicicletario.ms_aluguel.api.dto.BicicletaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ms-equipamento", url = "${scb.ms-equipamento.url}")
public interface EquipamentoClient {


    @GetMapping("/tranca/{idTranca}/bicicleta")
    BicicletaDTO recuperarBicicletaPorTranca(@PathVariable("idTranca") Long idTranca);


    @PostMapping("/tranca/{idTranca}/destrancar")
    void destrancarTranca(@PathVariable("idTranca") Long idTranca);


    @PostMapping("/tranca/{idTranca}/trancar")
    void trancarTranca(@PathVariable("idTranca") Long idTranca);



    @GetMapping("/bicicleta/{idBicicleta}")
    BicicletaDTO buscarBicicleta(@PathVariable("idBicicleta") Long idBicicleta);
}