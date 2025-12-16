package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import lombok.*;

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
    private Long idBicicleta;

    @Column(nullable = false)
    private Long idTrancaInicio;

    private Long idTrancaFim;

    @Column(nullable = false)
    private LocalDateTime dataHoraRetirada;

    private LocalDateTime dataHoraDevolucao;

    private Double valorCobrado;

}