package br.com.bicicletario.ms_aluguel.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

;

import static org.junit.jupiter.api.Assertions.*;

class AluguelTest {

    @Test
    void deveCriarAluguelCorretamente() {
        // 1. Preparar dados
        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);

        LocalDateTime agora = LocalDateTime.now();

        // 2. Testar Construtor Padrão + Setters (Lombok)
        Aluguel aluguel = new Aluguel();
        aluguel.setCiclista(ciclista);
        aluguel.setIdBicicleta(10L);
        aluguel.setIdTrancaInicio(20L);
        aluguel.setDataHoraRetirada(agora);

        // 3. Testar Setters adicionais
        aluguel.setId(99L);
        aluguel.setIdTrancaFim(30L);
        aluguel.setDataHoraDevolucao(agora.plusHours(1));
        aluguel.setValorCobrado(10.0);

        // 4. Testar Getters (Valida se o Lombok gerou tudo certo)
        assertEquals(99L, aluguel.getId());
        assertEquals(ciclista, aluguel.getCiclista());
        assertEquals(10L, aluguel.getIdBicicleta());
        assertEquals(20L, aluguel.getIdTrancaInicio());
        assertEquals(30L, aluguel.getIdTrancaFim());
        assertEquals(agora, aluguel.getDataHoraRetirada());
        assertEquals(agora.plusHours(1), aluguel.getDataHoraDevolucao());
        assertEquals(10.0, aluguel.getValorCobrado());
    }
}