package br.com.bicicletario.ms_aluguel.api.exception;

import br.com.bicicletario.ms_aluguel.api.dto.ErroDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    // --- NOVO MÉTODO PARA ERROS DE JSON (ENUMs, Datas inválidas, etc) ---
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroDTO> handleJsonErrors(HttpMessageNotReadableException ex) {
        String msg = ex.getMessage();
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            msg = ex.getCause().getMessage();
            // Remove detalhes técnicos excessivos do Jackson, se houver
            if (msg.contains("problem:")) {
                msg = msg.substring(0, msg.indexOf("problem:"));
            }
        }

        ErroDTO erro = new ErroDTO("MENSAGEM_INVALIDA", "Formato do JSON inválido ou tipo de dado incorreto. Detalhe: " + msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDTO> handleGenericException(Exception ex) {
        // Logar o erro no console para ajudar no debug
        ex.printStackTrace();

        ErroDTO erro = new ErroDTO("ERRO_INTERNO", "Ocorreu um erro inesperado no servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}