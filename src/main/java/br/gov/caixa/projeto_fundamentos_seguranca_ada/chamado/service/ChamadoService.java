package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.service;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoRequest;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.Chamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.PrioridadeChamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.StatusChamado;
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
    public List<ChamadoResponse> listarMeusChamados(
            String username, StatusChamado status, String titulo) {
        return chamadoRepository
                .buscarPorUsuarioEFiltros(username, status, titulo)
                .stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChamadoResponse consultarMeuChamado(Long id, String username) {
        return paraResposta(buscarChamadoDoUsuario(id, username));
    }

    @Transactional
    public ChamadoResponse atualizarStatus(Long id, String username, StatusChamado novoStatus) {
        Chamado chamado = buscarChamadoDoUsuario(id, username);
        if (chamado.getStatus() == StatusChamado.FECHADO && novoStatus == StatusChamado.ABERTO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Um chamado fechado não pode voltar para aberto.");
        }

        chamado.atualizarStatus(novoStatus);
        return paraResposta(chamadoRepository.save(chamado));
    }

    public Chamado criarChamado(ChamadoRequest request, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário autenticado não encontrado."));

        Chamado chamado = new Chamado(
                request.titulo(),
                request.descricao(),
                request.prioridade(),
                usuario);

        return chamadoRepository.save(chamado);
    }

    private Chamado buscarChamadoDoUsuario(Long id, String username) {
        return chamadoRepository.buscarPorIdEUsuario(id, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Chamado não encontrado."));
    }

    private ChamadoResponse paraResposta(Chamado chamado) {
        return new ChamadoResponse(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getPrioridade(),
                chamado.getStatus(),
                chamado.getCriadoEm());
    }
}