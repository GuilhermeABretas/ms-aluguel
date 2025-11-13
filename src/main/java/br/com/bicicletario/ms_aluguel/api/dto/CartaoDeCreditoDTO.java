package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;
import java.time.LocalDate;

// DTO para a resposta de GET /cartaoDeCredito/{idCiclista}
public class CartaoDeCreditoDTO {

    private String nomeTitular;
    private String numero; // Nota: Em um sistema real, mascararíamos isso
    private LocalDate validade;
    // Não retornamos o CVV por segurança

    // Construtor padrão
    public CartaoDeCreditoDTO() {
    }

    // Construtor para mapeamento
    public CartaoDeCreditoDTO(CartaoDeCredito entidade) {
        this.nomeTitular = entidade.getNomeTitular();
        this.numero = entidade.getNumero();
        this.validade = entidade.getValidade();
    }

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
}