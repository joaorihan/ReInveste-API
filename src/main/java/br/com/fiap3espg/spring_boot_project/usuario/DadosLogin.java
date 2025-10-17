package br.com.fiap3espg.spring_boot_project.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosLogin(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    String email,
    
    @NotBlank(message = "Senha é obrigatória")
    String senha
) {}

