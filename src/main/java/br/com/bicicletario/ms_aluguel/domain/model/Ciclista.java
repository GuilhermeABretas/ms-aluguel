package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ciclistas")
@Data
@AllArgsConstructor @NoArgsConstructor
public class Ciclista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate nascimento;

    @Column(unique = true) // CPF deve ser único (se brasileiro)
    private String cpf;

    @Embedded // Mapeia a classe Passaporte aqui
    private Passaporte passaporte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nacionalidade nacionalidade;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    // UC01 - R1: "Foto do documento"
    private String urlFotoDocumento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCiclista status;

    // UC02 - R3: "Registra data/hora da confirmação"
    private LocalDateTime dataConfirmacao;

    /**
     * Relacionamento Um-para-Um com CartaoDeCredito.
     * 'mappedBy = "ciclista"' indica que o CartaoDeCredito gerencia a chave estrangeira.
     */
    @OneToOne(mappedBy = "ciclista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CartaoDeCredito cartaoDeCredito;


}