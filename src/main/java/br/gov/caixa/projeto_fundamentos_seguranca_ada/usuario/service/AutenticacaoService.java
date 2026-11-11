package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.service;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.security.JwtService;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.AutenticacaoRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.AutenticacaoResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.entity.Usuario;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticacaoService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AutenticacaoResponse autenticar(AutenticacaoRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .filter(encontrado -> passwordEncoder.matches(
                        request.password(),
                        encontrado.getSenhaHash()
                ))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário ou senha inválidos."
                ));

        String token = jwtService.gerarToken(usuario.getUsername());
        return new AutenticacaoResponse(token);
    }
}