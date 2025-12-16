package br.com.bicicletario.ms_aluguel.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AluguelTest {

    @Test
    void deveCriarAluguelCorretamente() {
        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);

        LocalDateTime agora = LocalDateTime.now();

        Aluguel aluguel = new Aluguel();
        aluguel.setCiclista(ciclista);
        aluguel.setBicicletaId(10L);
        aluguel.setTrancaInicioId(20L);
        aluguel.setDataHoraInicio(agora);

        aluguel.setId(99L);
        aluguel.setTrancaFimId(30L);
        aluguel.setDataHoraDevolucao(agora.plusHours(1));
        aluguel.setValorCobrado(10.0);

        assertEquals(99L, aluguel.getId());
        assertEquals(ciclista, aluguel.getCiclista());
        assertEquals(10L, aluguel.getBicicletaId());
        assertEquals(20L, aluguel.getTrancaInicioId());
        assertEquals(30L, aluguel.getTrancaFimId());
        assertEquals(agora, aluguel.getDataHoraInicio());
        assertEquals(agora.plusHours(1), aluguel.getDataHoraDevolucao());
        assertEquals(10.0, aluguel.getValorCobrado());
    }
}