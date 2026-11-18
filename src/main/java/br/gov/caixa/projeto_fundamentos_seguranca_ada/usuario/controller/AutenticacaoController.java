package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.controller;

import java.util.Map;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.AutenticacaoRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.AutenticacaoResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.service.AutenticacaoService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> autenticar(
            @Valid @RequestBody AutenticacaoRequest request) {
        try {
            AutenticacaoResponse response = autenticacaoService.autenticar(request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException exception) {
            if (!HttpStatus.UNAUTHORIZED.equals(exception.getStatusCode())) {
                throw exception;
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Usuário ou senha inválidos."));
        }
    }
}