package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Funcao;
import br.com.bicicletario.ms_aluguel.domain.model.Funcionario;


public class FuncionarioDTO {

    private Long matricula; // No Swagger e UC15 é 'matricula'
    private String nome;
    private String email;
    private Integer idade;
    private String cpf;
    private Funcao funcao;

    /**
     * Construtor que facilita a conversão da Entidade para DTO.
     * (Isso será usado no Service)
     */
    public FuncionarioDTO(Funcionario entidade) {
        this.matricula = entidade.getId();
        this.nome = entidade.getNome();
        this.email = entidade.getEmail();
        this.idade = entidade.getIdade(); // Corrigido de getId() para getIdade()
        this.cpf = entidade.getCpf();
        this.funcao = entidade.getFuncao();
    }



    public Long getMatricula() {
        return matricula;
    }

    public void setMatricula(Long matricula) {
        this.matricula = matricula;
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