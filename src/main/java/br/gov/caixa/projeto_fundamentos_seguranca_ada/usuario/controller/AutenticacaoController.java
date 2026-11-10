package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.controller;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.AutenticacaoRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.AutenticacaoResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.service.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/authenticate")
    public AutenticacaoResponse autenticar(
            @Valid @RequestBody AutenticacaoRequest request) {
        return autenticacaoService.autenticar(request);
    }
}