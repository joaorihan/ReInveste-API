package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.exception.BusinessRuleException;
import br.com.fiap3espg.spring_boot_project.exception.ResourceNotFoundException;
import br.com.fiap3espg.spring_boot_project.meta.*;
import br.com.fiap3espg.spring_boot_project.service.validation.IValidationService;
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
public class MetaService implements IMetaService {

    @Autowired
    private MetaRepository metaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private IValidationService validationService;

    @Override
    public DadosListagemMeta cadastrar(DadosCadastroMeta dados, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        validationService.validarDatasMeta(dados.dataInicio(), dados.dataFim());
        validationService.validarLimiteMetasAtivas(usuario);
        
        Meta meta = new Meta(dados, usuario);
        metaRepository.save(meta);
        
        return new DadosListagemMeta(meta);
    }

    @Override
    public Page<DadosListagemMeta> listarPorUsuario(Long usuarioId, Pageable paginacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        return metaRepository.findByUsuarioOrderByDataCriacaoDesc(usuario, paginacao)
                .map(DadosListagemMeta::new);
    }

    @Override
    public Page<DadosListagemMeta> listarMetasAtivas(Long usuarioId, Pageable paginacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        return metaRepository.findByUsuarioAndStatus(usuario, StatusMeta.ATIVA, paginacao)
                .map(DadosListagemMeta::new);
    }

    @Override
    public DadosListagemMeta buscarPorId(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta", id));
        
        return new DadosListagemMeta(meta);
    }

    @Override
    public void atualizarProgresso(Long metaId, BigDecimal valorAdicional) {
        Meta meta = metaRepository.findById(metaId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta", metaId));
        
        if (!meta.getStatus().equals(StatusMeta.ATIVA)) {
            throw new BusinessRuleException("Meta não está ativa e não pode ter seu progresso atualizado");
        }
        
        meta.atualizarProgresso(valorAdicional);
        
        if (meta.isConcluida()) {
            // Atualizar estatísticas do usuário se necessário
            atualizarEstatisticasUsuario(meta.getUsuario());
        }
    }

    @Override
    public void cancelarMeta(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta", id));
        
        meta.cancelar();
    }

    @Override
    public List<DadosListagemMeta> buscarMetasVencidas() {
        List<Meta> metasVencidas = metaRepository.findMetasVencidas(LocalDate.now());
        return metasVencidas.stream()
                .map(DadosListagemMeta::new)
                .toList();
    }

    @Override
    public Long contarMetasConcluidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        return metaRepository.countMetasConcluidasByUsuario(usuario);
    }

    private void atualizarEstatisticasUsuario(Usuario usuario) {
        Long metasConcluidas = metaRepository.countMetasConcluidasByUsuario(usuario);
        // Aqui poderiam ser implementadas outras atualizações de estatísticas
    }
}
