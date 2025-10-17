package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.transacao.DadosCadastroTransacao;
import br.com.fiap3espg.spring_boot_project.transacao.DadosListagemTransacao;
import br.com.fiap3espg.spring_boot_project.transacao.TipoTransacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ITransacaoService {
    
    DadosListagemTransacao cadastrar(DadosCadastroTransacao dados, Long usuarioId);
    
    Page<DadosListagemTransacao> listarPorUsuario(Long usuarioId, Pageable paginacao);
    
    Page<DadosListagemTransacao> listarPorTipo(Long usuarioId, TipoTransacao tipo, Pageable paginacao);
    
    List<DadosListagemTransacao> listarPorPeriodo(Long usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);
    
    void confirmarTransacao(Long id);
    
    void cancelarTransacao(Long id);
    
    BigDecimal calcularTotalInvestimentos(Long usuarioId);
    
    BigDecimal calcularTotalEconomias(Long usuarioId);
    
    BigDecimal calcularTotalApostas(Long usuarioId);
    
    Long contarApostasRecentes(Long usuarioId, Integer dias);
}

