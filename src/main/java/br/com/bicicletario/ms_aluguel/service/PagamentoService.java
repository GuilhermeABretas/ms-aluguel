package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;

public interface PagamentoService {
    void validarCartao(NovoCartaoDeCreditoDTO cartao);
    void realizarCobranca(CartaoDeCredito cartao, Double valor);
}