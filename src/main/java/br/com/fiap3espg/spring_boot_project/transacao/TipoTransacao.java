package br.com.fiap3espg.spring_boot_project.transacao;

public enum TipoTransacao {
    INVESTIMENTO("Investimento"),
    ECONOMIA("Economia"),
    APOSTA("Aposta"),
    RESGATE("Resgate");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
