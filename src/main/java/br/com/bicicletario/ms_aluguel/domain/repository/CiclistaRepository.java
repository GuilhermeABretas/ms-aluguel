package br.com.bicicletario.ms_aluguel.domain.repository;

import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CiclistaRepository extends JpaRepository<Ciclista, Long> {

    /**
     * Busca um ciclista pelo Email.
     * (Usado no UC01 - GET /ciclista/existeEmail/{email})
     */
    Optional<Ciclista> findByEmail(String email);

    /**
     * Busca um ciclista pelo CPF.
     * (Usado na validação do UC01)
     */
    Optional<Ciclista> findByCpf(String cpf);

}