CREATE TABLE metas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo_meta VARCHAR(30) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor_alvo DECIMAL(10,2) NOT NULL,
    valor_atual DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_conclusao DATETIME,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
