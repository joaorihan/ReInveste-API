package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.usuario.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface IUsuarioService {
    
    DadosListagemUsuario cadastrar(DadosCadastroUsuario dados);
    
    Page<DadosListagemUsuario> listar(Pageable paginacao);
    
    DadosListagemUsuario buscarPorId(Long id);
    
    DadosListagemUsuario atualizar(DadosAtualizacaoUsuario dados);
    
    void excluir(Long id);
    
    void registrarAposta(Long usuarioId, BigDecimal valor);
    
    void atualizarDiasSemApostar(Long usuarioId);
    
    Page<DadosListagemUsuario> buscarUsuariosComMetaAlcancada(Integer diasMinimos, Pageable paginacao);
    
    Page<DadosListagemUsuario> buscarPorNivelVicio(NivelVicio nivel, Pageable paginacao);
    
    Long contarUsuariosSemApostar(Integer dias);
    
    BigDecimal calcularMediaGastoApostas();
}

