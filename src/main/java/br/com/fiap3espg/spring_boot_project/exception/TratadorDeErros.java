package br.com.fiap3espg.spring_boot_project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DadosErro> tratarErroRecursoNaoEncontrado(ResourceNotFoundException ex) {
        DadosErro erro = new DadosErro(
            "Recurso não encontrado",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<DadosErro> tratarErroRecursoDuplicado(DuplicateResourceException ex) {
        DadosErro erro = new DadosErro(
            "Recurso duplicado",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<DadosErro> tratarErroRegraDeNegocio(BusinessRuleException ex) {
        DadosErro erro = new DadosErro(
            "Erro de regra de negócio",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DadosErro> tratarErroCredenciaisInvalidas(BadCredentialsException ex) {
        DadosErro erro = new DadosErro(
            "Erro de autenticação",
            "Email ou senha inválidos",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DadosErro> tratarErroAutenticacao(AuthenticationException ex) {
        DadosErro erro = new DadosErro(
            "Erro de autenticação",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DadosErro> tratarErroValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            erros.put(fieldName, errorMessage);
        });

        DadosErro erro = new DadosErro(
            "Erro de validação",
            "Dados inválidos fornecidos na requisição",
            LocalDateTime.now(),
            erros
        );
        
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DadosErro> tratarErroGenerico(Exception ex) {
        ex.printStackTrace(); // Para debug em desenvolvimento
        DadosErro erro = new DadosErro(
            "Erro interno do servidor",
            "Ocorreu um erro inesperado. Tente novamente mais tarde.",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    public record DadosErro(
        String tipo,
        String mensagem,
        LocalDateTime timestamp,
        Map<String, String> detalhes
    ) {
        public DadosErro(String tipo, String mensagem, LocalDateTime timestamp) {
            this(tipo, mensagem, timestamp, new HashMap<>());
        }
    }
}
