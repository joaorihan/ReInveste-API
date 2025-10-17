package br.com.fiap3espg.spring_boot_project.service.validation;

import br.com.fiap3espg.spring_boot_project.exception.BusinessRuleException;
import br.com.fiap3espg.spring_boot_project.exception.DuplicateResourceException;
import br.com.fiap3espg.spring_boot_project.meta.Meta;
import br.com.fiap3espg.spring_boot_project.meta.MetaRepository;
import br.com.fiap3espg.spring_boot_project.transacao.DadosCadastroTransacao;
import br.com.fiap3espg.spring_boot_project.transacao.TipoTransacao;
import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import br.com.fiap3espg.spring_boot_project.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ValidationService implements IValidationService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MetaRepository metaRepository;

    @Override
    public void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Usuário", "email", email);
        }
    }

    @Override
    public void validarCpfUnico(String cpf) {
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new DuplicateResourceException("Usuário", "CPF", cpf);
        }
    }

    @Override
    public void validarDatasMeta(LocalDate dataInicio, LocalDate dataFim) {
        if (dataFim.isBefore(dataInicio)) {
            throw new BusinessRuleException("Data de fim deve ser posterior à data de início");
        }
        
        if (dataInicio.isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Data de início deve ser no futuro");
        }
    }

    @Override
    public void validarLimiteMetasAtivas(Usuario usuario) {
        List<Meta> metasAtivas = metaRepository.findMetasAtivasByUsuario(usuario);
        if (metasAtivas.size() >= 5) {
            throw new BusinessRuleException("Limite máximo de 5 metas ativas por usuário atingido");
        }
    }

    @Override
    public void validarTransacao(DadosCadastroTransacao dados, Usuario usuario) {
        if (dados.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Valor da transação deve ser maior que zero");
        }
        
        // Validações específicas por tipo de transação
        switch (dados.tipoTransacao()) {
            case APOSTA:
                validarAposta(dados.valor(), usuario);
                break;
            case INVESTIMENTO:
                validarInvestimento(dados);
                break;
            case ECONOMIA:
                validarEconomia(dados.valor());
                break;
            case RESGATE:
                validarResgate(dados);
                break;
            default:
                throw new BusinessRuleException("Tipo de transação inválido");
        }
    }
    
    private void validarAposta(BigDecimal valorAposta, Usuario usuario) {
        BigDecimal limiteAposta = usuario.getMetaInvestimentoMensal().multiply(new BigDecimal("2"));
        if (valorAposta.compareTo(limiteAposta) > 0) {
            throw new BusinessRuleException(
                String.format("Valor da aposta (R$ %.2f) muito alto em relação à meta de investimento (limite: R$ %.2f)", 
                    valorAposta, limiteAposta)
            );
        }
    }
    
    private void validarInvestimento(DadosCadastroTransacao dados) {
        if (dados.metaId() == null) {
            throw new BusinessRuleException("Investimentos devem estar associados a uma meta");
        }
    }
    
    private void validarEconomia(BigDecimal valorEconomia) {
        // Economia não pode ser um valor muito alto (possivelmente erro de digitação)
        BigDecimal limiteEconomia = new BigDecimal("100000.00");
        if (valorEconomia.compareTo(limiteEconomia) > 0) {
            throw new BusinessRuleException(
                String.format("Valor de economia (R$ %.2f) parece muito alto. Verifique o valor informado.", 
                    valorEconomia)
            );
        }
    }
    
    private void validarResgate(DadosCadastroTransacao dados) {
        if (dados.metaId() == null) {
            throw new BusinessRuleException("Resgates devem estar associados a uma meta de investimento");
        }
    }
}

