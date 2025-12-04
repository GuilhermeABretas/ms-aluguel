package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ciclistas")
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

    // --- CONSTRUTOR PADRÃO (Obrigatório para JPA) ---
    public Ciclista() {
    }

    // --- CONSTRUTOR COMPLETO (Facilita testes e evita Code Duplication) ---
    public Ciclista(Long id, String nome, LocalDate nascimento, String cpf, Passaporte passaporte,
                    Nacionalidade nacionalidade, String email, String senha, String urlFotoDocumento,
                    StatusCiclista status, LocalDateTime dataConfirmacao, CartaoDeCredito cartaoDeCredito) {
        this.id = id;
        this.nome = nome;
        this.nascimento = nascimento;
        this.cpf = cpf;
        this.passaporte = passaporte;
        this.nacionalidade = nacionalidade;
        this.email = email;
        this.senha = senha;
        this.urlFotoDocumento = urlFotoDocumento;
        this.status = status;
        this.dataConfirmacao = dataConfirmacao;
        this.cartaoDeCredito = cartaoDeCredito;
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Passaporte getPassaporte() {
        return passaporte;
    }

    public void setPassaporte(Passaporte passaporte) {
        this.passaporte = passaporte;
    }

    public Nacionalidade getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Nacionalidade nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUrlFotoDocumento() {
        return urlFotoDocumento;
    }

    public void setUrlFotoDocumento(String urlFotoDocumento) {
        this.urlFotoDocumento = urlFotoDocumento;
    }

    public StatusCiclista getStatus() {
        return status;
    }

    public void setStatus(StatusCiclista status) {
        this.status = status;
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }

    public void setDataConfirmacao(LocalDateTime dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }

    public CartaoDeCredito getCartaoDeCredito() {
        return cartaoDeCredito;
    }

    public void setCartaoDeCredito(CartaoDeCredito cartaoDeCredito) {
        this.cartaoDeCredito = cartaoDeCredito;
    }
}