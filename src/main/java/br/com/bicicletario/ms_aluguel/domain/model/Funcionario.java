package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// import lombok.AllArgsConstructor; // Removido
// import lombok.Getter; // Removido
// import lombok.NoArgsConstructor; // Removido
// import lombok.Setter; // Removido
import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "funcionarios")
// @Getter // Removido
// @Setter // Removido
// @NoArgsConstructor // Removido
// @AllArgsConstructor // Removido
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome não pode estar em branco")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Senha não pode estar em branco")
    @Column(nullable = false)
    private String senha;

    @NotNull(message = "Idade não pode ser nula")
    @Min(value = 18, message = "Funcionário deve ser maior de 18 anos")
    @Column(nullable = false)
    private Integer idade;

    @NotBlank(message = "CPF não pode estar em branco")
    @CPF(message = "CPF inválido")
    @Column(nullable = false, unique = true)
    private String cpf;

    @NotNull(message = "Função não pode ser nula")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Funcao funcao;

    // --- CONSTRUTORES MANUAIS ---

    /**
     * Construtor padrão (JPA precisa)
     */
    public Funcionario() {
    }

    /**
     * Construtor completo (útil para testes)
     */
    public Funcionario(Long id, String nome, String email, String senha, Integer idade, String cpf, Funcao funcao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.idade = idade;
        this.cpf = cpf;
        this.funcao = funcao;
    }

    // --- GETTERS E SETTERS MANUAIS ---

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

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }
}