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
    private Long ciclista;
    private Long bicicleta;
    private LocalDateTime dataHoraRetirada;
    private LocalDateTime dataHoraDevolucao;
    private Double valorCobrado;


    public AluguelDTO(Aluguel entidade) {
        if (entidade != null) {
            this.id = entidade.getId();

            // Proteção contra NullPointerException caso o ciclista não venha preenchido
            if (entidade.getCiclista() != null) {
                this.ciclista = entidade.getCiclista().getId();
            }

            this.bicicleta = entidade.getIdBicicleta();
            this.dataHoraRetirada = entidade.getDataHoraRetirada();
            this.dataHoraDevolucao = entidade.getDataHoraDevolucao();
            this.valorCobrado = entidade.getValorCobrado();
        }
    }
}