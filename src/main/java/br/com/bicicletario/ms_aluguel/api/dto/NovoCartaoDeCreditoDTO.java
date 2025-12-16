package br.com.bicicletario.ms_aluguel.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NovoCartaoDeCreditoDTO {

    @NotBlank(message = "Nome do titular é obrigatório")
    private String nomeTitular;

    @NotBlank(message = "Número do cartão é obrigatório")
    @Pattern(regexp = "\\d+", message = "Número do cartão deve conter apenas dígitos")
    private String numero;

    @Future(message = "Data de validade deve ser futura")
    private LocalDate validade;

    @NotBlank(message = "CVV é obrigatório")
    @Size(min = 3, max = 4, message = "CVV deve ter entre 3 e 4 dígitos")
    @Pattern(regexp = "\\d{3,4}", message = "CVV deve conter apenas dígitos")
    private String cvv;

}