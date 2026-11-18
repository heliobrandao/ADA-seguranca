package br.gov.caixa.projeto_fundamentos_seguranca_ada.chamado.entity;

import br.gov.caixa.projeto_fundamentos_seguranca_ada.usuario.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "chamados")
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false, length = 100)
    private String titulo;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadeChamado prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusChamado status = StatusChamado.ABERTO;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    protected Chamado() {
    }

    public Chamado(
            String titulo,
            String descricao,
            PrioridadeChamado prioridade,
            Usuario usuario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.usuario = usuario;
    }

    public Chamado(
            String titulo,
            String descricao,
            String prioridade
    ) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = PrioridadeChamado.valueOf(prioridade);
    }

    @PrePersist
    void definirCriadoEm() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public PrioridadeChamado getPrioridade() {
        return prioridade;
    }

    public StatusChamado getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}