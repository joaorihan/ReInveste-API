package br.com.fiap3espg.spring_boot_project.transacao;

public enum StatusTransacao {
    PENDENTE("Pendente"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
