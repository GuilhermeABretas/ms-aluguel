package br.com.bicicletario.ms_aluguel.service;

import org.springframework.stereotype.Service;

@Service
public class EquipamentoServiceMockImpl implements EquipamentoService {

    @Override
    public Long recuperarBicicletaPorTranca(Long idTranca) {

        if (idTranca != null && idTranca > 0) {
            return 100L;
        }
        return null;
    }

    @Override
    public void destrancarTranca(Long idTranca) {
        System.out.println("[MOCK] Solicitando destrancamento da tranca: " + idTranca);
    }
}