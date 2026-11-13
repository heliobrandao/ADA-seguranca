package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto;

import jakarta.validation.constraints.NotBlank;

public record ChamadoRequest(   
    @NotBlank String titulo,
    @NotBlank String descricao,
    @NotBlank String prioridade) {
}
