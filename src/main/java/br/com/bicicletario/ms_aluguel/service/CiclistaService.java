package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.CartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.CiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCiclistaDTO;

public interface CiclistaService {

    /**
     * UC01 - Cadastra um novo ciclista.
     */
    CiclistaDTO cadastrarCiclista(NovoCiclistaDTO dto);

    /**
     * UC01 - Verifica se um email já está em uso.
     */
    boolean existeEmail(String email);

    /**
     * UC02 - Ativa o cadastro de um ciclista.
     */
    CiclistaDTO ativarCiclista(Long idCiclista);

    /**
     * UC07 - Busca os dados do cartão de crédito de um ciclista.
     */
    CartaoDeCreditoDTO buscarCartao(Long idCiclista);

    /**
     * UC07 - Altera os dados do cartão de crédito de um ciclista.
     */
    void atualizarCartao(Long idCiclista, NovoCartaoDeCreditoDTO cartaoDTO);

}