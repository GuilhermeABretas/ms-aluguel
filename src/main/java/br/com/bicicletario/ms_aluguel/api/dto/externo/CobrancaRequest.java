package br.com.bicicletario.ms_aluguel.api.dto.externo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CobrancaRequest(
        @JsonProperty("valor")
        Double valor,

        @JsonProperty("ciclista")
        Long ciclistaId,

        // Opcional: só envie se quiser forçar um erro/sucesso específico nos testes
        @JsonProperty("numeroCartaoTeste")
        String numeroCartaoTeste
) {}