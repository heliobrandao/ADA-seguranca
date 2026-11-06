package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroRequest(
        @NotBlank
        @Size(min = 2, max = 100)
        String nome,

        @NotBlank
        @Size(min = 3, max = 30)
        @Pattern(regexp = "^[A-Za-z0-9._]+$")
        String username,

        @NotBlank
        @Size(min = 8)
        String password
    ) {
}