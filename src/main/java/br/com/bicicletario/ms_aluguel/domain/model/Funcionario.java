package br.com.bicicletario.ms_aluguel.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "funcionarios")
@Getter
@Setter
@NoArgsConstructor // Lombok gera o construtor vazio aqui
@AllArgsConstructor // Lombok gera o construtor com todos os campos aqui
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


}