package br.com.bicicletario.ms_aluguel.domain.repository;

import br.com.bicicletario.ms_aluguel.domain.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {


    Optional<Aluguel> findByCiclistaIdAndDataHoraDevolucaoIsNull(Long idCiclista);


    @Query("SELECT a FROM Aluguel a WHERE a.idBicicleta = :idBicicleta AND a.dataHoraDevolucao IS NULL")
    Optional<Aluguel> findByBicicletaAndDevolucaoNull(@Param("idBicicleta") Long idBicicleta);

}