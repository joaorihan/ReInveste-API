package br.com.fiap3espg.spring_boot_project.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String resourceName, String field, String value) {
        super(String.format("%s já existe com %s: %s", resourceName, field, value));
    }
}

