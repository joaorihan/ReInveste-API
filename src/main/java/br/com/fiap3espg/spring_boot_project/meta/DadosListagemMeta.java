package br.com.fiap3espg.spring_boot_project.meta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DadosListagemMeta(
    Long id,
    Long usuarioId,
    TipoMeta tipoMeta,
    String descricao,
    BigDecimal valorAlvo,
    BigDecimal valorAtual,
    BigDecimal percentualConclusao,
    LocalDate dataInicio,
    LocalDate dataFim,
    StatusMeta status,
    LocalDateTime dataCriacao,
    LocalDateTime dataConclusao
) {
    public DadosListagemMeta(Meta meta) {
        this(
            meta.getId(),
            meta.getUsuario().getId(),
            meta.getTipoMeta(),
            meta.getDescricao(),
            meta.getValorAlvo(),
            meta.getValorAtual(),
            meta.getPercentualConclusao(),
            meta.getDataInicio(),
            meta.getDataFim(),
            meta.getStatus(),
            meta.getDataCriacao(),
            meta.getDataConclusao()
        );
    }
}
