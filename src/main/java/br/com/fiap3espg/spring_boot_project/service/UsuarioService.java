package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.usuario.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public DadosListagemUsuario cadastrar(DadosCadastroUsuario dados) {
        validarEmailUnico(dados.email());
        validarCpfUnico(dados.cpf());
        
        Usuario usuario = new Usuario(dados);
        usuarioRepository.save(usuario);
        
        return new DadosListagemUsuario(usuario);
    }

    public Page<DadosListagemUsuario> listar(Pageable paginacao) {
        return usuarioRepository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemUsuario::new);
    }

    public DadosListagemUsuario buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (!usuario.getAtivo()) {
            throw new RuntimeException("Usuário inativo");
        }
        
        return new DadosListagemUsuario(usuario);
    }

    public DadosListagemUsuario atualizar(DadosAtualizacaoUsuario dados) {
        Usuario usuario = usuarioRepository.findById(dados.id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (!usuario.getAtivo()) {
            throw new RuntimeException("Usuário inativo");
        }
        
        usuario.atualizarInformacoes(dados);
        
        return new DadosListagemUsuario(usuario);
    }

    public void excluir(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        usuario.excluir();
    }

    public void registrarAposta(Long usuarioId, BigDecimal valor) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        usuario.registrarAposta(valor);
    }

    public void atualizarDiasSemApostar(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        usuario.atualizarDiasSemApostar();
    }

    public Page<DadosListagemUsuario> buscarUsuariosComMetaAlcancada(Integer diasMinimos, Pageable paginacao) {
        return usuarioRepository.findUsuariosComMetaAlcancada(diasMinimos, paginacao)
                .map(DadosListagemUsuario::new);
    }

    public Page<DadosListagemUsuario> buscarPorNivelVicio(NivelVicio nivel, Pageable paginacao) {
        return usuarioRepository.findByNivelVicio(nivel, paginacao)
                .map(DadosListagemUsuario::new);
    }

    public Long contarUsuariosSemApostar(Integer dias) {
        return usuarioRepository.countUsuariosSemApostar(dias);
    }

    public BigDecimal calcularMediaGastoApostas() {
        Double media = usuarioRepository.calcularMediaGastoApostas();
        return media != null ? BigDecimal.valueOf(media) : BigDecimal.ZERO;
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já cadastrado");
        }
    }

    private void validarCpfUnico(String cpf) {
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new RuntimeException("CPF já cadastrado");
        }
    }
}
