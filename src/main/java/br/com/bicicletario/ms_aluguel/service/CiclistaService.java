package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.CartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.CiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCiclistaDTO;

public interface CiclistaService {

    // --- UC01: Cadastrar ---

    CiclistaDTO cadastrarCiclista(NovoCiclistaDTO dto);


    boolean existeEmail(String email);

    // --- UC02: Ativar ---

    CiclistaDTO ativarCiclista(Long idCiclista);

    // --- UC06: Obter e Alterar Dados (NOVOS) ---

    CiclistaDTO buscarPorId(Long idCiclista);


    CiclistaDTO atualizarCiclista(Long idCiclista, NovoCiclistaDTO dto);

    // --- Helpers para Aluguel (NOVOS) ---

    boolean permiteAluguel(Long idCiclista);


    CiclistaDTO obterBicicletaAlugada(Long idCiclista);

    // --- UC07: Cartão ---

    CartaoDeCreditoDTO buscarCartao(Long idCiclista);


    void atualizarCartao(Long idCiclista, NovoCartaoDeCreditoDTO cartaoDTO);

}