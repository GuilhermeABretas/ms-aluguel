package br.com.bicicletario.ms_aluguel.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


public class NovoPassaporteDTO {

    @NotBlank(message = "Número do passaporte é obrigatório")
    private String numero;

    @Future(message = "Validade do passaporte deve ser futura")
    private LocalDate validade;

    @NotBlank(message = "País do passaporte é obrigatório")
    @Size(min = 2, max = 2, message = "País deve ser um código de 2 dígitos (ISO 3166-1 alpha-2)")
    private String pais;



    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}