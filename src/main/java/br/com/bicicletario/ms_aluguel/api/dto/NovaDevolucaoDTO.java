package br.com.bicicletario.ms_aluguel.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NovaDevolucaoDTO {

    @NotNull(message = "ID da tranca é obrigatório")
    private Long idTranca;

    @NotNull(message = "ID da bicicleta é obrigatório")
    private Long idBicicleta;
}