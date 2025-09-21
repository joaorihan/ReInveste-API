package br.com.fiap3espg.spring_boot_project.usuario;

import br.com.fiap3espg.spring_boot_project.endereco.Endereco;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DadosListagemUsuario(
    Long id,
    String nome,
    String email,
    String telefone,
    String cpf,
    LocalDate dataNascimento,
    NivelVicio nivelVicio,
    BigDecimal valorGastoApostas,
    BigDecimal metaInvestimentoMensal,
    LocalDateTime dataUltimaAposta,
    Integer diasSemApostar,
    Endereco endereco,
    Boolean ativo,
    LocalDateTime dataCriacao
) {
    public DadosListagemUsuario(Usuario usuario) {
        this(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTelefone(),
            usuario.getCpf(),
            usuario.getDataNascimento(),
            usuario.getNivelVicio(),
            usuario.getValorGastoApostas(),
            usuario.getMetaInvestimentoMensal(),
            usuario.getDataUltimaAposta(),
            usuario.getDiasSemApostar(),
            usuario.getEndereco(),
            usuario.getAtivo(),
            usuario.getDataCriacao()
        );
    }
}
