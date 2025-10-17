package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.endereco.DadosEndereco;
import br.com.fiap3espg.spring_boot_project.exception.BusinessRuleException;
import br.com.fiap3espg.spring_boot_project.exception.DuplicateResourceException;
import br.com.fiap3espg.spring_boot_project.exception.ResourceNotFoundException;
import br.com.fiap3espg.spring_boot_project.service.validation.IValidationService;
import br.com.fiap3espg.spring_boot_project.usuario.*;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private IValidationService validationService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private DadosCadastroUsuario dadosCadastro;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        DadosEndereco endereco = new DadosEndereco(
                "Rua Teste", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310100"
        );

        dadosCadastro = new DadosCadastroUsuario(
                "João Silva",
                "joao@email.com",
                "senha123",
                "11987654321",
                "12345678901",
                LocalDate.of(1990, 1, 1),
                NivelVicio.MODERADO,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDateTime.now().minusDays(10),
                endereco
        );

        usuario = new Usuario(dadosCadastro);
        usuario.setSenha("$2a$10$hashedPassword");
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso")
    void deveCadastrarUsuarioComSucesso() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        doNothing().when(validationService).validarEmailUnico(anyString());
        doNothing().when(validationService).validarCpfUnico(anyString());

        // Act
        DadosListagemUsuario resultado = usuarioService.cadastrar(dadosCadastro);

        // Assert
        assertNotNull(resultado);
        assertEquals(dadosCadastro.nome(), resultado.nome());
        assertEquals(dadosCadastro.email(), resultado.email());
        verify(validationService).validarEmailUnico(dadosCadastro.email());
        verify(validationService).validarCpfUnico(dadosCadastro.cpf());
        verify(passwordEncoder).encode(dadosCadastro.senha());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com email duplicado")
    void naoDeveCadastrarUsuarioComEmailDuplicado() {
        // Arrange
        doThrow(new DuplicateResourceException("Usuário", "email", dadosCadastro.email()))
                .when(validationService).validarEmailUnico(anyString());

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            usuarioService.cadastrar(dadosCadastro);
        });
        verify(validationService).validarEmailUnico(dadosCadastro.email());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar usuários ativos com paginação")
    void deveListarUsuariosAtivosComPaginacao() {
        // Arrange
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Usuario> pageUsuarios = new PageImpl<>(List.of(usuario));
        when(usuarioRepository.findAllByAtivoTrue(paginacao)).thenReturn(pageUsuarios);

        // Act
        Page<DadosListagemUsuario> resultado = usuarioService.listar(paginacao);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(usuarioRepository).findAllByAtivoTrue(paginacao);
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        // Act
        DadosListagemUsuario resultado = usuarioService.buscarPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuario.getNome(), resultado.nome());
        verify(usuarioRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        // Arrange
        Long id = 999L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.buscarPorId(id);
        });
        verify(usuarioRepository).findById(id);
    }

    @Test
    @DisplayName("Deve excluir usuário (exclusão lógica)")
    void deveExcluirUsuarioLogicamente() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.excluir(id);

        // Assert
        assertFalse(usuario.getAtivo());
        verify(usuarioRepository).findById(id);
    }

    @Test
    @DisplayName("Deve registrar aposta do usuário")
    void deveRegistrarApostaDoUsuario() {
        // Arrange
        Long id = 1L;
        BigDecimal valorAposta = BigDecimal.valueOf(100);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        BigDecimal valorAnterior = usuario.getValorGastoApostas();

        // Act
        usuarioService.registrarAposta(id, valorAposta);

        // Assert
        assertEquals(valorAnterior.add(valorAposta), usuario.getValorGastoApostas());
        assertEquals(0, usuario.getDiasSemApostar());
        verify(usuarioRepository).findById(id);
    }

    @Test
    @DisplayName("Deve calcular média de gasto em apostas")
    void deveCalcularMediaGastoApostas() {
        // Arrange
        Double mediaEsperada = 1500.50;
        when(usuarioRepository.calcularMediaGastoApostas()).thenReturn(mediaEsperada);

        // Act
        BigDecimal resultado = usuarioService.calcularMediaGastoApostas();

        // Assert
        assertEquals(BigDecimal.valueOf(mediaEsperada), resultado);
        verify(usuarioRepository).calcularMediaGastoApostas();
    }

    @Test
    @DisplayName("Deve retornar zero quando não há média de gastos")
    void deveRetornarZeroQuandoNaoHaMediaDeGastos() {
        // Arrange
        when(usuarioRepository.calcularMediaGastoApostas()).thenReturn(null);

        // Act
        BigDecimal resultado = usuarioService.calcularMediaGastoApostas();

        // Assert
        assertEquals(BigDecimal.ZERO, resultado);
        verify(usuarioRepository).calcularMediaGastoApostas();
    }
}

