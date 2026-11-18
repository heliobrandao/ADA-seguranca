package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record AutenticacaoRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}