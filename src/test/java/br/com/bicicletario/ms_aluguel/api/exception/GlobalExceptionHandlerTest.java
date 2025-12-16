package br.com.bicicletario.ms_aluguel.api.exception;

import br.com.bicicletario.ms_aluguel.api.dto.ErroDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    // Instanciamos a classe real, pois é ELA que queremos testar
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleRecursoNaoEncontrado() {
        // GIVEN (Cenário)
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Ciclista não existe");

        // WHEN (Ação)
        ResponseEntity<ErroDTO> response = handler.handleRecursoNaoEncontrado(ex);

        // THEN (Verificação)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("NAO_ENCONTRADO", response.getBody().getCodigo());
        assertEquals("Ciclista não existe", response.getBody().getMensagem());
    }

    @Test
    void testHandleValidacaoException() {
        // GIVEN
        ValidacaoException ex = new ValidacaoException("Cartão inválido");

        // WHEN
        ResponseEntity<ErroDTO> response = handler.handleValidacaoException(ex);

        // THEN
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("REGRA_NEGOCIO_VIOLADA", response.getBody().getCodigo());
        assertEquals("Cartão inválido", response.getBody().getMensagem());
    }

    @Test
    void testHandleGenericException() {
        // GIVEN
        Exception ex = new RuntimeException("Erro de banco de dados");

        // WHEN
        ResponseEntity<ErroDTO> response = handler.handleGenericException(ex);

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO_INTERNO", response.getBody().getCodigo());
        assertEquals("Ocorreu um erro inesperado no servidor.", response.getBody().getMensagem());
    }

    @Test
    void testHandleBeanValidation() {
        // GIVEN - Esse é o mais chato, pois precisa mockar o erro do Spring
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dto", "email", "Email inválido");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Construtor do MethodArgumentNotValidException exige um BindingResult
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // WHEN
        ResponseEntity<List<ErroDTO>> response = handler.handleBeanValidation(ex);

        // THEN
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("EMAIL_INVALIDO", response.getBody().get(0).getCodigo());
        assertEquals("Email inválido", response.getBody().get(0).getMensagem());
    }
}