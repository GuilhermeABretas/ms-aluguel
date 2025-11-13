package br.com.bicicletario.ms_aluguel.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


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

    // --- GETTERS E SETTERS MANUAIS ---

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

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

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}