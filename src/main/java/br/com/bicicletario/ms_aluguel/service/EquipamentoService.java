package br.com.bicicletario.ms_aluguel.service;

public interface EquipamentoService {
    /**
     * Simula a busca da bicicleta que está presa em uma tranca.
     * Retorna o ID da bicicleta.
     */
    Long recuperarBicicletaPorTranca(Long idTranca);

    /**
     * Simula liberar a tranca (destrancar).
     */
    void destrancarTranca(Long idTranca);
}