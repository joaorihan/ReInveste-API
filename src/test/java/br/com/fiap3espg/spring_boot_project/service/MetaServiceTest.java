package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.exception.BusinessRuleException;
import br.com.fiap3espg.spring_boot_project.exception.ResourceNotFoundException;
import br.com.fiap3espg.spring_boot_project.meta.*;
import br.com.fiap3espg.spring_boot_project.service.validation.IValidationService;
import br.com.fiap3espg.spring_boot_project.usuario.NivelVicio;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do MetaService")
class MetaServiceTest {

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private IValidationService validationService;

    @InjectMocks
    private MetaService metaService;

    private Usuario usuario;
    private Meta meta;
    private DadosCadastroMeta dadosCadastroMeta;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        
        dadosCadastroMeta = new DadosCadastroMeta(
                TipoMeta.INVESTIMENTO,
                "Meta de investimento mensal",
                BigDecimal.valueOf(5000),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(1)
        );

        meta = new Meta(dadosCadastroMeta, usuario);
    }

    @Test
    @DisplayName("Deve cadastrar meta com sucesso")
    void deveCadastrarMetaComSucesso() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(metaRepository.save(any(Meta.class))).thenReturn(meta);
        doNothing().when(validationService).validarDatasMeta(any(), any());
        doNothing().when(validationService).validarLimiteMetasAtivas(any());

        // Act
        DadosListagemMeta resultado = metaService.cadastrar(dadosCadastroMeta, usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(dadosCadastroMeta.descricao(), resultado.descricao());
        verify(usuarioRepository).findById(usuarioId);
        verify(validationService).validarDatasMeta(dadosCadastroMeta.dataInicio(), dadosCadastroMeta.dataFim());
        verify(validationService).validarLimiteMetasAtivas(usuario);
        verify(metaRepository).save(any(Meta.class));
    }

    @Test
    @DisplayName("Não deve cadastrar meta para usuário inexistente")
    void naoDeveCadastrarMetaParaUsuarioInexistente() {
        // Arrange
        Long usuarioId = 999L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            metaService.cadastrar(dadosCadastroMeta, usuarioId);
        });
        verify(usuarioRepository).findById(usuarioId);
        verify(metaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar metas por usuário")
    void deveListarMetasPorUsuario() {
        // Arrange
        Long usuarioId = 1L;
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Meta> pageMetas = new PageImpl<>(List.of(meta));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(metaRepository.findByUsuarioOrderByDataCriacaoDesc(usuario, paginacao)).thenReturn(pageMetas);

        // Act
        Page<DadosListagemMeta> resultado = metaService.listarPorUsuario(usuarioId, paginacao);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(metaRepository).findByUsuarioOrderByDataCriacaoDesc(usuario, paginacao);
    }

    @Test
    @DisplayName("Deve buscar meta por ID")
    void deveBuscarMetaPorId() {
        // Arrange
        Long id = 1L;
        when(metaRepository.findById(id)).thenReturn(Optional.of(meta));

        // Act
        DadosListagemMeta resultado = metaService.buscarPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(meta.getDescricao(), resultado.descricao());
        verify(metaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar progresso da meta")
    void deveAtualizarProgressoDaMeta() {
        // Arrange
        Long metaId = 1L;
        BigDecimal valorAdicional = BigDecimal.valueOf(1000);
        when(metaRepository.findById(metaId)).thenReturn(Optional.of(meta));
        when(metaRepository.countMetasConcluidasByUsuario(usuario)).thenReturn(1L);

        // Act
        metaService.atualizarProgresso(metaId, valorAdicional);

        // Assert
        assertEquals(valorAdicional, meta.getValorAtual());
        verify(metaRepository).findById(metaId);
    }

    @Test
    @DisplayName("Não deve atualizar progresso de meta inativa")
    void naoDeveAtualizarProgressoDeMetaInativa() {
        // Arrange
        Long metaId = 1L;
        meta.cancelar(); // Meta inativa
        BigDecimal valorAdicional = BigDecimal.valueOf(1000);
        when(metaRepository.findById(metaId)).thenReturn(Optional.of(meta));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> {
            metaService.atualizarProgresso(metaId, valorAdicional);
        });
        verify(metaRepository).findById(metaId);
    }

    @Test
    @DisplayName("Deve cancelar meta")
    void deveCancelarMeta() {
        // Arrange
        Long metaId = 1L;
        when(metaRepository.findById(metaId)).thenReturn(Optional.of(meta));

        // Act
        metaService.cancelarMeta(metaId);

        // Assert
        assertEquals(StatusMeta.CANCELADA, meta.getStatus());
        verify(metaRepository).findById(metaId);
    }

    @Test
    @DisplayName("Deve buscar metas vencidas")
    void deveBuscarMetasVencidas() {
        // Arrange
        when(metaRepository.findMetasVencidas(any(LocalDate.class))).thenReturn(List.of(meta));

        // Act
        List<DadosListagemMeta> resultado = metaService.buscarMetasVencidas();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(metaRepository).findMetasVencidas(any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve contar metas concluídas por usuário")
    void deveContarMetasConcluidasPorUsuario() {
        // Arrange
        Long usuarioId = 1L;
        Long esperado = 5L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(metaRepository.countMetasConcluidasByUsuario(usuario)).thenReturn(esperado);

        // Act
        Long resultado = metaService.contarMetasConcluidas(usuarioId);

        // Assert
        assertEquals(esperado, resultado);
        verify(metaRepository).countMetasConcluidasByUsuario(usuario);
    }
}

