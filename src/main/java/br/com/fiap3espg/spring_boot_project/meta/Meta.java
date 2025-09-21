package br.com.fiap3espg.spring_boot_project.meta;

import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "metas")
@Entity(name = "Meta")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMeta tipoMeta;
    
    @Column(nullable = false)
    private String descricao;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorAlvo;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorAtual = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private LocalDate dataInicio;
    
    @Column(nullable = false)
    private LocalDate dataFim;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMeta status = StatusMeta.ATIVA;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    private LocalDateTime dataConclusao;

    public Meta(DadosCadastroMeta dados, Usuario usuario) {
        this.usuario = usuario;
        this.tipoMeta = dados.tipoMeta();
        this.descricao = dados.descricao();
        this.valorAlvo = dados.valorAlvo();
        this.dataInicio = dados.dataInicio();
        this.dataFim = dados.dataFim();
    }

    public void atualizarProgresso(BigDecimal valorAdicional) {
        this.valorAtual = this.valorAtual.add(valorAdicional);
        
        if(this.valorAtual.compareTo(this.valorAlvo) >= 0) {
            this.status = StatusMeta.CONCLUIDA;
            this.dataConclusao = LocalDateTime.now();
        }
    }

    public void cancelar() {
        this.status = StatusMeta.CANCELADA;
    }

    public boolean isConcluida() {
        return StatusMeta.CONCLUIDA.equals(this.status);
    }

    public boolean isVencida() {
        return LocalDate.now().isAfter(this.dataFim) && !isConcluida();
    }

    public BigDecimal getPercentualConclusao() {
        if(valorAlvo.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return valorAtual.divide(valorAlvo, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .min(new BigDecimal("100"));
    }
}
