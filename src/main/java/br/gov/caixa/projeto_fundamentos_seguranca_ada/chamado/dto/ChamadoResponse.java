package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.PrioridadeChamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.StatusChamado;

import java.time.LocalDateTime;

public record ChamadoResponse(
        Long id,
        String titulo,
        String descricao,
        PrioridadeChamado prioridade,
        StatusChamado status,
        LocalDateTime criadoEm) {
}