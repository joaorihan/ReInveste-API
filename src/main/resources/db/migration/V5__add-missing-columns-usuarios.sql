-- Adicionar colunas que estão faltando na tabela usuarios
ALTER TABLE usuarios ADD COLUMN numero VARCHAR(10);
ALTER TABLE usuarios ADD COLUMN complemento VARCHAR(100);

