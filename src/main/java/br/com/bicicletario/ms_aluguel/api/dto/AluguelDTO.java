package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Aluguel;
import java.time.LocalDateTime;

public class AluguelDTO {
    private Long id;
    private Long ciclista;
    private Long bicicleta;
    private LocalDateTime dataHoraRetirada;
    private LocalDateTime dataHoraDevolucao;
    private Double valorCobrado;

    public AluguelDTO(Aluguel entidade) {
        this.id = entidade.getId();
        this.ciclista = entidade.getCiclista().getId();
        this.bicicleta = entidade.getIdBicicleta();
        this.dataHoraRetirada = entidade.getDataHoraRetirada();
        this.dataHoraDevolucao = entidade.getDataHoraDevolucao();
        this.valorCobrado = entidade.getValorCobrado();
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCiclista() { return ciclista; }
    public void setCiclista(Long ciclista) { this.ciclista = ciclista; }
    public Long getBicicleta() { return bicicleta; }
    public void setBicicleta(Long bicicleta) { this.bicicleta = bicicleta; }
    public LocalDateTime getDataHoraRetirada() { return dataHoraRetirada; }
    public void setDataHoraRetirada(LocalDateTime dataHoraRetirada) { this.dataHoraRetirada = dataHoraRetirada; }
    public LocalDateTime getDataHoraDevolucao() { return dataHoraDevolucao; }
    public void setDataHoraDevolucao(LocalDateTime dataHoraDevolucao) { this.dataHoraDevolucao = dataHoraDevolucao; }
    public Double getValorCobrado() { return valorCobrado; }
    public void setValorCobrado(Double valorCobrado) { this.valorCobrado = valorCobrado; }
}