package br.gov.caixa.projeto_fundamentos_seguranca_ada.exception;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Requisição inválida.");
        return corpo(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException ex) {
        Throwable causa = ex.getCause();
        if (causa instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            String campo = ife.getPath().isEmpty() ? "campo"
                    : ife.getPath().get(ife.getPath().size() - 1).getPropertyName();
            String permitidos = Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            return corpo(HttpStatus.BAD_REQUEST,
                    campo + ": valor '" + ife.getValue() + "' inválido. Valores permitidos: " + permitidos + ".");
        }
        return corpo(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formado.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> status(ResponseStatusException ex) {
        return corpo(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> generico(Exception ex) {
        return corpo(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    private ResponseEntity<Map<String, Object>> corpo(HttpStatus status, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("erro", status.getReasonPhrase());
        body.put("mensagem", mensagem);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        Class<?> tipo = ex.getRequiredType();
        String mensagem = ex.getName() + ": valor '" + ex.getValue() + "' inválido.";
        if (tipo != null && tipo.isEnum()) {
            mensagem += " Valores permitidos: " + Arrays.stream(tipo.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", ")) + ".";
        }
        return corpo(HttpStatus.BAD_REQUEST, mensagem);
    }
}