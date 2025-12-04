package br.com.bicicletario.ms_aluguel.service;

import org.springframework.stereotype.Service;

@Service
public class EquipamentoServiceMockImpl implements EquipamentoService {

    @Override
    public Long recuperarBicicletaPorTranca(Long idTranca) {
        // Simulação: Retorna um ID de bicicleta fixo ou baseado na tranca
        return idTranca + 1000;
    }

    @Override
    public void destrancarTranca(Long idTranca) {
        System.out.println("--- MOCK EQUIPAMENTO: Tranca " + idTranca + " destrancada. ---");
    }
}