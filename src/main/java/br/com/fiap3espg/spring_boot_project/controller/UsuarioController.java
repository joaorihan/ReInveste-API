package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.service.UsuarioService;
import br.com.fiap3espg.spring_boot_project.usuario.*;
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
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<DadosListagemUsuario> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        DadosListagemUsuario usuario = usuarioService.cadastrar(dados);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemUsuario>> listar(
            @PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        Page<DadosListagemUsuario> usuarios = usuarioService.listar(paginacao);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosListagemUsuario> buscarPorId(@PathVariable Long id) {
        DadosListagemUsuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping
    public ResponseEntity<DadosListagemUsuario> atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        DadosListagemUsuario usuario = usuarioService.atualizar(dados);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
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
