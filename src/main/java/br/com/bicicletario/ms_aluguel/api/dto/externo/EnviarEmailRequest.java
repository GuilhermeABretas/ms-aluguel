package br.com.bicicletario.seumicrosservico.dto.externo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EnviarEmailRequest(
        @JsonProperty("email")
        String emailDestino,

        @JsonProperty("assunto")
        String assunto,

        @JsonProperty("mensagem")
        String mensagem
) {}