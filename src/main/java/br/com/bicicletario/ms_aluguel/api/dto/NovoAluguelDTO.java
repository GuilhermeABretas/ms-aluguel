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
public class NovoAluguelDTO {

    @NotNull(message = "ID do ciclista é obrigatório")
    private Long ciclista;

    @NotNull(message = "ID da tranca de início é obrigatório")
    private Long trancaInicio;
}