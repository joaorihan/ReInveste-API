package br.com.fiap3espg.spring_boot_project.meta;

public enum StatusMeta {
    ATIVA("Ativa"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada"),
    VENCIDA("Vencida");

    private final String descricao;

    StatusMeta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
