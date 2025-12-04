package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alugueis")
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

    // --- CONSTRUTOR PADRÃO (Obrigatório para o JPA) ---
    public Aluguel() {
    }

    // --- CONSTRUTOR COM ARGUMENTOS (Resolve Duplication e facilita Testes) ---
    public Aluguel(Ciclista ciclista, Long idBicicleta, Long idTrancaInicio, LocalDateTime dataHoraRetirada) {
        this.ciclista = ciclista;
        this.idBicicleta = idBicicleta;
        this.idTrancaInicio = idTrancaInicio;
        this.dataHoraRetirada = dataHoraRetirada;
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ciclista getCiclista() {
        return ciclista;
    }

    public void setCiclista(Ciclista ciclista) {
        this.ciclista = ciclista;
    }

    public Long getIdBicicleta() {
        return idBicicleta;
    }

    public void setIdBicicleta(Long idBicicleta) {
        this.idBicicleta = idBicicleta;
    }

    public Long getIdTrancaInicio() {
        return idTrancaInicio;
    }

    public void setIdTrancaInicio(Long idTrancaInicio) {
        this.idTrancaInicio = idTrancaInicio;
    }

    public Long getIdTrancaFim() {
        return idTrancaFim;
    }

    public void setIdTrancaFim(Long idTrancaFim) {
        this.idTrancaFim = idTrancaFim;
    }

    public LocalDateTime getDataHoraRetirada() {
        return dataHoraRetirada;
    }

    public void setDataHoraRetirada(LocalDateTime dataHoraRetirada) {
        this.dataHoraRetirada = dataHoraRetirada;
    }

    public LocalDateTime getDataHoraDevolucao() {
        return dataHoraDevolucao;
    }

    public void setDataHoraDevolucao(LocalDateTime dataHoraDevolucao) {
        this.dataHoraDevolucao = dataHoraDevolucao;
    }

    public Double getValorCobrado() {
        return valorCobrado;
    }

    public void setValorCobrado(Double valorCobrado) {
        this.valorCobrado = valorCobrado;
    }
}