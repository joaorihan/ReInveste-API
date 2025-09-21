package br.com.fiap3espg.spring_boot_project.usuario;

public enum NivelVicio {
    BAIXO("Baixo - Aposta ocasionalmente"),
    MEDIO("Médio - Aposta regularmente"),
    ALTO("Alto - Aposta frequentemente"),
    CRITICO("Crítico - Aposta diariamente e tem problemas");

    private final String descricao;

    NivelVicio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
