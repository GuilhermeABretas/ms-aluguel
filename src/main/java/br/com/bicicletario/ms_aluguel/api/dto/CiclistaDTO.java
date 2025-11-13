package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import br.com.bicicletario.ms_aluguel.domain.model.Nacionalidade;
import br.com.bicicletario.ms_aluguel.domain.model.StatusCiclista;

import java.time.LocalDate;


public class CiclistaDTO {

    private Long id;
    private String nome;
    private LocalDate nascimento;
    private String cpf;
    // Não vamos expor o passaporte inteiro, talvez só o país
    private String paisPassaporte;
    private Nacionalidade nacionalidade;
    private String email;
    private String urlFotoDocumento;
    private StatusCiclista status;

    // Construtor padrão
    public CiclistaDTO() {
    }

    // Construtor para mapeamento fácil
    public CiclistaDTO(Ciclista entidade) {
        this.id = entidade.getId();
        this.nome = entidade.getNome();
        this.nascimento = entidade.getNascimento();
        this.cpf = entidade.getCpf();
        if (entidade.getPassaporte() != null) {
            this.paisPassaporte = entidade.getPassaporte().getPais();
        }
        this.nacionalidade = entidade.getNacionalidade();
        this.email = entidade.getEmail();
        this.urlFotoDocumento = entidade.getUrlFotoDocumento();
        this.status = entidade.getStatus();
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

    public String getPaisPassaporte() {
        return paisPassaporte;
    }

    public void setPaisPassaporte(String paisPassaporte) {
        this.paisPassaporte = paisPassaporte;
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
}