package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.meta.*;
import br.com.fiap3espg.spring_boot_project.service.IMetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.List;

@RestController
@RequestMapping("/metas")
@Tag(name = "Metas", description = "Gerenciamento de metas financeiras dos usuários")
@SecurityRequirement(name = "bearerAuth")
public class MetaController {

    @Autowired
    private IMetaService metaService;

    @Operation(summary = "Cadastrar meta", description = "Cria uma nova meta financeira para um usuário")
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<DadosListagemMeta> cadastrar(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @RequestBody @Valid DadosCadastroMeta dados) {
        DadosListagemMeta meta = metaService.cadastrar(dados, usuarioId);
        return ResponseEntity.ok(meta);
    }

    @Operation(summary = "Listar metas por usuário", description = "Retorna todas as metas de um usuário")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<DadosListagemMeta>> listarPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        Page<DadosListagemMeta> metas = metaService.listarPorUsuario(usuarioId, paginacao);
        return ResponseEntity.ok(metas);
    }

    @Operation(summary = "Listar metas ativas", description = "Retorna apenas metas ativas de um usuário")
    @GetMapping("/usuario/{usuarioId}/ativas")
    public ResponseEntity<Page<DadosListagemMeta>> listarMetasAtivas(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        Page<DadosListagemMeta> metas = metaService.listarMetasAtivas(usuarioId, paginacao);
        return ResponseEntity.ok(metas);
    }

    @Operation(summary = "Buscar meta por ID", description = "Retorna detalhes de uma meta específica")
    @GetMapping("/{id}")
    public ResponseEntity<DadosListagemMeta> buscarPorId(
            @Parameter(description = "ID da meta") @PathVariable Long id) {
        DadosListagemMeta meta = metaService.buscarPorId(id);
        return ResponseEntity.ok(meta);
    }

    @Operation(summary = "Atualizar progresso da meta", description = "Adiciona valor ao progresso de uma meta")
    @PutMapping("/{id}/progresso")
    public ResponseEntity<Void> atualizarProgresso(
            @Parameter(description = "ID da meta") @PathVariable Long id,
            @RequestBody @Valid DadosProgressoMeta dados) {
        metaService.atualizarProgresso(id, dados.valorAdicional());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancelar meta", description = "Cancela uma meta ativa")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarMeta(
            @Parameter(description = "ID da meta") @PathVariable Long id) {
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
