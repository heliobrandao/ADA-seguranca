package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.Chamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.PrioridadeChamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity.StatusChamado;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.repository.ChamadoRepository;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.security.JwtService;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.entity.Usuario;
import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:chamados-filtros;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.h2.console.enabled=false",
        "spring.jpa.show-sql=false",
        "security.jwt.secret=chave-exclusiva-de-testes-com-mais-de-32-bytes"
})
@AutoConfigureMockMvc
@Transactional
class ChamadoFiltroIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void prepararChamados() {
        Usuario usuario = usuarioRepository.save(new Usuario("Usuario A", "usuario.a", "hash-de-teste"));
        Usuario outroUsuario = usuarioRepository.save(new Usuario("Usuario B", "usuario.b", "hash-de-teste"));

        for (Usuario dono : new Usuario[] {usuario, outroUsuario}) {
            salvarChamado("Impressora sem papel", StatusChamado.ABERTO, dono);
            salvarChamado("Impressora sem tinta", StatusChamado.FECHADO, dono);
            salvarChamado("Acesso ao sistema", StatusChamado.EM_ANDAMENTO, dono);
        }

        entityManager.clear();
        token = jwtService.gerarToken(usuario.getUsername());
    }

    @Test
    void semFiltrosRetornaSomenteChamadosDoUsuarioAutenticado() throws Exception {
        mockMvc.perform(requisicaoAutenticada())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].titulo", containsInAnyOrder(
                        "Impressora sem papel", "Impressora sem tinta", "Acesso ao sistema")));
    }

    @ParameterizedTest
    @CsvSource({
            "ABERTO, , Impressora sem papel",
            "FECHADO, , Impressora sem tinta",
            "EM_ANDAMENTO, , Acesso ao sistema",
            ", SISTEMA, Acesso ao sistema",
            "ABERTO, impressora, Impressora sem papel",
            "FECHADO, IMPRESSORA, Impressora sem tinta"
    })
    void aplicaFiltrosOpcionaisSemRetornarChamadosDeOutroUsuario(
            String statusFiltro, String titulo, String tituloEsperado) throws Exception {
        MockHttpServletRequestBuilder requisicao = requisicaoAutenticada();
        if (statusFiltro != null) {
            requisicao.param("status", statusFiltro);
        }
        if (titulo != null) {
            requisicao.param("titulo", titulo);
        }

        mockMvc.perform(requisicao)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value(tituloEsperado));
    }

    @Test
    void filtraPorParteDoTituloSemDiferenciarMaiusculas() throws Exception {
        mockMvc.perform(requisicaoAutenticada().param("titulo", "PRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].titulo", containsInAnyOrder(
                        "Impressora sem papel", "Impressora sem tinta")));
    }

    @Test
    void filtrosCombinadosExigemQueAmbosSejamAtendidos() throws Exception {
        mockMvc.perform(requisicaoAutenticada()
                        .param("status", "EM_ANDAMENTO")
                        .param("titulo", "impressora"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"inexistente", "' OR '1'='1", "%", "_"})
    void tituloSemCorrespondenciaOuComCaracteresSqlNaoAmpliaBusca(String titulo) throws Exception {
        mockMvc.perform(requisicaoAutenticada().param("titulo", titulo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void trataAspasPercentualESublinhadoComoTextoLiteral() throws Exception {
        Usuario usuario = usuarioRepository.findByUsername("usuario.a").orElseThrow();
        salvarChamado("Falha d'impressora 100%_teste", StatusChamado.ABERTO, usuario);
        entityManager.clear();

        mockMvc.perform(requisicaoAutenticada().param("titulo", "'impressora 100%_"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Falha d'impressora 100%_teste"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALIDO", "aberto", "ABERTO' OR '1'='1"})
    void statusInvalidoRetornaBadRequest(String statusInvalido) throws Exception {
        mockMvc.perform(requisicaoAutenticada().param("status", statusInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void usuarioSemChamadosRecebeListaVazia() throws Exception {
        usuarioRepository.saveAndFlush(new Usuario(
                "Usuario sem chamados", "usuario.sem.chamados", "hash-de-teste"));
        String tokenSemChamados = jwtService.gerarToken("usuario.sem.chamados");

        mockMvc.perform(get("/chamados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSemChamados))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void requisicaoSemTokenRetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/chamados").param("status", "ABERTO"))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpServletRequestBuilder requisicaoAutenticada() {
        return get("/chamados").header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    private void salvarChamado(String titulo, StatusChamado status, Usuario usuario) {
        Chamado chamado = chamadoRepository.saveAndFlush(new Chamado(
                titulo, "Descricao do problema", PrioridadeChamado.MEDIA, usuario));
        entityManager.createQuery("update Chamado c set c.status = :status where c.id = :id")
                .setParameter("status", status)
                .setParameter("id", chamado.getId())
                .executeUpdate();
    }
}
