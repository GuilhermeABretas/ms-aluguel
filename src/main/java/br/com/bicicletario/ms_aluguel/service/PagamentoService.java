package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;

public interface PagamentoService {

    /**
     * Simula a validação de um cartão de crédito.
     * @param cartaoDTO O DTO do cartão a ser validado.
     * @return true se o cartão for válido (mock), false ou exceção se inválido.
     */
    boolean validarCartao(NovoCartaoDeCreditoDTO cartaoDTO);

}