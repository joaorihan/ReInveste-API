package br.com.fiap3espg.spring_boot_project.usuario;

import br.com.fiap3espg.spring_boot_project.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DadosCadastroUsuario(
    @NotBlank(message = "Nome é obrigatório")
    String nome,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    String email,
    
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
    String telefone,
    
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    String cpf,
    
    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    LocalDate dataNascimento,
    
    @NotNull(message = "Nível do vício é obrigatório")
    NivelVicio nivelVicio,
    
    @DecimalMin(value = "0.0", message = "Valor gasto em apostas deve ser maior ou igual a zero")
    BigDecimal valorGastoApostas,
    
    @DecimalMin(value = "0.0", message = "Meta de investimento mensal deve ser maior ou igual a zero")
    BigDecimal metaInvestimentoMensal,
    
    @Past(message = "Data da última aposta deve ser no passado")
    LocalDateTime dataUltimaAposta,
    
    @Valid
    DadosEndereco endereco
) {}
