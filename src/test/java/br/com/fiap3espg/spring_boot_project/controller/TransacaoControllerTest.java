package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.endereco.DadosEndereco;
import br.com.fiap3espg.spring_boot_project.meta.DadosCadastroMeta;
import br.com.fiap3espg.spring_boot_project.meta.Meta;
import br.com.fiap3espg.spring_boot_project.meta.MetaRepository;
import br.com.fiap3espg.spring_boot_project.meta.TipoMeta;
import br.com.fiap3espg.spring_boot_project.security.JwtUtil;
import br.com.fiap3espg.spring_boot_project.transacao.DadosCadastroTransacao;
import br.com.fiap3espg.spring_boot_project.transacao.TipoTransacao;
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
@DisplayName("Testes de Integração - TransacaoController")
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String token;
    private Usuario usuario;
    private Meta meta;

    @BeforeEach
    void setUp() {
        DadosEndereco endereco = new DadosEndereco(
                "Rua Teste", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310100"
        );

        DadosCadastroUsuario dados = new DadosCadastroUsuario(
                "Usuário Transação",
                "usuario.transacao@email.com",
                "senha123",
                "11987654321",
                "11122233344",
                LocalDate.of(1990, 1, 1),
                NivelVicio.MODERADO,
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(1000),
                LocalDateTime.now().minusDays(10),
                endereco
        );

        usuario = new Usuario(dados);
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        usuarioRepository.save(usuario);

        // Criar uma meta para o usuário
        DadosCadastroMeta dadosMeta = new DadosCadastroMeta(
                TipoMeta.INVESTIMENTO,
                "Meta para testes",
                BigDecimal.valueOf(5000),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(1)
        );
        meta = new Meta(dadosMeta, usuario);
        metaRepository.save(meta);

        token = jwtUtil.generateToken(usuario);
    }

    @Test
    @DisplayName("Deve cadastrar transação de investimento com autenticação")
    void deveCadastrarTransacaoDeInvestimentoComAutenticacao() throws Exception {
        DadosCadastroTransacao dadosTransacao = new DadosCadastroTransacao(
                TipoTransacao.INVESTIMENTO,
                "Investimento em ações",
                BigDecimal.valueOf(1000),
                meta.getId(),
                "Investimento inicial"
        );

        mockMvc.perform(post("/transacoes/usuario/" + usuario.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosTransacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Investimento em ações"))
                .andExpect(jsonPath("$.valor").value(1000));
    }

    @Test
    @DisplayName("Deve cadastrar transação de economia")
    void deveCadastrarTransacaoDeEconomia() throws Exception {
        DadosCadastroTransacao dadosTransacao = new DadosCadastroTransacao(
                TipoTransacao.ECONOMIA,
                "Economia mensal",
                BigDecimal.valueOf(500),
                null,
                "Economia do mês"
        );

        mockMvc.perform(post("/transacoes/usuario/" + usuario.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosTransacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoTransacao").value("ECONOMIA"));
    }

    @Test
    @DisplayName("Não deve cadastrar transação sem autenticação")
    void naoDeveCadastrarTransacaoSemAutenticacao() throws Exception {
        DadosCadastroTransacao dadosTransacao = new DadosCadastroTransacao(
                TipoTransacao.INVESTIMENTO,
                "Investimento",
                BigDecimal.valueOf(1000),
                meta.getId(),
                "Teste"
        );

        mockMvc.perform(post("/transacoes/usuario/" + usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosTransacao)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve listar transações por usuário")
    void deveListarTransacoesPorUsuario() throws Exception {
        mockMvc.perform(get("/transacoes/usuario/" + usuario.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve listar transações por tipo")
    void deveListarTransacoesPorTipo() throws Exception {
        mockMvc.perform(get("/transacoes/usuario/" + usuario.getId() + "/tipo/INVESTIMENTO")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve calcular total de investimentos")
    void deveCalcularTotalDeInvestimentos() throws Exception {
        mockMvc.perform(get("/transacoes/usuario/" + usuario.getId() + "/estatisticas/investimentos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}

