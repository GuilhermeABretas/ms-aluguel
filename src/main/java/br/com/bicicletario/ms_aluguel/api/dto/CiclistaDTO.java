package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import br.com.bicicletario.ms_aluguel.domain.model.Nacionalidade;
import br.com.bicicletario.ms_aluguel.domain.model.StatusCiclista;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor // Gera o construtor vazio automaticamente
@AllArgsConstructor // Gera o construtor com todos os campos
public class CiclistaDTO {

    private Long id;
    private String nome;
    private LocalDate nascimento;
    private String cpf;

    private String paisPassaporte;
    private Nacionalidade nacionalidade;
    private String email;
    private String urlFotoDocumento;
    private StatusCiclista status;


    public CiclistaDTO(Ciclista entidade) {
        if (entidade != null) {
            this.id = entidade.getId();
            this.nome = entidade.getNome();
            this.nascimento = entidade.getNascimento();
            this.cpf = entidade.getCpf();

            // Lógica especial para evitar NullPointerException se não tiver passaporte
            if (entidade.getPassaporte() != null) {
                this.paisPassaporte = entidade.getPassaporte().getPais();
            }

            this.nacionalidade = entidade.getNacionalidade();
            this.email = entidade.getEmail();
            this.urlFotoDocumento = entidade.getUrlFotoDocumento();
            this.status = entidade.getStatus();
        }
    }
}