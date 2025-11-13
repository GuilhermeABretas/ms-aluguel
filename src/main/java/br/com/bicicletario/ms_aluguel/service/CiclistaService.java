package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.CartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.CiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCiclistaDTO;

public interface CiclistaService {


    CiclistaDTO cadastrarCiclista(NovoCiclistaDTO dto);


    boolean existeEmail(String email);


    CiclistaDTO ativarCiclista(Long idCiclista);


    CartaoDeCreditoDTO buscarCartao(Long idCiclista);


    void atualizarCartao(Long idCiclista, NovoCartaoDeCreditoDTO cartaoDTO);

}