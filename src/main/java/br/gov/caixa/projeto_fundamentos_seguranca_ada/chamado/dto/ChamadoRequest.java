package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.PrioridadeChamado;

public record ChamadoRequest(
    @NotBlank @Size(min = 5, max = 100) String titulo,
    @NotBlank @Size(max = 2000) String descricao,
    @NotNull PrioridadeChamado prioridade) {
}