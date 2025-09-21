package br.com.fiap3espg.spring_boot_project.transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DadosListagemTransacao(
    Long id,
    Long usuarioId,
    Long metaId,
    TipoTransacao tipoTransacao,
    String descricao,
    BigDecimal valor,
    StatusTransacao status,
    LocalDateTime dataTransacao,
    LocalDateTime dataCriacao,
    String observacoes
) {
    public DadosListagemTransacao(Transacao transacao) {
        this(
            transacao.getId(),
            transacao.getUsuario().getId(),
            transacao.getMeta() != null ? transacao.getMeta().getId() : null,
            transacao.getTipoTransacao(),
            transacao.getDescricao(),
            transacao.getValor(),
            transacao.getStatus(),
            transacao.getDataTransacao(),
            transacao.getDataCriacao(),
            transacao.getObservacoes()
        );
    }
}
