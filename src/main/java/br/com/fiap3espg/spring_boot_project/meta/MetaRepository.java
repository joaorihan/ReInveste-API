package br.com.fiap3espg.spring_boot_project.meta;

import br.com.fiap3espg.spring_boot_project.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    
    Page<Meta> findByUsuarioAndStatus(Usuario usuario, StatusMeta status, Pageable paginacao);
    
    @Query("SELECT m FROM Meta m WHERE m.usuario = :usuario AND m.status = 'ATIVA'")
    List<Meta> findMetasAtivasByUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT m FROM Meta m WHERE m.usuario = :usuario ORDER BY m.dataCriacao DESC")
    Page<Meta> findByUsuarioOrderByDataCriacaoDesc(@Param("usuario") Usuario usuario, Pageable paginacao);
    
    @Query("SELECT m FROM Meta m WHERE m.status = 'ATIVA' AND m.dataFim < :dataAtual")
    List<Meta> findMetasVencidas(@Param("dataAtual") LocalDate dataAtual);
    
    @Query("SELECT COUNT(m) FROM Meta m WHERE m.usuario = :usuario AND m.status = 'CONCLUIDA'")
    Long countMetasConcluidasByUsuario(@Param("usuario") Usuario usuario);
}
