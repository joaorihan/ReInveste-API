package br.com.fiap3espg.spring_boot_project.controller;

import br.com.fiap3espg.spring_boot_project.service.TransacaoService;
import br.com.fiap3espg.spring_boot_project.transacao.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<DadosListagemTransacao> cadastrar(
            @PathVariable Long usuarioId,
            @RequestBody @Valid DadosCadastroTransacao dados) {
        DadosListagemTransacao transacao = transacaoService.cadastrar(dados, usuarioId);
        return ResponseEntity.ok(transacao);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<DadosListagemTransacao>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10, sort = {"dataTransacao"}) Pageable paginacao) {
        Page<DadosListagemTransacao> transacoes = transacaoService.listarPorUsuario(usuarioId, paginacao);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<Page<DadosListagemTransacao>> listarPorTipo(
            @PathVariable Long usuarioId,
            @PathVariable TipoTransacao tipo,
            @PageableDefault(size = 10, sort = {"dataTransacao"}) Pageable paginacao) {
        Page<DadosListagemTransacao> transacoes = transacaoService.listarPorTipo(usuarioId, tipo, paginacao);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<DadosListagemTransacao>> listarPorPeriodo(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        List<DadosListagemTransacao> transacoes = transacaoService.listarPorPeriodo(usuarioId, dataInicio, dataFim);
        return ResponseEntity.ok(transacoes);
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmarTransacao(@PathVariable Long id) {
        transacaoService.confirmarTransacao(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarTransacao(@PathVariable Long id) {
        transacaoService.cancelarTransacao(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuario/{usuarioId}/estatisticas/investimentos")
    public ResponseEntity<BigDecimal> calcularTotalInvestimentos(@PathVariable Long usuarioId) {
        BigDecimal total = transacaoService.calcularTotalInvestimentos(usuarioId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{usuarioId}/estatisticas/economias")
    public ResponseEntity<BigDecimal> calcularTotalEconomias(@PathVariable Long usuarioId) {
        BigDecimal total = transacaoService.calcularTotalEconomias(usuarioId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{usuarioId}/estatisticas/apostas")
    public ResponseEntity<BigDecimal> calcularTotalApostas(@PathVariable Long usuarioId) {
        BigDecimal total = transacaoService.calcularTotalApostas(usuarioId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{usuarioId}/estatisticas/apostas-recentes")
    public ResponseEntity<Long> contarApostasRecentes(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "30") Integer dias) {
        Long count = transacaoService.contarApostasRecentes(usuarioId, dias);
        return ResponseEntity.ok(count);
    }
}
