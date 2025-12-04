package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;
import java.time.LocalDate;


public class CartaoDeCreditoDTO {

    private String nomeTitular;
    private String numero;
    private LocalDate validade;



    public CartaoDeCreditoDTO() {
    }


    public CartaoDeCreditoDTO(CartaoDeCredito entidade) {
        this.nomeTitular = entidade.getNomeTitular();
        this.numero = entidade.getNumero();
        this.validade = entidade.getValidade();
    }


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