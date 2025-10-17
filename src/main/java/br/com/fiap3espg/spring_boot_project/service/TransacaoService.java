package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.exception.ResourceNotFoundException;
import br.com.fiap3espg.spring_boot_project.meta.Meta;
import br.com.fiap3espg.spring_boot_project.meta.MetaRepository;
import br.com.fiap3espg.spring_boot_project.service.validation.IValidationService;
import br.com.fiap3espg.spring_boot_project.transacao.*;
import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import br.com.fiap3espg.spring_boot_project.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransacaoService implements ITransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MetaRepository metaRepository;
    
    @Autowired
    private IValidationService validationService;

    @Override
    public DadosListagemTransacao cadastrar(DadosCadastroTransacao dados, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        Meta meta = null;
        if (dados.metaId() != null) {
            meta = metaRepository.findById(dados.metaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Meta", dados.metaId()));
        }
        
        validationService.validarTransacao(dados, usuario);
        
        Transacao transacao = new Transacao(dados, usuario, meta);
        transacaoRepository.save(transacao);
        
        // Se for uma aposta, atualizar dados do usuário
        if (dados.tipoTransacao().equals(TipoTransacao.APOSTA)) {
            usuario.registrarAposta(dados.valor());
        }
        
        return new DadosListagemTransacao(transacao);
    }

    @Override
    public Page<DadosListagemTransacao> listarPorUsuario(Long usuarioId, Pageable paginacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        return transacaoRepository.findByUsuarioOrderByDataTransacaoDesc(usuario, paginacao)
                .map(DadosListagemTransacao::new);
    }

    @Override
    public Page<DadosListagemTransacao> listarPorTipo(Long usuarioId, TipoTransacao tipo, Pageable paginacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        return transacaoRepository.findByUsuarioAndTipoTransacao(usuario, tipo, paginacao)
                .map(DadosListagemTransacao::new);
    }

    @Override
    public List<DadosListagemTransacao> listarPorPeriodo(Long usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        return transacaoRepository.findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim)
                .stream()
                .map(DadosListagemTransacao::new)
                .toList();
    }

    @Override
    public void confirmarTransacao(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação", id));
        
        transacao.confirmar();
        
        // Se for um investimento/economia, atualizar progresso da meta se existir
        if ((transacao.isInvestimento() || transacao.isEconomia()) && transacao.getMeta() != null) {
            transacao.getMeta().atualizarProgresso(transacao.getValor());
        }
    }

    @Override
    public void cancelarTransacao(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação", id));
        
        transacao.cancelar();
    }

    @Override
    public BigDecimal calcularTotalInvestimentos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        Double total = transacaoRepository.calcularTotalPorTipo(usuario, TipoTransacao.INVESTIMENTO);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcularTotalEconomias(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        Double total = transacaoRepository.calcularTotalPorTipo(usuario, TipoTransacao.ECONOMIA);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcularTotalApostas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        Double total = transacaoRepository.calcularTotalPorTipo(usuario, TipoTransacao.APOSTA);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    @Override
    public Long contarApostasRecentes(Long usuarioId, Integer dias) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(dias);
        return transacaoRepository.countApostasRecentes(usuario, dataInicio);
    }
}
