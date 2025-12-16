package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ciclistas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ciclista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate nascimento;

    @Column(unique = true)
    private String cpf;

    @Embedded
    private Passaporte passaporte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nacionalidade nacionalidade;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    private String urlFotoDocumento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCiclista status;

    private LocalDateTime dataConfirmacao;

    @OneToOne(mappedBy = "ciclista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private CartaoDeCredito cartaoDeCredito;
}