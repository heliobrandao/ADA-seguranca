package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.repository;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findAllByUsuario_IdOrderByCriadoEmDesc(Long usuarioId);

    List<Chamado> findAllByUsuario_UsernameOrderByCriadoEmDesc(String username);
}