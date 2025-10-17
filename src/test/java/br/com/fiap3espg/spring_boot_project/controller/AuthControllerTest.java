package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.endereco.DadosEndereco;
import br.com.fiap3espg.spring_boot_project.usuario.DadosCadastroUsuario;
import br.com.fiap3espg.spring_boot_project.usuario.DadosLogin;
import br.com.fiap3espg.spring_boot_project.usuario.NivelVicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void deveRegistrarNovoUsuarioComSucesso() throws Exception {
        // Arrange
        DadosEndereco endereco = new DadosEndereco(
                "Rua Teste", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310100"
        );

        DadosCadastroUsuario dados = new DadosCadastroUsuario(
                "João Silva",
                "joao.teste@email.com",
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

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao.teste@email.com"));
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() throws Exception {
        // Arrange - Primeiro registra o usuário
        DadosEndereco endereco = new DadosEndereco(
                "Rua Teste", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310100"
        );

        DadosCadastroUsuario dadosCadastro = new DadosCadastroUsuario(
                "Maria Silva",
                "maria.login@email.com",
                "senha123",
                "11987654321",
                "98765432109",
                LocalDate.of(1990, 1, 1),
                NivelVicio.MODERADO,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDateTime.now().minusDays(10),
                endereco
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dadosCadastro)));

        DadosLogin dadosLogin = new DadosLogin("maria.login@email.com", "senha123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("maria.login@email.com"));
    }

    @Test
    @DisplayName("Não deve fazer login com senha incorreta")
    void naoDeveFazerLoginComSenhaIncorreta() throws Exception {
        // Arrange - Primeiro registra o usuário
        DadosEndereco endereco = new DadosEndereco(
                "Rua Teste", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310100"
        );

        DadosCadastroUsuario dadosCadastro = new DadosCadastroUsuario(
                "Pedro Silva",
                "pedro.senha@email.com",
                "senhaCorreta123",
                "11987654321",
                "11122233344",
                LocalDate.of(1990, 1, 1),
                NivelVicio.MODERADO,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDateTime.now().minusDays(10),
                endereco
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dadosCadastro)));

        DadosLogin dadosLogin = new DadosLogin("pedro.senha@email.com", "senhaErrada");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve registrar usuário com email duplicado")
    void naoDeveRegistrarUsuarioComEmailDuplicado() throws Exception {
        // Arrange
        DadosEndereco endereco = new DadosEndereco(
                "Rua Teste", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310100"
        );

        DadosCadastroUsuario dados = new DadosCadastroUsuario(
                "Ana Silva",
                "ana.duplicado@email.com",
                "senha123",
                "11987654321",
                "55566677788",
                LocalDate.of(1990, 1, 1),
                NivelVicio.MODERADO,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDateTime.now().minusDays(10),
                endereco
        );

        // Primeiro registro
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dados)));

        // Tentativa de segundo registro com mesmo email
        DadosCadastroUsuario dadosDuplicado = new DadosCadastroUsuario(
                "Ana Duplicada",
                "ana.duplicado@email.com",
                "senha456",
                "11987654321",
                "99988877766",
                LocalDate.of(1990, 1, 1),
                NivelVicio.MODERADO,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                LocalDateTime.now().minusDays(10),
                endereco
        );

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosDuplicado)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Não deve registrar usuário com dados inválidos")
    void naoDeveRegistrarUsuarioComDadosInvalidos() throws Exception {
        // Arrange - Dados sem email
        String jsonInvalido = "{\"nome\":\"Teste\",\"senha\":\"123\"}";

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }
}

