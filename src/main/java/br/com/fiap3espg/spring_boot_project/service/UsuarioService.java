package br.com.fiap3espg.spring_boot_project.service;

import br.com.fiap3espg.spring_boot_project.exception.BusinessRuleException;
import br.com.fiap3espg.spring_boot_project.exception.ResourceNotFoundException;
import br.com.fiap3espg.spring_boot_project.service.validation.IValidationService;
import br.com.fiap3espg.spring_boot_project.usuario.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private IValidationService validationService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public DadosListagemUsuario cadastrar(DadosCadastroUsuario dados) {
        validationService.validarEmailUnico(dados.email());
        validationService.validarCpfUnico(dados.cpf());
        
        Usuario usuario = new Usuario(dados);
        // Criptografar senha com BCrypt
        String senhaCriptografada = passwordEncoder.encode(dados.senha());
        usuario.setSenha(senhaCriptografada);
        
        usuarioRepository.save(usuario);
        
        return new DadosListagemUsuario(usuario);
    }

    @Override
    public Page<DadosListagemUsuario> listar(Pageable paginacao) {
        return usuarioRepository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemUsuario::new);
    }

    @Override
    public DadosListagemUsuario buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        
        if (!usuario.getAtivo()) {
            throw new BusinessRuleException("Usuário está inativo");
        }
        
        return new DadosListagemUsuario(usuario);
    }

    @Override
    public DadosListagemUsuario atualizar(DadosAtualizacaoUsuario dados) {
        Usuario usuario = usuarioRepository.findById(dados.id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", dados.id()));
        
        if (!usuario.getAtivo()) {
            throw new BusinessRuleException("Usuário está inativo");
        }
        
        usuario.atualizarInformacoes(dados);
        
        return new DadosListagemUsuario(usuario);
    }

    @Override
    public void excluir(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        
        usuario.excluir();
    }

    @Override
    public void registrarAposta(Long usuarioId, BigDecimal valor) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        usuario.registrarAposta(valor);
    }

    @Override
    public void atualizarDiasSemApostar(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", usuarioId));
        
        usuario.atualizarDiasSemApostar();
    }

    @Override
    public Page<DadosListagemUsuario> buscarUsuariosComMetaAlcancada(Integer diasMinimos, Pageable paginacao) {
        return usuarioRepository.findUsuariosComMetaAlcancada(diasMinimos, paginacao)
                .map(DadosListagemUsuario::new);
    }

    @Override
    public Page<DadosListagemUsuario> buscarPorNivelVicio(NivelVicio nivel, Pageable paginacao) {
        return usuarioRepository.findByNivelVicio(nivel, paginacao)
                .map(DadosListagemUsuario::new);
    }

    @Override
    public Long contarUsuariosSemApostar(Integer dias) {
        return usuarioRepository.countUsuariosSemApostar(dias);
    }

    @Override
    public BigDecimal calcularMediaGastoApostas() {
        Double media = usuarioRepository.calcularMediaGastoApostas();
        return media != null ? BigDecimal.valueOf(media) : BigDecimal.ZERO;
    }
}
