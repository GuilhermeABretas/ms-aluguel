package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Aluguel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevolucaoDTO {
    private Long id;
    private Long bicicletaId;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private Double valorExtra;
    private Double valorTotal;

    public DevolucaoDTO(Aluguel aluguel, Double valorExtra, Double valorTotal) {
        this.id = aluguel.getId();
        this.bicicletaId = aluguel.getBicicletaId();
        this.dataHoraInicio = aluguel.getDataHoraInicio();
        this.dataHoraFim = aluguel.getDataHoraDevolucao();
        this.valorExtra = valorExtra;
        this.valorTotal = valorTotal;
    }
}