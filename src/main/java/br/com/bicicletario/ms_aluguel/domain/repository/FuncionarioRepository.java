package br.com.bicicletario.ms_aluguel.domain.repository;

import br.com.bicicletario.ms_aluguel.domain.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    /**
     * Busca um funcionário pelo CPF.
     * (Útil para validação no cadastro)
     */
    Optional<Funcionario> findByCpf(String cpf);

    /**
     * Busca um funcionário pelo Email.
     * (Útil para validação no cadastro)
     */
    Optional<Funcionario> findByEmail(String email);
}