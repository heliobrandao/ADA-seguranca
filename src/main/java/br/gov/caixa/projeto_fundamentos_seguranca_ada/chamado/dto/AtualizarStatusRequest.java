package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.StatusChamado;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(
        @NotNull StatusChamado status
) {
}