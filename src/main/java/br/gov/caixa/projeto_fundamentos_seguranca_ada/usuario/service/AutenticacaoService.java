package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.service;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.security.JwtService;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.AutenticacaoRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.AutenticacaoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AutenticacaoService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AutenticacaoResponse autenticar(AutenticacaoRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(),
                            request.password()
                    )
            );

            String token = jwtService.gerarToken(authentication);
            return new AutenticacaoResponse(token);
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário ou senha inválidos.",
                    exception
            );
        }
    }
}