package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.endereco.DadosEndereco;
import br.com.fiap3espg.spring_boot_project.security.JwtUtil;
import br.com.fiap3espg.spring_boot_project.usuario.DadosCadastroUsuario;
import br.com.fiap3espg.spring_boot_project.usuario.NivelVicio;
import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import br.com.fiap3espg.spring_boot_project.usuario.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - UsuarioController")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String token;
    private Usuario usuarioAutenticado;

    @BeforeEach
    void setUp() {
        // Criar usuário para autenticação
        DadosEndereco endereco = new DadosEndereco(
                "Rua Teste", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310100"
        );

        DadosCadastroUsuario dados = new DadosCadastroUsuario(
                "Usuário Teste",
                "usuario.teste@email.com",
                "senha123",
                "11987654321",
                "12312312345",
                LocalDate.of(1990, 1, 1),
                NivelVicio.MODERADO,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDateTime.now().minusDays(10),
                endereco
        );

        usuarioAutenticado = new Usuario(dados);
        usuarioAutenticado.setSenha(passwordEncoder.encode(dados.senha()));
        usuarioRepository.save(usuarioAutenticado);

        token = jwtUtil.generateToken(usuarioAutenticado);
    }

    @Test
    @DisplayName("Deve listar usuários com autenticação")
    void deveListarUsuariosComAutenticacao() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Não deve listar usuários sem autenticação")
    void naoDeveListarUsuariosSemAutenticacao() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com autenticação")
    void deveBuscarUsuarioPorIdComAutenticacao() throws Exception {
        mockMvc.perform(get("/usuarios/" + usuarioAutenticado.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Usuário Teste"))
                .andExpect(jsonPath("$.email").value("usuario.teste@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar usuário inexistente")
    void deveRetornar404AoBuscarUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/usuarios/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve excluir usuário com autenticação")
    void deveExcluirUsuarioComAutenticacao() throws Exception {
        mockMvc.perform(delete("/usuarios/" + usuarioAutenticado.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve buscar usuários por nível de vício")
    void deveBuscarUsuariosPorNivelDeVicio() throws Exception {
        mockMvc.perform(get("/usuarios/nivel-vicio/MODERADO")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}

