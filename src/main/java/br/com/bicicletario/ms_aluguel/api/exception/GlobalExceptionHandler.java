package br.com.bicicletario.ms_aluguel.api.exception;

import br.com.bicicletario.ms_aluguel.api.dto.ErroDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroDTO> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        ErroDTO erro = new ErroDTO("NAO_ENCONTRADO", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }


    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroDTO> handleValidacaoException(ValidacaoException ex) {
        ErroDTO erro = new ErroDTO("REGRA_NEGOCIO_VIOLADA", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErroDTO>> handleBeanValidation(MethodArgumentNotValidException ex) {
        List<ErroDTO> erros = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String campo = fieldError.getField();
            String mensagem = fieldError.getDefaultMessage();
            erros.add(new ErroDTO(campo.toUpperCase() + "_INVALIDO", mensagem));
        }

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erros);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDTO> handleGenericException(Exception ex) {
        // Logar o erro em um sistema de log real é uma boa prática
        ex.printStackTrace();
        ErroDTO erro = new ErroDTO("ERRO_INTERNO", "Ocorreu um erro inesperado no servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}