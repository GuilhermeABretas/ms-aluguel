package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import br.com.bicicletario.ms_aluguel.domain.model.Nacionalidade;
import br.com.bicicletario.ms_aluguel.domain.model.Passaporte;
import br.com.bicicletario.ms_aluguel.domain.model.StatusCiclista;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CiclistaDTO {

    private Long id;
    private StatusCiclista status;
    private String nome;
    private LocalDate nascimento;
    private String cpf;
    private PassaporteDTO passaporte;
    private Nacionalidade nacionalidade;
    private String email;
    private String urlFotoDocumento;

    public CiclistaDTO(Ciclista entidade) {
        if (entidade != null) {
            this.id = entidade.getId();
            this.status = entidade.getStatus();
            this.nome = entidade.getNome();
            this.nascimento = entidade.getNascimento();
            this.cpf = entidade.getCpf();
            this.nacionalidade = entidade.getNacionalidade();
            this.email = entidade.getEmail();
            this.urlFotoDocumento = entidade.getUrlFotoDocumento();

            if (entidade.getPassaporte() != null) {
                this.passaporte = new PassaporteDTO(entidade.getPassaporte());
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassaporteDTO {
        private String numero;
        private LocalDate validade;
        private String pais;

        public PassaporteDTO(Passaporte p) {
            this.numero = p.getNumero();
            this.validade = p.getValidade();
            this.pais = p.getPais();
        }
    }
}