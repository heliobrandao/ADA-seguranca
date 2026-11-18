package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.dto.ChamadoResponse;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.Chamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.PrioridadeChamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.repository.ChamadoRepository;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.service.ChamadoService;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.entity.Usuario;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ChamadoControllerListarMeusChamadosTest {

    @Autowired
    private ChamadoService chamadoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @BeforeEach
    void limparBanco() {
        chamadoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void deveListarSomenteChamadosDoUsuarioAutenticado() {
        Usuario ana = usuarioRepository.save(new Usuario("Ana Souza", "ana.souza", "hash-a"));
        Usuario bruno = usuarioRepository.save(new Usuario("Bruno Lima", "bruno.lima", "hash-b"));

        chamadoRepository.save(new Chamado(
                "Notebook muito lento",
                "Demora para ligar.",
                PrioridadeChamado.ALTA,
                ana));

        chamadoRepository.save(new Chamado(
                "Erro na impressora",
                "Nao imprime em rede.",
                PrioridadeChamado.MEDIA,
                bruno));

        List<ChamadoResponse> chamadosDaAna = chamadoService.listarMeusChamados("ana.souza", null, null);

        assertThat(chamadosDaAna).hasSize(1);
        assertThat(chamadosDaAna.get(0).titulo()).isEqualTo("Notebook muito lento");
        assertThat(chamadosDaAna.get(0).prioridade()).isEqualTo(PrioridadeChamado.ALTA);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemChamados() {
        usuarioRepository.save(new Usuario("Carlos Silva", "carlos.silva", "hash-c"));

        List<ChamadoResponse> chamados = chamadoService.listarMeusChamados("carlos.silva", null, null);

        assertThat(chamados).isEmpty();
    }
}
