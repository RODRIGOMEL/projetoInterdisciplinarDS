-- Criação do banco de dados
CREATE DATABASE bd_drogaria;
USE bd_drogaria;

-- Tabela Fornecedor
CREATE TABLE Fornecedor (
  ID_Fornecedor INT AUTO_INCREMENT PRIMARY KEY,
  Nome_Fornecedor VARCHAR(100) NOT NULL
);


-- Tabela CategoriaProduto
CREATE TABLE CategoriaProduto (
  ID_Categoria INT AUTO_INCREMENT PRIMARY KEY,
  Nome_Categoria VARCHAR(255) NOT NULL
);

-- Tabela Telefone
CREATE TABLE Telefone (
  ID_Telefone INT AUTO_INCREMENT PRIMARY KEY,
  Num_Telefone VARCHAR(15) NOT NULL  -- Telefone como VARCHAR para incluir formatos com DDD e possíveis caracteres especiais
);

-- Tabela Cliente
CREATE TABLE Cliente (
  ID_Cliente INT AUTO_INCREMENT PRIMARY KEY,
  ID_Telefone INT,
  Nome_Cliente VARCHAR(255) NOT NULL,
  Endereco VARCHAR(255) NOT NULL,
  CPF VARCHAR(11) NOT NULL,  -- CPF pode ser VARCHAR (11) para armazenar o número com zero à esquerda
  Data_nascimento DATE NOT NULL,
  Email VARCHAR(255) NOT NULL,
  Senha VARCHAR(255) NOT NULL,
  FOREIGN KEY (ID_Telefone) REFERENCES Telefone(ID_Telefone)

);

-- Tabela MetodoPgto
CREATE TABLE MetodoPgto (
  ID_metodoPgto INT AUTO_INCREMENT PRIMARY KEY,
  Descricao_pag VARCHAR(255) NOT NULL
);

-- Tabela Produto
CREATE TABLE Produto (
  ID_Produto INT AUTO_INCREMENT PRIMARY KEY,
  ID_Categoria INT,
  Cod_barra VARCHAR(255) NOT NULL,
  Nome_Produto VARCHAR(255) NOT NULL,
  Preco DECIMAL(10,2) NOT NULL,  -- Utilizando DECIMAL para armazenar valores monetários com precisão
  Qtd_Estoque INT NOT NULL,
  FOREIGN KEY (ID_Categoria) REFERENCES CategoriaProduto(ID_Categoria)
);
INSERT INTO Produto (ID_Produto, Nome_Produto, Preco, Qtd_Estoque, Cod_barra) VALUES 
(1, 'Pantoprazol 20mg', 12.50, 150, 7896523214351),
(2, 'Dipirona Monoidratada 1g', 21.05, 200, 7896714207551),
(3, 'Tandrilax Ache', 17.00, 100, 7896658004704),
(4, 'Dramin B6 25mg/ml', 22.45, 120, 7896094922075);

ALTER TABLE Produto CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE cupons (
  Codigo VARCHAR(50) PRIMARY KEY,    -- Código do cupom (ex: "DESCONTO10")
  Desconto DECIMAL(5,2) NOT NULL     -- Percentual de desconto (ex: 10.00 para 10%)
);
INSERT INTO cupons (Codigo, Desconto) VALUES
('DESCONTO10', 10.00),   -- 10% de desconto
('DESCONTO20', 20.00),   -- 20% de desconto
('VIP50', 50.00),        -- 50% de desconto
('PROMO5', 5.00);        -- 5% de desconto


-- Tabela Compra
CREATE TABLE Compra (
  ID_Compra INT AUTO_INCREMENT PRIMARY KEY,
  ID_Cliente INT,
  Data_compra DATE NOT NULL,
  FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente)
);

-- Tabela Fornecem (relacionamento entre Produto e Fornecedor)
CREATE TABLE Fornecem (
  ID_Produto INT,
  ID_Fornecedor INT,
  PRIMARY KEY (ID_Produto, ID_Fornecedor),
  FOREIGN KEY (ID_Produto) REFERENCES Produto(ID_Produto),
  FOREIGN KEY (ID_Fornecedor) REFERENCES Fornecedor(ID_Fornecedor)
);

-- Tabela Itens_Compra
CREATE TABLE Itens_Compra (
  ID_Produto INT,
  ID_Compra INT,
  Qtd_vendida INT NOT NULL,
  PRIMARY KEY (ID_Produto, ID_Compra),
  FOREIGN KEY (ID_Produto) REFERENCES Produto(ID_Produto),
  FOREIGN KEY (ID_Compra) REFERENCES Compra(ID_Compra)
);

-- Tabela FormaPgto
CREATE TABLE FormaPgto (
  ID_Compra INT,
  ID_metodoPgto INT,
  Valor DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (ID_Compra, ID_metodoPgto),
  FOREIGN KEY (ID_Compra) REFERENCES Compra(ID_Compra),
  FOREIGN KEY (ID_metodoPgto) REFERENCES MetodoPgto(ID_metodoPgto)
);


-- Tabela usuario
CREATE TABLE tblusuario (
  ID_usuario INT AUTO_INCREMENT PRIMARY KEY,
  usuario varchar(255) NOT NULL,
  senha varchar(255) NOT NULL,
  CPF VARCHAR(11) NOT NULL, 
  Cargo VARCHAR(255) NOT NULL,
  data_nasc DATE NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO tblusuario (ID_usuario, usuario, senha, Cargo) VALUES ('1', 'admin_user', '9982', 'admin');
INSERT INTO tblusuario (ID_usuario, usuario, senha, Cargo) VALUES ('2', 'Sergio', '7654', 'gerente');
INSERT INTO tblusuario (ID_usuario, usuario, senha, Cargo) VALUES ('3', 'Roberto', '4567', 'farmaceutico');
INSERT INTO tblusuario (ID_usuario, usuario, senha, Cargo) VALUES ('4', 'Jeferson', '6762', 'caixa');
