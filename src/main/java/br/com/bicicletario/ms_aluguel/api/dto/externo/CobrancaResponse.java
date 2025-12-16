package br.com.bicicletario.seumicrosservico.dto.externo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record CobrancaResponse(
        Long id,
        String status, // "PENDENTE", "PAGA", "FALHA"
        LocalDateTime horaSolicitacao,
        LocalDateTime horaFinalizacao,
        Double valor,

        @JsonProperty("ciclista")
        Long ciclistaId
) {}