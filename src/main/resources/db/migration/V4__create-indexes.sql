-- Índices para melhorar performance das consultas

-- Índices para tabela usuarios
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_cpf ON usuarios(cpf);
CREATE INDEX idx_usuarios_nivel_vicio ON usuarios(nivel_vicio);
CREATE INDEX idx_usuarios_dias_sem_apostar ON usuarios(dias_sem_apostar);

-- Índices para tabela metas
CREATE INDEX idx_metas_usuario_id ON metas(usuario_id);
CREATE INDEX idx_metas_status ON metas(status);
CREATE INDEX idx_metas_tipo_meta ON metas(tipo_meta);
CREATE INDEX idx_metas_data_fim ON metas(data_fim);

-- Índices para tabela transacoes
CREATE INDEX idx_transacoes_usuario_id ON transacoes(usuario_id);
CREATE INDEX idx_transacoes_meta_id ON transacoes(meta_id);
CREATE INDEX idx_transacoes_tipo ON transacoes(tipo_transacao);
CREATE INDEX idx_transacoes_status ON transacoes(status);
CREATE INDEX idx_transacoes_data_transacao ON transacoes(data_transacao);
