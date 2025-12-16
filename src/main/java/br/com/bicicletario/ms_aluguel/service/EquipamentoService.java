package br.com.bicicletario.ms_aluguel.service;

public interface EquipamentoService {

    Long recuperarBicicletaPorTranca(Long idTranca);


    void destrancarTranca(Long idTranca);
}