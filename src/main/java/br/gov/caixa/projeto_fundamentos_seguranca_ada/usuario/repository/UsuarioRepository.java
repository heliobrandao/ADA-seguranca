package br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.repository;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByUsername(String username);

    Optional<Usuario> findByUsername(String username);
}