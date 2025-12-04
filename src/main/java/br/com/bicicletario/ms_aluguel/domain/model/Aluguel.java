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
    private Long idBicicleta; // ID vindo do MS Equipamento

    @Column(nullable = false)
    private Long idTrancaInicio; // ID vindo do MS Equipamento

    private Long idTrancaFim; // Preenchido na devolução

    @Column(nullable = false)
    private LocalDateTime dataHoraRetirada;

    private LocalDateTime dataHoraDevolucao; // Preenchido na devolução

    private Double valorCobrado; // Preenchido na devolução

}