package br.com.bicicletario.ms_aluguel.api.dto.externo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record ValidacaoCartaoRequest(
        @JsonProperty("nomeTitular")
        String nomeTitular,

        @JsonProperty("numero")
        String numero,

        @JsonProperty("validade")
        @JsonFormat(pattern = "yyyy-MM-dd") // O formato exato que o ms-externo exige
        LocalDate validade,

        @JsonProperty("cvv")
        String cvv
) {}