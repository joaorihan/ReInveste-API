package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.meta.DadosCadastroMeta;
import br.com.fiap3espg.spring_boot_project.meta.DadosListagemMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IMetaService {
    
    DadosListagemMeta cadastrar(DadosCadastroMeta dados, Long usuarioId);
    
    Page<DadosListagemMeta> listarPorUsuario(Long usuarioId, Pageable paginacao);
    
    Page<DadosListagemMeta> listarMetasAtivas(Long usuarioId, Pageable paginacao);
    
    DadosListagemMeta buscarPorId(Long id);
    
    void atualizarProgresso(Long metaId, BigDecimal valorAdicional);
    
    void cancelarMeta(Long id);
    
    List<DadosListagemMeta> buscarMetasVencidas();
    
    Long contarMetasConcluidas(Long usuarioId);
}

