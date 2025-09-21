package br.com.fiap3espg.spring_boot_project.usuario;

import br.com.fiap3espg.spring_boot_project.endereco.Endereco;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Boolean ativo = true;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String telefone;
    
    @Column(nullable = false, unique = true)
    private String cpf;
    
    @Column(nullable = false)
    private LocalDate dataNascimento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelVicio nivelVicio;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorGastoApostas = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal metaInvestimentoMensal = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private LocalDateTime dataUltimaAposta;
    
    @Column(nullable = false)
    private Integer diasSemApostar = 0;
    
    @Embedded
    private Endereco endereco;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    private LocalDateTime dataAtualizacao;

    public Usuario(DadosCadastroUsuario dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.cpf = dados.cpf();
        this.dataNascimento = dados.dataNascimento();
        this.nivelVicio = dados.nivelVicio();
        this.valorGastoApostas = dados.valorGastoApostas();
        this.metaInvestimentoMensal = dados.metaInvestimentoMensal();
        this.dataUltimaAposta = dados.dataUltimaAposta();
        this.endereco = new Endereco(dados.endereco());
        this.diasSemApostar = calcularDiasSemApostar(dados.dataUltimaAposta());
    }

    public void atualizarInformacoes(@Valid DadosAtualizacaoUsuario dados) {
        if(dados.nome() != null) {
            this.nome = dados.nome();
        }
        if(dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
        if(dados.endereco() != null) {
            this.endereco.atualizarInformacoes(dados.endereco());
        }
        if(dados.metaInvestimentoMensal() != null) {
            this.metaInvestimentoMensal = dados.metaInvestimentoMensal();
        }
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void excluir() {
        this.ativo = false;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void registrarAposta(BigDecimal valor) {
        this.valorGastoApostas = this.valorGastoApostas.add(valor);
        this.dataUltimaAposta = LocalDateTime.now();
        this.diasSemApostar = 0;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void atualizarDiasSemApostar() {
        if(this.dataUltimaAposta != null) {
            this.diasSemApostar = calcularDiasSemApostar(this.dataUltimaAposta);
        }
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    private Integer calcularDiasSemApostar(LocalDateTime dataUltimaAposta) {
        if(dataUltimaAposta == null) return 0;
        return (int) java.time.Duration.between(dataUltimaAposta, LocalDateTime.now()).toDays();
    }
}
