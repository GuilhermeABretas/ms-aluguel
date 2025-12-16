package br.com.bicicletario.ms_aluguel.service;

import org.springframework.stereotype.Service;

@Service
public class EquipamentoServiceMockImpl implements EquipamentoService {

    @Override
    public Long recuperarBicicletaPorTranca(Long idTranca) {
        // Simulação: Se o ID da tranca for positivo, dizemos que tem a bicicleta de ID 100.
        // Se quiser testar o erro, envie um ID de tranca negativo ou zero.
        if (idTranca != null && idTranca > 0) {
            return 100L; // ID simulado da bicicleta
        }
        return null; // Nenhuma bicicleta encontrada
    }

    @Override
    public void destrancarTranca(Long idTranca) {
        System.out.println("[MOCK] Solicitando destrancamento da tranca: " + idTranca);
    }
}