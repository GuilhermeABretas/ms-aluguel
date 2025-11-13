package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;

public interface PagamentoService {


    boolean validarCartao(NovoCartaoDeCreditoDTO cartaoDTO);

}