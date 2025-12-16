package br.com.bicicletario.ms_aluguel.domain.repository;

import br.com.bicicletario.ms_aluguel.domain.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {


    Optional<Aluguel> findByCiclistaIdAndDataHoraDevolucaoIsNull(Long idCiclista);


    Optional<Aluguel> findByBicicletaIdAndDataHoraDevolucaoIsNull(Long bicicletaId);

}