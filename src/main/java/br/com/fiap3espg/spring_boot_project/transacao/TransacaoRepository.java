package br.com.fiap3espg.spring_boot_project.transacao;

import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
    Page<Transacao> findByUsuarioOrderByDataTransacaoDesc(Usuario usuario, Pageable paginacao);
    
    @Query("SELECT t FROM Transacao t WHERE t.usuario = :usuario AND t.tipoTransacao = :tipo ORDER BY t.dataTransacao DESC")
    Page<Transacao> findByUsuarioAndTipoTransacao(@Param("usuario") Usuario usuario, 
                                                  @Param("tipo") TipoTransacao tipo, 
                                                  Pageable paginacao);
    
    @Query("SELECT t FROM Transacao t WHERE t.usuario = :usuario AND t.dataTransacao BETWEEN :dataInicio AND :dataFim ORDER BY t.dataTransacao DESC")
    List<Transacao> findByUsuarioAndDataTransacaoBetween(@Param("usuario") Usuario usuario,
                                                        @Param("dataInicio") LocalDateTime dataInicio,
                                                        @Param("dataFim") LocalDateTime dataFim);
    
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.usuario = :usuario AND t.tipoTransacao = :tipo AND t.status = 'CONFIRMADA'")
    Double calcularTotalPorTipo(@Param("usuario") Usuario usuario, @Param("tipo") TipoTransacao tipo);
    
    @Query("SELECT COUNT(t) FROM Transacao t WHERE t.usuario = :usuario AND t.tipoTransacao = 'APOSTA' AND t.dataTransacao >= :dataInicio")
    Long countApostasRecentes(@Param("usuario") Usuario usuario, @Param("dataInicio") LocalDateTime dataInicio);
}
