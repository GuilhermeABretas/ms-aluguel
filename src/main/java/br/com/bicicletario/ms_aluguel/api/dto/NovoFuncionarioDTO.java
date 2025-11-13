package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Funcao;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
// import lombok.Getter; // Removido
// import lombok.Setter; // Removido
import org.hibernate.validator.constraints.br.CPF;

// DTO para POST /funcionario e PUT /funcionario/{id}
// @Getter // Removido
// @Setter // Removido
public class NovoFuncionarioDTO {

    @NotBlank(message = "Nome não pode estar em branco")
    private String nome;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Senha não pode estar em branco")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotNull(message = "Idade não pode ser nula")
    @Min(value = 18, message = "Funcionário deve ser maior de 18 anos")
    private Integer idade;

    @NotBlank(message = "CPF não pode estar em branco")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotNull(message = "Função não pode ser nula")
    private Funcao funcao; // (ADMINISTRATIVO ou REPARADOR)

    // --- GETTERS E SETTERS MANUAIS ---

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