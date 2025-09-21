package br.com.fiap3espg.spring_boot_project.meta;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCadastroMeta(
    @NotNull(message = "Tipo da meta é obrigatório")
    TipoMeta tipoMeta,
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    String descricao,
    
    @NotNull(message = "Valor alvo é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor alvo deve ser maior que zero")
    BigDecimal valorAlvo,
    
    @NotNull(message = "Data de início é obrigatória")
    @Future(message = "Data de início deve ser no futuro")
    LocalDate dataInicio,
    
    @NotNull(message = "Data de fim é obrigatória")
    @Future(message = "Data de fim deve ser no futuro")
    LocalDate dataFim
) {
    public DadosCadastroMeta {
        if(dataFim != null && dataInicio != null && !dataFim.isAfter(dataInicio)) {
            throw new IllegalArgumentException("Data de fim deve ser posterior à data de início");
        }
    }
}
