package br.com.fiap3espg.spring_boot_project.usuario;

public record DadosTokenJWT(
    String token,
    String tipo,
    Long usuarioId,
    String nome,
    String email
) {
    public DadosTokenJWT(String token, Long usuarioId, String nome, String email) {
        this(token, "Bearer", usuarioId, nome, email);
    }
}

