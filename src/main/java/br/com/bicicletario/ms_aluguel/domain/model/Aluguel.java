package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alugueis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ciclista_id", nullable = false)
    private Ciclista ciclista;

    @Column(nullable = false)
    private Long bicicletaId;

    @Column(nullable = false)
    private Long trancaInicioId;

    private Long trancaFimId;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    private LocalDateTime dataHoraDevolucao;

    private Double valorCobrado;
}