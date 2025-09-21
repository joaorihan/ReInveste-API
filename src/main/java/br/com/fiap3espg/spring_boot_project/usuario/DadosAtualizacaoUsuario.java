package br.com.fiap3espg.spring_boot_project.usuario;

import br.com.fiap3espg.spring_boot_project.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DadosAtualizacaoUsuario(
    Long id,
    
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,
    
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
    String telefone,
    
    @DecimalMin(value = "0.0", message = "Meta de investimento mensal deve ser maior ou igual a zero")
    BigDecimal metaInvestimentoMensal,
    
    @Valid
    DadosEndereco endereco
) {}
