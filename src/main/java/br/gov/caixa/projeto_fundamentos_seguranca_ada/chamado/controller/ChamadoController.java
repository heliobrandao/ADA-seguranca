package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.AtualizarStatusRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.Chamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.StatusChamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.service.ChamadoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public ResponseEntity<List<ChamadoResponse>> listarMeusChamados(
            @RequestParam(required = false) StatusChamado status,
            @RequestParam(required = false) String titulo,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(chamadoService.listarMeusChamados(
                jwt.getSubject(), status, titulo));
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> criarChamado(
            @Valid @RequestBody ChamadoRequest chamadoRequest,
            @AuthenticationPrincipal Jwt jwt) {

        Chamado response = chamadoService.criarChamado(
                chamadoRequest,
                jwt.getSubject());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ChamadoResponse(
                response.getId(),
                response.getTitulo(),
                response.getDescricao(),
                response.getPrioridade(),
                response.getStatus(),
                response.getCriadoEm()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        ChamadoResponse response = chamadoService.buscarPorId(id, jwt.getSubject());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ChamadoResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Chamado response = chamadoService.atualizarStatus(id, jwt.getSubject(), request.status());
        return ResponseEntity.ok(new ChamadoResponse(
                response.getId(),
                response.getTitulo(),
                response.getDescricao(),
                response.getPrioridade(),
                response.getStatus(),
                response.getCriadoEm()));
    }

}
