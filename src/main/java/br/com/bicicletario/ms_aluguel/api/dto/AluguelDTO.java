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
public class AluguelDTO {
    private Long id;
    private Long ciclistaId;
    private Long bicicletaId;
    private LocalDateTime dataHoraInicio;
    private Long trancaInicioId;
    private Double valorCobrado;

    public AluguelDTO(Aluguel entidade) {
        this.id = entidade.getId();

        this.ciclistaId = entidade.getCiclista() != null ? entidade.getCiclista().getId() : null;
        this.bicicletaId = entidade.getBicicletaId();
        this.dataHoraInicio = entidade.getDataHoraInicio();
        this.trancaInicioId = entidade.getTrancaInicioId();
        this.valorCobrado = entidade.getValorCobrado();
    }
}