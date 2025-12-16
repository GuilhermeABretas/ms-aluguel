package br.com.bicicletario.ms_aluguel.api.dto;

import jakarta.validation.constraints.NotNull;

public class NovaDevolucaoDTO {

    @NotNull(message = "ID da tranca é obrigatório")
    private Long idTranca;

    @NotNull(message = "ID da bicicleta é obrigatório")
    private Long idBicicleta;


    public Long getIdTranca() { return idTranca; }
    public void setIdTranca(Long idTranca) { this.idTranca = idTranca; }
    public Long getIdBicicleta() { return idBicicleta; }
    public void setIdBicicleta(Long idBicicleta) { this.idBicicleta = idBicicleta; }
}