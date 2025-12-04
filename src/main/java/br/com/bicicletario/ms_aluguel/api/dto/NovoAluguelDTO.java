package br.com.bicicletario.ms_aluguel.api.dto;

import jakarta.validation.constraints.NotNull;

public class NovoAluguelDTO {

    @NotNull(message = "ID do ciclista é obrigatório")
    private Long ciclista;

    @NotNull(message = "ID da tranca é obrigatório")
    private Long trancaInicio;

    // Getters e Setters manuais
    public Long getCiclista() { return ciclista; }
    public void setCiclista(Long ciclista) { this.ciclista = ciclista; }
    public Long getTrancaInicio() { return trancaInicio; }
    public void setTrancaInicio(Long trancaInicio) { this.trancaInicio = trancaInicio; }
}