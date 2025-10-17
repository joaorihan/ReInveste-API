package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.exception.ResourceNotFoundException;
import br.com.fiap3espg.spring_boot_project.meta.Meta;
import br.com.fiap3espg.spring_boot_project.meta.MetaRepository;
import br.com.fiap3espg.spring_boot_project.service.validation.IValidationService;
import br.com.fiap3espg.spring_boot_project.transacao.*;
import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import br.com.fiap3espg.spring_boot_project.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do TransacaoService")
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private IValidationService validationService;

    @InjectMocks
    private TransacaoService transacaoService;

    private Usuario usuario;
    private Meta meta;
    private Transacao transacao;
    private DadosCadastroTransacao dadosCadastroTransacao;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        meta = new Meta();

        dadosCadastroTransacao = new DadosCadastroTransacao(
                TipoTransacao.INVESTIMENTO,
                "Investimento em ações",
                BigDecimal.valueOf(1000),
                1L,
                "Investimento inicial"
        );

        transacao = new Transacao(dadosCadastroTransacao, usuario, meta);
    }

    @Test
    @DisplayName("Deve cadastrar transação com sucesso")
    void deveCadastrarTransacaoComSucesso() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(metaRepository.findById(dadosCadastroTransacao.metaId())).thenReturn(Optional.of(meta));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacao);
        doNothing().when(validationService).validarTransacao(any(), any());

        // Act
        DadosListagemTransacao resultado = transacaoService.cadastrar(dadosCadastroTransacao, usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(dadosCadastroTransacao.descricao(), resultado.descricao());
        verify(usuarioRepository).findById(usuarioId);
        verify(metaRepository).findById(dadosCadastroTransacao.metaId());
        verify(validationService).validarTransacao(dadosCadastroTransacao, usuario);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve cadastrar aposta e atualizar dados do usuário")
    void deveCadastrarApostaEAtualizarDadosDoUsuario() {
        // Arrange
        Long usuarioId = 1L;
        DadosCadastroTransacao dadosAposta = new DadosCadastroTransacao(
                TipoTransacao.APOSTA,
                "Aposta em futebol",
                BigDecimal.valueOf(100),
                null,
                "Aposta online"
        );
        Transacao apostaTransacao = new Transacao(dadosAposta, usuario, null);
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(apostaTransacao);
        doNothing().when(validationService).validarTransacao(any(), any());

        // Act
        transacaoService.cadastrar(dadosAposta, usuarioId);

        // Assert
        verify(usuarioRepository).findById(usuarioId);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Não deve cadastrar transação para usuário inexistente")
    void naoDeveCadastrarTransacaoParaUsuarioInexistente() {
        // Arrange
        Long usuarioId = 999L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            transacaoService.cadastrar(dadosCadastroTransacao, usuarioId);
        });
        verify(usuarioRepository).findById(usuarioId);
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar transações por usuário")
    void deveListarTransacoesPorUsuario() {
        // Arrange
        Long usuarioId = 1L;
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Transacao> pageTransacoes = new PageImpl<>(List.of(transacao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.findByUsuarioOrderByDataTransacaoDesc(usuario, paginacao))
                .thenReturn(pageTransacoes);

        // Act
        Page<DadosListagemTransacao> resultado = transacaoService.listarPorUsuario(usuarioId, paginacao);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(transacaoRepository).findByUsuarioOrderByDataTransacaoDesc(usuario, paginacao);
    }

    @Test
    @DisplayName("Deve listar transações por tipo")
    void deveListarTransacoesPorTipo() {
        // Arrange
        Long usuarioId = 1L;
        TipoTransacao tipo = TipoTransacao.INVESTIMENTO;
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Transacao> pageTransacoes = new PageImpl<>(List.of(transacao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.findByUsuarioAndTipoTransacao(usuario, tipo, paginacao))
                .thenReturn(pageTransacoes);

        // Act
        Page<DadosListagemTransacao> resultado = transacaoService.listarPorTipo(usuarioId, tipo, paginacao);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(transacaoRepository).findByUsuarioAndTipoTransacao(usuario, tipo, paginacao);
    }

    @Test
    @DisplayName("Deve confirmar transação")
    void deveConfirmarTransacao() {
        // Arrange
        Long transacaoId = 1L;
        when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacao));

        // Act
        transacaoService.confirmarTransacao(transacaoId);

        // Assert
        assertEquals(StatusTransacao.CONFIRMADA, transacao.getStatus());
        verify(transacaoRepository).findById(transacaoId);
    }

    @Test
    @DisplayName("Deve cancelar transação")
    void deveCancelarTransacao() {
        // Arrange
        Long transacaoId = 1L;
        when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacao));

        // Act
        transacaoService.cancelarTransacao(transacaoId);

        // Assert
        assertEquals(StatusTransacao.CANCELADA, transacao.getStatus());
        verify(transacaoRepository).findById(transacaoId);
    }

    @Test
    @DisplayName("Deve calcular total de investimentos")
    void deveCalcularTotalDeInvestimentos() {
        // Arrange
        Long usuarioId = 1L;
        Double totalEsperado = 5000.00;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.calcularTotalPorTipo(usuario, TipoTransacao.INVESTIMENTO))
                .thenReturn(totalEsperado);

        // Act
        BigDecimal resultado = transacaoService.calcularTotalInvestimentos(usuarioId);

        // Assert
        assertEquals(BigDecimal.valueOf(totalEsperado), resultado);
        verify(transacaoRepository).calcularTotalPorTipo(usuario, TipoTransacao.INVESTIMENTO);
    }

    @Test
    @DisplayName("Deve retornar zero quando não há investimentos")
    void deveRetornarZeroQuandoNaoHaInvestimentos() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.calcularTotalPorTipo(usuario, TipoTransacao.INVESTIMENTO))
                .thenReturn(null);

        // Act
        BigDecimal resultado = transacaoService.calcularTotalInvestimentos(usuarioId);

        // Assert
        assertEquals(BigDecimal.ZERO, resultado);
        verify(transacaoRepository).calcularTotalPorTipo(usuario, TipoTransacao.INVESTIMENTO);
    }

    @Test
    @DisplayName("Deve contar apostas recentes")
    void deveContarApostasRecentes() {
        // Arrange
        Long usuarioId = 1L;
        Integer dias = 30;
        Long expectedCount = 5L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.countApostasRecentes(eq(usuario), any(LocalDateTime.class)))
                .thenReturn(expectedCount);

        // Act
        Long resultado = transacaoService.contarApostasRecentes(usuarioId, dias);

        // Assert
        assertEquals(expectedCount, resultado);
        verify(transacaoRepository).countApostasRecentes(eq(usuario), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve listar transações por período")
    void deveListarTransacoesPorPeriodo() {
        // Arrange
        Long usuarioId = 1L;
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
        LocalDateTime dataFim = LocalDateTime.now();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim))
                .thenReturn(List.of(transacao));

        // Act
        List<DadosListagemTransacao> resultado = transacaoService.listarPorPeriodo(usuarioId, dataInicio, dataFim);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(transacaoRepository).findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim);
    }
}

