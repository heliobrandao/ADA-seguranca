package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.repository;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.Chamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.StatusChamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findAllByUsuario_IdOrderByCriadoEmDesc(Long usuarioId);

    List<Chamado> findAllByUsuario_UsernameOrderByCriadoEmDesc(String username);

    Optional<Chamado> findByIdAndUsuario_Username(Long id, String username);

    @Query("""
            select c from Chamado c
            where c.usuario.username = :username
              and (:status is null or c.status = :status)
              and (:titulo is null or locate(lower(:titulo), lower(c.titulo)) > 0)
            order by c.criadoEm desc
            """)
    List<Chamado> buscarPorUsuarioEFiltros(
            @Param("username") String username,
            @Param("status") StatusChamado status,
            @Param("titulo") String titulo);
}