package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.service;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.UsuarioCadastroRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.dto.UsuarioResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.entity.Usuario;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@Service
@Validated
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(@Valid UsuarioCadastroRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha deve possuir no máximo 72 bytes em UTF-8."
            );
        }

        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nome de usuário já está em uso."
            );
        }

        String senhaHash = passwordEncoder.encode(request.password());

        Usuario usuario = new Usuario(
                request.nome(),
                request.username(),
                senhaHash
        );

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                salvo.getId(),
                salvo.getNome(),
                salvo.getUsername()
        );
    }
}