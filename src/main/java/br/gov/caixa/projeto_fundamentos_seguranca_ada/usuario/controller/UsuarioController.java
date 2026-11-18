package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.controller;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.UsuarioCadastroRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.UsuarioResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody UsuarioCadastroRequest request) {

        UsuarioResponse response = usuarioService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}