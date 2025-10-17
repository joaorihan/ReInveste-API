package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.service.IUsuarioService;
import br.com.fiap3espg.spring_boot_project.usuario.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário (use /auth/register para criar conta)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário cadastrado com sucesso"),
        @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    @PostMapping
    public ResponseEntity<DadosListagemUsuario> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        DadosListagemUsuario usuario = usuarioService.cadastrar(dados);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Listar usuários", description = "Retorna lista paginada de usuários ativos")
    @GetMapping
    public ResponseEntity<Page<DadosListagemUsuario>> listar(
            @Parameter(description = "Parâmetros de paginação e ordenação")
            @PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        Page<DadosListagemUsuario> usuarios = usuarioService.listar(paginacao);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna detalhes de um usuário específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DadosListagemUsuario> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        DadosListagemUsuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza informações de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping
    public ResponseEntity<DadosListagemUsuario> atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        DadosListagemUsuario usuario = usuarioService.atualizar(dados);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Excluir usuário", description = "Realiza exclusão lógica de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@Parameter(description = "ID do usuário") @PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/aposta")
    public ResponseEntity<Void> registrarAposta(@PathVariable Long id, @RequestBody @Valid DadosAposta dados) {
        usuarioService.registrarAposta(id, dados.valor());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/dias-sem-apostar")
    public ResponseEntity<Void> atualizarDiasSemApostar(@PathVariable Long id) {
        usuarioService.atualizarDiasSemApostar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/meta-alcancada")
    public ResponseEntity<Page<DadosListagemUsuario>> buscarUsuariosComMetaAlcancada(
            @RequestParam Integer diasMinimos,
            @PageableDefault(size = 10, sort = {"diasSemApostar"}) Pageable paginacao) {
        Page<DadosListagemUsuario> usuarios = usuarioService.buscarUsuariosComMetaAlcancada(diasMinimos, paginacao);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/nivel-vicio/{nivel}")
    public ResponseEntity<Page<DadosListagemUsuario>> buscarPorNivelVicio(
            @PathVariable NivelVicio nivel,
            @PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        Page<DadosListagemUsuario> usuarios = usuarioService.buscarPorNivelVicio(nivel, paginacao);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/estatisticas/sem-apostar")
    public ResponseEntity<Long> contarUsuariosSemApostar(@RequestParam Integer dias) {
        Long count = usuarioService.contarUsuariosSemApostar(dias);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/estatisticas/media-gasto-apostas")
    public ResponseEntity<BigDecimal> calcularMediaGastoApostas() {
        BigDecimal media = usuarioService.calcularMediaGastoApostas();
        return ResponseEntity.ok(media);
    }

    public record DadosAposta(@Valid @jakarta.validation.constraints.DecimalMin("0.01") BigDecimal valor) {}
}
