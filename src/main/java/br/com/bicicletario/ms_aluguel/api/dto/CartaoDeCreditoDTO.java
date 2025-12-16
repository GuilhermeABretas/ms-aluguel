package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartaoDeCreditoDTO {

    private String nomeTitular;
    private String numero;
    private LocalDate validade;


    public CartaoDeCreditoDTO(CartaoDeCredito entidade) {
        if (entidade != null) {
            this.nomeTitular = entidade.getNomeTitular();
            this.numero = entidade.getNumero();
            this.validade = entidade.getValidade();
        }
    }
}