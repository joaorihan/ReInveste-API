package br.com.fiap3espg.spring_boot_project.transacao;

import br.com.fiap3espg.spring_boot_project.meta.Meta;
import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "transacoes")
@Entity(name = "Transacao")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_id")
    private Meta meta;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipoTransacao;
    
    @Column(nullable = false)
    private String descricao;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status = StatusTransacao.PENDENTE;
    
    @Column(nullable = false)
    private LocalDateTime dataTransacao = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    private String observacoes;

    public Transacao(DadosCadastroTransacao dados, Usuario usuario, Meta meta) {
        this.usuario = usuario;
        this.meta = meta;
        this.tipoTransacao = dados.tipoTransacao();
        this.descricao = dados.descricao();
        this.valor = dados.valor();
        this.observacoes = dados.observacoes();
    }

    public void confirmar() {
        this.status = StatusTransacao.CONFIRMADA;
    }

    public void cancelar() {
        this.status = StatusTransacao.CANCELADA;
    }

    public boolean isInvestimento() {
        return TipoTransacao.INVESTIMENTO.equals(this.tipoTransacao);
    }

    public boolean isEconomia() {
        return TipoTransacao.ECONOMIA.equals(this.tipoTransacao);
    }

    public boolean isConfirmada() {
        return StatusTransacao.CONFIRMADA.equals(this.status);
    }
}
