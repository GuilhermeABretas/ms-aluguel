package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cartoes_de_credito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @ToString.Exclude // <--- CRUCIAL: Previne loop infinito (StackOverflowError)
    private Ciclista ciclista;

}