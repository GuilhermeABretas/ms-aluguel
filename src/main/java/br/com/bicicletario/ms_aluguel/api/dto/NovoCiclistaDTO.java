package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Nacionalidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

// DTO para a requisição de POST /ciclista
public class NovoCiclistaDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Nascimento é obrigatório")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate nascimento;

    // CPF só é validado no Service (R1)
    @CPF(message = "CPF inválido")
    private String cpf;

    // Passaporte só é validado no Service (R1)
    @Valid // Valida os campos dentro do DTO de passaporte
    private NovoPassaporteDTO passaporte;

    @NotNull(message = "Nacionalidade é obrigatória")
    private Nacionalidade nacionalidade; // BRASILEIRO ou ESTRANGEIRO

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    // UC01 - R1: "Foto do documento"
    @NotBlank(message = "URL da foto do documento é obrigatória")
    @URL(message = "URL da foto inválida")
    private String urlFotoDocumento;

    // O Swagger `POST /ciclista` pede o `meioDePagamento` junto.
    @NotNull(message = "Meio de pagamento é obrigatório")
    @Valid
    private NovoCartaoDeCreditoDTO meioDePagamento;

    // --- GETTERS E SETTERS MANUAIS ---

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

    public NovoPassaporteDTO getPassaporte() {
        return passaporte;
    }

    public void setPassaporte(NovoPassaporteDTO passaporte) {
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

    public NovoCartaoDeCreditoDTO getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(NovoCartaoDeCreditoDTO meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }
}