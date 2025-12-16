package br.com.bicicletario.ms_aluguel.domain.repository;

import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CiclistaRepository extends JpaRepository<Ciclista, Long> {


    Optional<Ciclista> findByEmail(String email);


    Optional<Ciclista> findByCpf(String cpf);

}