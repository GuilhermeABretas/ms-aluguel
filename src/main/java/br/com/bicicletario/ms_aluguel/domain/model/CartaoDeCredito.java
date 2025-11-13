package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cartoes_de_credito")
@AllArgsConstructor @NoArgsConstructor
@Data
public class CartaoDeCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeTitular;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private LocalDate validade;

    @Column(nullable = false)
    private String cvv;

    /**
     * Define a relação: Um Cartão pertence a Um Ciclista.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ciclista_id", referencedColumnName = "id")
    private Ciclista ciclista;

}