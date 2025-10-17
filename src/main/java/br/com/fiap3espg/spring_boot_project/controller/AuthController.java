package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.security.JwtUtil;
import br.com.fiap3espg.spring_boot_project.service.IUsuarioService;
import br.com.fiap3espg.spring_boot_project.usuario.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUsuarioService usuarioService;

    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                content = @Content(schema = @Schema(implementation = DadosTokenJWT.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<DadosTokenJWT> login(@RequestBody @Valid DadosLogin dados) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        Usuario usuario = (Usuario) authentication.getPrincipal();

        String token = jwtUtil.generateToken(usuario);

        DadosTokenJWT dadosToken = new DadosTokenJWT(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );

        return ResponseEntity.ok(dadosToken);
    }

    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso",
                content = @Content(schema = @Schema(implementation = DadosListagemUsuario.class))),
        @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<DadosListagemUsuario> register(@RequestBody @Valid DadosCadastroUsuario dados) {
        DadosListagemUsuario usuario = usuarioService.cadastrar(dados);
        return ResponseEntity.ok(usuario);
    }
}

