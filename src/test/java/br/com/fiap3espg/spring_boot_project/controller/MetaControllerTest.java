package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.endereco.DadosEndereco;
import br.com.fiap3espg.spring_boot_project.meta.DadosCadastroMeta;
import br.com.fiap3espg.spring_boot_project.meta.TipoMeta;
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
@DisplayName("Testes de Integração - MetaController")
class MetaControllerTest {

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
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        DadosEndereco endereco = new DadosEndereco(
                "Rua Teste", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310100"
        );

        DadosCadastroUsuario dados = new DadosCadastroUsuario(
                "Usuário Meta",
                "usuario.meta@email.com",
                "senha123",
                "11987654321",
                "99988877766",
                LocalDate.of(1990, 1, 1),
                NivelVicio.MODERADO,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDateTime.now().minusDays(10),
                endereco
        );

        usuario = new Usuario(dados);
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        usuarioRepository.save(usuario);

        token = jwtUtil.generateToken(usuario);
    }

    @Test
    @DisplayName("Deve cadastrar meta com autenticação")
    void deveCadastrarMetaComAutenticacao() throws Exception {
        DadosCadastroMeta dadosMeta = new DadosCadastroMeta(
                TipoMeta.INVESTIMENTO,
                "Meta de investimento mensal",
                BigDecimal.valueOf(5000),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(1)
        );

        mockMvc.perform(post("/metas/usuario/" + usuario.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosMeta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Meta de investimento mensal"))
                .andExpect(jsonPath("$.valorAlvo").value(5000));
    }

    @Test
    @DisplayName("Não deve cadastrar meta sem autenticação")
    void naoDeveCadastrarMetaSemAutenticacao() throws Exception {
        DadosCadastroMeta dadosMeta = new DadosCadastroMeta(
                TipoMeta.INVESTIMENTO,
                "Meta de investimento",
                BigDecimal.valueOf(5000),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(1)
        );

        mockMvc.perform(post("/metas/usuario/" + usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosMeta)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve listar metas por usuário")
    void deveListarMetasPorUsuario() throws Exception {
        mockMvc.perform(get("/metas/usuario/" + usuario.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve listar apenas metas ativas")
    void deveListarApenasMetasAtivas() throws Exception {
        mockMvc.perform(get("/metas/usuario/" + usuario.getId() + "/ativas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve buscar metas vencidas")
    void deveBuscarMetasVencidas() throws Exception {
        mockMvc.perform(get("/metas/vencidas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

