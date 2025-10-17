package br.com.fiap3espg.spring_boot_project.service.validation;

import br.com.fiap3espg.spring_boot_project.transacao.DadosCadastroTransacao;
import br.com.fiap3espg.spring_boot_project.usuario.Usuario;

import java.time.LocalDate;

public interface IValidationService {
    
    void validarEmailUnico(String email);
    
    void validarCpfUnico(String cpf);
    
    void validarDatasMeta(LocalDate dataInicio, LocalDate dataFim);
    
    void validarLimiteMetasAtivas(Usuario usuario);
    
    void validarTransacao(DadosCadastroTransacao dados, Usuario usuario);
}

