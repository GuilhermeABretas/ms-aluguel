package br.com.bicicletario.ms_aluguel.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BicicletaDTO {
    private Long id;
    private String marca;
    private String modelo;
    private String numero;
    private String status;
}