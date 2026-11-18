package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.service;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.Chamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.PrioridadeChamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.repository.ChamadoRepository;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.entity.Usuario;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioRepository usuarioRepository;

    public ChamadoService(ChamadoRepository chamadoRepository, UsuarioRepository usuarioRepository) {
        this.chamadoRepository = chamadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponse> listarMeusChamados(String username) {
        return chamadoRepository
                .findAllByUsuario_UsernameOrderByCriadoEmDesc(username)
                .stream()
                .map(chamado -> new ChamadoResponse(
                        chamado.getId(),
                        chamado.getTitulo(),
                        chamado.getDescricao(),
                        chamado.getPrioridade(),
                        chamado.getStatus(),
                        chamado.getCriadoEm()))
                .toList();
    }

    public Chamado criarChamado(ChamadoRequest request, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário autenticado não encontrado."));

        Chamado chamado = new Chamado(
                request.titulo(),
                request.descricao(),
                PrioridadeChamado.valueOf(request.prioridade()),
                usuario);

        return chamadoRepository.save(chamado);
    }
}