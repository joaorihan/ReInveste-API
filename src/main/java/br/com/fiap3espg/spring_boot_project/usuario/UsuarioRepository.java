package br.com.fiap3espg.spring_boot_project.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Page<Usuario> findAllByAtivoTrue(Pageable paginacao);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND u.diasSemApostar >= :diasMinimos")
    Page<Usuario> findUsuariosComMetaAlcancada(Integer diasMinimos, Pageable paginacao);
    
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND u.nivelVicio = :nivel")
    Page<Usuario> findByNivelVicio(NivelVicio nivel, Pageable paginacao);
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.ativo = true AND u.diasSemApostar >= :dias")
    Long countUsuariosSemApostar(Integer dias);
    
    @Query("SELECT AVG(u.valorGastoApostas) FROM Usuario u WHERE u.ativo = true")
    Double calcularMediaGastoApostas();
}
