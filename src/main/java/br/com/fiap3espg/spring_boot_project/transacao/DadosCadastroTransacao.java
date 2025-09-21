package br.com.fiap3espg.spring_boot_project.transacao;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DadosCadastroTransacao(
    @NotNull(message = "Tipo da transação é obrigatório")
    TipoTransacao tipoTransacao,
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    String descricao,
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    BigDecimal valor,
    
    @NotNull(message = "Data da transação é obrigatória")
    @Past(message = "Data da transação deve ser no passado")
    LocalDateTime dataTransacao,
    
    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    String observacoes,
    
    Long metaId
) {}
