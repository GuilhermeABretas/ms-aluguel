package br.com.bicicletario.ms_aluguel.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.bicicletario.ms_aluguel.domain.model.Ciclista;
import br.com.bicicletario.ms_aluguel.domain.model.Passaporte;
import br.com.bicicletario.ms_aluguel.domain.model.Nacionalidade;
import br.com.bicicletario.ms_aluguel.domain.model.StatusCiclista;

import static org.junit.jupiter.api.Assertions.*;

class CiclistaTest {

    @Test
    void deveCriarCiclistaCorretamente() {
        // 1. Preparar objetos auxiliares
        Passaporte passaporte = new Passaporte();
        passaporte.setNumero("AB123456");

        LocalDate nascimento = LocalDate.of(2000, 1, 1);
        LocalDateTime agora = LocalDateTime.now();

        // 2. Testar Construtor Padrão + Setters (Lombok)
        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setNascimento(nascimento);
        ciclista.setCpf("123.456.789-00");
        ciclista.setPassaporte(passaporte);
        ciclista.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista.setEmail("joao@teste.com");
        ciclista.setSenha("senha123");
        ciclista.setUrlFotoDocumento("http://foto.com/doc.jpg");
        ciclista.setStatus(StatusCiclista.ATIVO);
        ciclista.setDataConfirmacao(agora);

        // 3. Testar Getters (Garante Coverage)
        assertEquals(1L, ciclista.getId());
        assertEquals("João Silva", ciclista.getNome());
        assertEquals(nascimento, ciclista.getNascimento());
        assertEquals("123.456.789-00", ciclista.getCpf());
        assertEquals(passaporte, ciclista.getPassaporte());
        assertEquals(Nacionalidade.BRASILEIRO, ciclista.getNacionalidade());
        assertEquals("joao@teste.com", ciclista.getEmail());
        assertEquals("senha123", ciclista.getSenha());
        assertEquals("http://foto.com/doc.jpg", ciclista.getUrlFotoDocumento());
        assertEquals(StatusCiclista.ATIVO, ciclista.getStatus());
        assertEquals(agora, ciclista.getDataConfirmacao());
    }
}