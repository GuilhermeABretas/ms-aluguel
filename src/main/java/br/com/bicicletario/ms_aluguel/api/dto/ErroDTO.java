package br.com.bicicletario.ms_aluguel.api.dto;

import lombok.Getter;
// import lombok.AllArgsConstructor; // <- Removido

@Getter
public class ErroDTO {

    private String codigo;
    private String mensagem;


    public ErroDTO(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }
}