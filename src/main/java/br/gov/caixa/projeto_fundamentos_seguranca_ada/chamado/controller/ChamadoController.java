package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.Chamado;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public ResponseEntity<List<ChamadoResponse>> listarMeusChamados(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(chamadoService.listarMeusChamados(jwt.getSubject()));
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> criarChamado(
            @Valid @RequestBody ChamadoRequest chamadoRequest,
            @AuthenticationPrincipal Jwt jwt) {

        Chamado response = chamadoService.criarChamado(
                chamadoRequest,
                jwt.getSubject());

        return ResponseEntity.ok(new ChamadoResponse(
                response.getId(),
                response.getTitulo(),
                response.getDescricao(),
                response.getPrioridade(),
                response.getStatus(),
                response.getCriadoEm()));
    }

}
