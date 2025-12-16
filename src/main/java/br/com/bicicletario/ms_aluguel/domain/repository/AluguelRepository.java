package br.com.bicicletario.ms_aluguel.domain.repository;

import br.com.bicicletario.ms_aluguel.domain.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {

    // Busca aluguel em aberto para o ciclista (Spring entende 'CiclistaId' navegando no objeto Ciclista)
    Optional<Aluguel> findByCiclistaIdAndDataHoraDevolucaoIsNull(Long idCiclista);

    // Busca aluguel em aberto para a bicicleta
    Optional<Aluguel> findByBicicletaIdAndDataHoraDevolucaoIsNull(Long bicicletaId);

}