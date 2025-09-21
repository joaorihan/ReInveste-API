package br.com.fiap3espg.spring_boot_project.meta;

public enum TipoMeta {
    INVESTIMENTO_MENSAL("Investimento Mensal"),
    ECONOMIA_DIARIA("Economia Diária"),
    DIAS_SEM_APOSTAR("Dias Sem Apostar"),
    VALOR_ECONOMIZADO("Valor Economizado"),
    INVESTIMENTO_ESPECIFICO("Investimento Específico");

    private final String descricao;

    TipoMeta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
