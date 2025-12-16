package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Funcao;
import br.com.bicicletario.ms_aluguel.domain.model.Funcionario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private Funcao funcao;
    private int idade;
    private String documento;

    public FuncionarioDTO(Funcionario entidade) {
        if (entidade != null) {
            this.id = entidade.getId();
            this.nome = entidade.getNome();
            this.email = entidade.getEmail();
            this.cpf = entidade.getCpf();
            this.funcao = entidade.getFuncao();
            this.idade = entidade.getIdade();
            this.documento = entidade.getDocumento();
        }
    }
}