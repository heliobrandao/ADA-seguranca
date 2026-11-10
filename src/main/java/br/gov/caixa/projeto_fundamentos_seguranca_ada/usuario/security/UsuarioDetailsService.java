package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.security;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(UsuarioUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário ou senha inválidos."
                ));
    }
}