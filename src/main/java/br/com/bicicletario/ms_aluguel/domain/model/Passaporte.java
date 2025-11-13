package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class Passaporte {

    private String numero;
    private LocalDate validade;
    private String pais; // Swagger define como "countrycode" de 2 dígitos

    // --- CONSTRUTOR PADRÃO (JPA) ---
    public Passaporte() {
    }

    // --- GETTERS E SETTERS MANUAIS ---

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