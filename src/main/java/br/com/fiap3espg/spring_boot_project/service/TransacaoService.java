package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.meta.Meta;
import br.com.fiap3espg.spring_boot_project.meta.MetaRepository;
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
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MetaRepository metaRepository;

    public DadosListagemTransacao cadastrar(DadosCadastroTransacao dados, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Meta meta = null;
        if (dados.metaId() != null) {
            meta = metaRepository.findById(dados.metaId())
                    .orElseThrow(() -> new RuntimeException("Meta não encontrada"));
        }
        
        validarTransacao(dados, usuario);
        
        Transacao transacao = new Transacao(dados, usuario, meta);
        transacaoRepository.save(transacao);
        
        // Se for uma aposta, atualizar dados do usuário
        if (dados.tipoTransacao().equals(TipoTransacao.APOSTA)) {
            usuario.registrarAposta(dados.valor());
        }
        
        return new DadosListagemTransacao(transacao);
    }

    public Page<DadosListagemTransacao> listarPorUsuario(Long usuarioId, Pageable paginacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return transacaoRepository.findByUsuarioOrderByDataTransacaoDesc(usuario, paginacao)
                .map(DadosListagemTransacao::new);
    }

    public Page<DadosListagemTransacao> listarPorTipo(Long usuarioId, TipoTransacao tipo, Pageable paginacao) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return transacaoRepository.findByUsuarioAndTipoTransacao(usuario, tipo, paginacao)
                .map(DadosListagemTransacao::new);
    }

    public List<DadosListagemTransacao> listarPorPeriodo(Long usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return transacaoRepository.findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim)
                .stream()
                .map(DadosListagemTransacao::new)
                .toList();
    }

    public void confirmarTransacao(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        transacao.confirmar();
        
        // Se for um investimento/economia, atualizar progresso da meta se existir
        if ((transacao.isInvestimento() || transacao.isEconomia()) && transacao.getMeta() != null) {
            transacao.getMeta().atualizarProgresso(transacao.getValor());
        }
    }

    public void cancelarTransacao(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        transacao.cancelar();
    }

    public BigDecimal calcularTotalInvestimentos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Double total = transacaoRepository.calcularTotalPorTipo(usuario, TipoTransacao.INVESTIMENTO);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    public BigDecimal calcularTotalEconomias(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Double total = transacaoRepository.calcularTotalPorTipo(usuario, TipoTransacao.ECONOMIA);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    public BigDecimal calcularTotalApostas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Double total = transacaoRepository.calcularTotalPorTipo(usuario, TipoTransacao.APOSTA);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    public Long contarApostasRecentes(Long usuarioId, Integer dias) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(dias);
        return transacaoRepository.countApostasRecentes(usuario, dataInicio);
    }

    private void validarTransacao(DadosCadastroTransacao dados, Usuario usuario) {
        if (dados.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor da transação deve ser maior que zero");
        }
        
        // Validações específicas por tipo de transação
        switch (dados.tipoTransacao()) {
            case APOSTA:
                BigDecimal limiteAposta = usuario.getMetaInvestimentoMensal().multiply(new BigDecimal("2"));
                if (dados.valor().compareTo(limiteAposta) > 0) {
                    throw new RuntimeException("Valor da aposta muito alto em relação à meta de investimento");
                }
                break;
            case INVESTIMENTO:
                if (dados.metaId() == null) {
                    throw new RuntimeException("Investimentos devem estar associados a uma meta");
                }
                break;
        }
    }
}
