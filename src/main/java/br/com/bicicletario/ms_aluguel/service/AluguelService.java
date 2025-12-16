package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.AluguelDTO;
import br.com.bicicletario.ms_aluguel.api.dto.DevolucaoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovaDevolucaoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoAluguelDTO;

public interface AluguelService {
    AluguelDTO realizarAluguel(NovoAluguelDTO dto);
    DevolucaoDTO realizarDevolucao(NovaDevolucaoDTO dto);
}