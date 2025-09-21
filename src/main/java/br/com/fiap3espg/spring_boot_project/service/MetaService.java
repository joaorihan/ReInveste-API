package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.meta.*;
import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import br.com.fiap3espg.spring_boot_project.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MetaService {

    @Autowired
    private MetaRepository metaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public DadosListagemMeta cadastrar(DadosCadastroMeta dados, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        validarDatasMeta(dados.dataInicio(), dados.dataFim());
        validarLimiteMetasAtivas(usuario);
        
        Meta meta = new Meta(dados, usuario);
        metaRepository.save(meta);
        
        return new DadosListagemMeta(meta);
    }

    public Page<DadosListagemMeta> listarPorUsuario(Long usuarioId, Pageable paginacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return metaRepository.findByUsuarioOrderByDataCriacaoDesc(usuario, paginacao)
                .map(DadosListagemMeta::new);
    }

    public Page<DadosListagemMeta> listarMetasAtivas(Long usuarioId, Pageable paginacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return metaRepository.findByUsuarioAndStatus(usuario, StatusMeta.ATIVA, paginacao)
                .map(DadosListagemMeta::new);
    }

    public DadosListagemMeta buscarPorId(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));
        
        return new DadosListagemMeta(meta);
    }

    public void atualizarProgresso(Long metaId, BigDecimal valorAdicional) {
        Meta meta = metaRepository.findById(metaId)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));
        
        if (!meta.getStatus().equals(StatusMeta.ATIVA)) {
            throw new RuntimeException("Meta não está ativa");
        }
        
        meta.atualizarProgresso(valorAdicional);
        
        if (meta.isConcluida()) {
            // Atualizar estatísticas do usuário se necessário
            atualizarEstatisticasUsuario(meta.getUsuario());
        }
    }

    public void cancelarMeta(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));
        
        meta.cancelar();
    }

    public List<DadosListagemMeta> buscarMetasVencidas() {
        List<Meta> metasVencidas = metaRepository.findMetasVencidas(LocalDate.now());
        return metasVencidas.stream()
                .map(DadosListagemMeta::new)
                .toList();
    }

    public Long contarMetasConcluidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return metaRepository.countMetasConcluidasByUsuario(usuario);
    }

    private void validarDatasMeta(LocalDate dataInicio, LocalDate dataFim) {
        if (dataFim.isBefore(dataInicio)) {
            throw new RuntimeException("Data de fim deve ser posterior à data de início");
        }
        
        if (dataInicio.isBefore(LocalDate.now())) {
            throw new RuntimeException("Data de início deve ser no futuro");
        }
    }

    private void validarLimiteMetasAtivas(Usuario usuario) {
        List<Meta> metasAtivas = metaRepository.findMetasAtivasByUsuario(usuario);
        if (metasAtivas.size() >= 5) {
            throw new RuntimeException("Limite máximo de 5 metas ativas por usuário");
        }
    }

    private void atualizarEstatisticasUsuario(Usuario usuario) {
        Long metasConcluidas = metaRepository.countMetasConcluidasByUsuario(usuario);
        // Aqui poderiam ser implementadas outras atualizações de estatísticas
    }
}
