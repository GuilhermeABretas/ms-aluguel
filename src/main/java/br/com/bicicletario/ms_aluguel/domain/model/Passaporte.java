package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor @NoArgsConstructor
public class Passaporte {

    private String numero;
    private LocalDate validade;
    private String pais; // Swagger define como "countrycode" de 2 dígitos


}