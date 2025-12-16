package br.com.bicicletario.ms_aluguel.api.dto;

import br.com.bicicletario.ms_aluguel.domain.model.Nacionalidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NovoCiclistaDTO {

    @NotNull(message = "Dados do ciclista são obrigatórios")
    @Valid
    private DadosCiclista ciclista;

    @NotNull(message = "Meio de pagamento é obrigatório")
    @Valid
    private NovoCartaoDeCreditoDTO meioDePagamento;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DadosCiclista {
        @NotBlank(message = "Nome é obrigatório")
        private String nome;

        @NotNull(message = "Nascimento é obrigatório")
        @Past(message = "Data de nascimento deve ser no passado")
        private LocalDate nascimento;

        @CPF(message = "CPF inválido")
        private String cpf;

        @Valid
        private NovoPassaporteDTO passaporte;

        @NotNull(message = "Nacionalidade é obrigatória")
        private Nacionalidade nacionalidade; // BRASILEIRO ou ESTRANGEIRO

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        private String senha;

        @NotBlank(message = "URL da foto do documento é obrigatória")
        @URL(message = "URL da foto inválida")
        private String urlFotoDocumento;
    }
}