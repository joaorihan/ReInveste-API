package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.meta.*;
import br.com.fiap3espg.spring_boot_project.service.MetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/metas")
public class MetaController {

    @Autowired
    private MetaService metaService;

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<DadosListagemMeta> cadastrar(
            @PathVariable Long usuarioId,
            @RequestBody @Valid DadosCadastroMeta dados) {
        DadosListagemMeta meta = metaService.cadastrar(dados, usuarioId);
        return ResponseEntity.ok(meta);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<DadosListagemMeta>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        Page<DadosListagemMeta> metas = metaService.listarPorUsuario(usuarioId, paginacao);
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/usuario/{usuarioId}/ativas")
    public ResponseEntity<Page<DadosListagemMeta>> listarMetasAtivas(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        Page<DadosListagemMeta> metas = metaService.listarMetasAtivas(usuarioId, paginacao);
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosListagemMeta> buscarPorId(@PathVariable Long id) {
        DadosListagemMeta meta = metaService.buscarPorId(id);
        return ResponseEntity.ok(meta);
    }

    @PutMapping("/{id}/progresso")
    public ResponseEntity<Void> atualizarProgresso(
            @PathVariable Long id,
            @RequestBody @Valid DadosProgressoMeta dados) {
        metaService.atualizarProgresso(id, dados.valorAdicional());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarMeta(@PathVariable Long id) {
        metaService.cancelarMeta(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vencidas")
    public ResponseEntity<List<DadosListagemMeta>> buscarMetasVencidas() {
        List<DadosListagemMeta> metas = metaService.buscarMetasVencidas();
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/usuario/{usuarioId}/concluidas/count")
    public ResponseEntity<Long> contarMetasConcluidas(@PathVariable Long usuarioId) {
        Long count = metaService.contarMetasConcluidas(usuarioId);
        return ResponseEntity.ok(count);
    }

    public record DadosProgressoMeta(@Valid @jakarta.validation.constraints.DecimalMin("0.01") BigDecimal valorAdicional) {}
}
