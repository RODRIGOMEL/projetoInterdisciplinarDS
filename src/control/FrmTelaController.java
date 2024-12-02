package control;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import conexao.Conexao;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import view.Produto;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell; // Importação necessária para TableCell
import java.util.Optional;
import javafx.scene.control.Label;
import javafx.scene.control.Dialog; // Para criar o diálogo customizado
import javafx.scene.control.PasswordField; // Campo de senha
import javafx.scene.control.ButtonType; // Tipo de botão no diálogo
import javafx.scene.layout.VBox; // Layout para o conteúdo do diálogo
import javafx.scene.control.ButtonBar; // Para controlar o comportamento dos botões no diálogo
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button; 





//classe da tela de caixa
public class FrmTelaController {
    
    private Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage; // Armazena a referência do Stage
    }
    
    @FXML
    private Label idnomeatendente; //label do nome do atendente no canto superior esquerdo
    
    @FXML
    private Label idcodatendente; //label do nome do atendente no canto superior esquerdo
    
    @FXML
    private Label idsubtotal;  // Referência para a Label de subtotal
    
    @FXML
    private Label idtotal;
    
    @FXML
    private Label iddesconto;
    
    @FXML
    private Label idtotalrecebido;
    
    @FXML
    private Label idtroco;
    
    
    @FXML
    private Button idbtnaumentar;
    
    @FXML
    private Button idbtndiminuir;
    

    @FXML
    private TextField idtextocod; // TextField para o código do produto
    
    @FXML
    private TextField idAdicionarDesconto;
    
    
    @FXML
    private ComboBox<String> mtdPagamento;
    
    @FXML
    private TextField inserirTotalRecebido;
    
    
    @FXML
    private TableView<Produto> idTableView; // TableView para exibir os produtos
    
    @FXML
    private TableColumn<Produto, Integer> idColunaID; // Coluna para o ID
    @FXML
    private TableColumn<Produto, String> idColunaNome; // Coluna para o Nome
    @FXML
    private TableColumn<Produto, Integer> idColunaQtd; // Coluna para a Quantidade
    @FXML
    private TableColumn<Produto, Double> idColunaPreco; // Coluna para o Preço
    @FXML
    private TableColumn<Produto, Double> idColunaPrecoUnitario; // Coluna para o Preço unitario

    // Lista observável para manter os produtos adicionados
    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();
    

    
     public void exibirNomeAtendente(String nome) {
        idnomeatendente.setText(nome);
    }
     
     
    public void exibirIdAtendente(String id) {
        idcodatendente.setText(id);
    }
   
    
    //método construtor que inicializa os componentes da tableview da lista de compras
    @FXML
    public void initialize() {

        idColunaID.setCellValueFactory(new PropertyValueFactory<>("codBarra"));
        idColunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        idColunaQtd.setCellValueFactory(new PropertyValueFactory<>("qtd"));
        
        idColunaPrecoUnitario.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        idColunaPrecoUnitario.setCellFactory(coluna -> new TableCell<Produto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("R$ " + String.format("%.2f", item));
                }
            }
        });

        // Coluna de preço com formatação
        idColunaPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        idColunaPreco.setCellFactory(coluna -> new TableCell<Produto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("R$ " + String.format("%.2f", item));
                }
            }
        });
        

        // Define a lista inicial para a TableView
        idTableView.setItems(listaProdutos);
  
        idAdicionarDesconto.setOnAction(event -> aplicarCupom());
        
        
        // Define a opção padrão, caso não tenha sido configurada no SceneBuilder
        mtdPagamento.getItems().addAll("Dinheiro", "Cartão");
        mtdPagamento.getSelectionModel().select("Cartão");  // Garantir que "Cartão" seja selecionado inicialmente

        // Adiciona o ChangeListener no ComboBox para verificar quando o método de pagamento for alterado
        mtdPagamento.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Dinheiro".equals(newValue)) {
                inserirTotalRecebido.setDisable(false); // Habilita o campo se o pagamento for dinheiro
            } else {
                inserirTotalRecebido.setDisable(true);  // Desabilita o campo se for cartão
            }
        });

        // Chama a função para garantir que o TextField esteja desabilitado inicialmente
        if ("Cartão".equals(mtdPagamento.getValue())) {
            inserirTotalRecebido.setDisable(true);  // Inicialmente desabilitado se for cartão
        

            inserirTotalRecebido.textProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue.matches("\\d*(\\.\\d{0,2})?")) { // Permite apenas números e até duas casas decimais
            atualizarLabelTotalRecebido(newValue);
            atualizarTroco(); // Recalcula o troco sempre que o valor recebido muda
        } else {
            inserirTotalRecebido.setText(oldValue); // Reverte caso seja inválido
        }
    });
        }
    } 

    
    private void atualizarLabelTotalRecebido(String valor) {
        try {
            double totalRecebido = Double.parseDouble(valor);
            idtotalrecebido.setText(String.format("R$ %.2f", totalRecebido)); // Atualiza a label
        } catch (NumberFormatException e) {
            idtotalrecebido.setText("R$ 0,00"); // Valor padrão caso ocorra erro
        }
    }
    

    //método que adiciona o produto na lista de compras, utilizando o método 'buscarProdutoNoBanco' para puxar do banco e colocá-lo na lista
    public void buscarProduto() {
        String codigo = idtextocod.getText().trim(); // Remove espaços extras
        Produto produto = buscarProdutoNoBanco(codigo);

        if (produto != null) {
            // Verifica se o produto já foi adicionado à lista
            boolean jaAdicionado = listaProdutos.stream()
                    .anyMatch(p -> p.getCodBarra().equals(produto.getCodBarra()));

            if (!jaAdicionado) {
                listaProdutos.add(produto); // Adiciona à lista se não estiver
                idtextocod.clear();
                atualizarSubtotal(); // Atualiza subtotal após adicionar
                
            } else {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setHeaderText("Produto já adicionado");
                alerta.setContentText("O produto já está na lista.");
                alerta.showAndWait();
            }
        } else {
            exibirAlerta("Produto não encontrado", "Nenhum produto foi encontrado com o código: " + codigo);
        }
    }


    //método faz a busca do produto no banco de dados e seleciona as informações que vão aparecer na tela do caixa como id nome qtd e etc
    private Produto buscarProdutoNoBanco(String codigo) {
        Produto produto = null;
        String sql = "SELECT Cod_barra, Nome_Produto, Preco, Qtd_Estoque FROM produto WHERE Cod_barra = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_drogaria", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String codBarra = rs.getString("Cod_barra");
                String nomeProduto = rs.getString("Nome_Produto");
                double precoUnitario = rs.getDouble("Preco");
                int qtdEstoque = rs.getInt("Qtd_Estoque");

                // Inicializa o produto com quantidade 1
                produto = new Produto(codBarra, nomeProduto, 1, precoUnitario);
            }
        } catch (SQLException e) {
            exibirAlerta("Erro de Banco de Dados", "Ocorreu um erro ao acessar o banco de dados: " + e.getMessage());
        } catch (Exception e) {
            exibirAlerta("Erro", "Ocorreu um erro inesperado: "  + e.getMessage());
        }

        return produto;
    }
    
    
        private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        alert.initOwner(stage); // Usa a referência do Stage da tela de caixa
        alert.initModality(Modality.WINDOW_MODAL); // Torna o alerta modal em relação ao Stage

        alert.showAndWait();
    }

    
    //método pra sair do programa
    @FXML
    private void sairDoPrograma() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Saída");
        alerta.setHeaderText("Você deseja sair?");
        alerta.setContentText("Clique em OK para encerrar ou Cancelar para continuar.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            System.exit(0);
        }
    }
    
    
    //método pra aumentar a quantidade do produto na lista
    @FXML
    private void aumentarQuantidade() {
        Produto produtoSelecionado = idTableView.getSelectionModel().getSelectedItem();

        if (produtoSelecionado != null) {
            // Aumenta a quantidade
            int novaQuantidade = produtoSelecionado.getQtd() + 1;
            produtoSelecionado.setQtd(novaQuantidade); // Atualiza a quantidade

            idTableView.refresh(); // Atualiza a TableView para refletir as mudanças
            atualizarSubtotal();  // Atualiza subtotal após aumentar

        } else {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setHeaderText("Nenhum Produto Selecionado");
            alerta.setContentText("Por favor, selecione um produto para aumentar a quantidade.");
            alerta.showAndWait();
        }
    }



    //método de diminuir a quantidade do produto na lista
    @FXML
    private void diminuirQuantidade() {
    Produto produtoSelecionado = idTableView.getSelectionModel().getSelectedItem();
    
        if (produtoSelecionado != null) {
            int novaQuantidade = produtoSelecionado.getQtd() - 1;

            if (novaQuantidade > 0) {
                // Atualiza a quantidade e o preço proporcionalmente
                produtoSelecionado.setQtd(novaQuantidade);
                double precoUnitario = produtoSelecionado.getPrecoUnitario();
                produtoSelecionado.setPreco(precoUnitario * novaQuantidade);
                idTableView.refresh(); // Atualiza a TableView para refletir as mudanças
                atualizarSubtotal();  // Atualiza subtotal após diminuir

            } else {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setHeaderText("Quantidade Mínima");
                alerta.setContentText("A quantidade não pode ser menor que 1. Para remover o produto, utilize o botão de remover.");
                alerta.showAndWait();
            }

            // Atualiza a TableView para exibir os dados atualizados
            idTableView.refresh();
        } else {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setHeaderText("Nenhum Produto Selecionado");
            alerta.setContentText("Por favor, selecione um produto para diminuir a quantidade.");
            alerta.showAndWait();
        }
    }

    
    //método de remover produto da lista usando verificação de senha de cargo superior, utilizando o método 'verificarPermissao'
    @FXML
    private void removerProduto() {
        // Criação do Diálogo Personalizado
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Autenticação Requerida");
        dialog.setHeaderText("Insira o código de acesso para remover o produto");

        // Adiciona os botões OK e Cancelar
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Criação do PasswordField
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");

        // Layout do Diálogo
        VBox vbox = new VBox(passwordField);
        dialog.getDialogPane().setContent(vbox);

        // Desabilita o botão OK se o campo estiver vazio
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        // Define o resultado do diálogo
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        // Exibe o diálogo e processa a senha inserida
        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            String senha = resultado.get();
            if (verificarPermissao(senha)) {
                // Código para remover o produto da lista
                Produto produtoSelecionado = idTableView.getSelectionModel().getSelectedItem();
                if (produtoSelecionado != null) {
                    listaProdutos.remove(produtoSelecionado);
                    atualizarSubtotal();  // Atualiza subtotal após remover

                    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.setHeaderText("Produto removido");
                    alerta.setContentText("O produto foi removido com sucesso.");
                    alerta.showAndWait();
                    
                } else {
                    exibirAlerta("Erro", "Nenhum produto selecionado para remoção.");
                }
            } else {
                exibirAlerta("Permissão Negada", "Você não tem permissão para remover produtos.");
            }
        }
    }


    //verifica se a senha inserida é de cargo de gerente ou admin
    private boolean verificarPermissao(String senha) {
        String sql = "SELECT Cargo FROM tblusuario WHERE senha = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_drogaria", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String Cargo = rs.getString("Cargo");
                return Cargo.equals("admin") || Cargo.equals("gerente");
            }
        } catch (SQLException e) {
            exibirAlerta("Erro de Banco de Dados", "Ocorreu um erro ao acessar o banco de dados: " + e.getMessage());
        }
        return false; // Retorna falso se a senha não for encontrada ou se não for admin ou gerente
    }


    private double descontoTotal = 0; // Variável para armazenar o desconto acumulado
    

    @FXML
    private void aplicarCupom() {
        String codigoCupom = idAdicionarDesconto.getText().trim(); // Obtém o cupom do TextField
        double desconto = buscarDescontoDoBanco(codigoCupom); // Obtém o desconto do banco

        if (desconto > 0) {
            descontoTotal += desconto; // Acumula o desconto
            atualizarSubtotal(); // Atualiza subtotal e aplica o desconto total
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Cupom Aplicado");
            alerta.setHeaderText(null);
            alerta.setContentText("Desconto de " + desconto + "% aplicado");
            alerta.showAndWait();
        } else {
            exibirAlerta("Cupom inválido", "Cupom de desconto não encontrado ou inválido.");
        }
    }
    
    
    private double atualizarSubtotal() {
    double subtotal = 0.0;
    for (Produto produto : listaProdutos) {
        subtotal += produto.getPrecoUnitario() * produto.getQtd();
    }
    idsubtotal.setText(String.format("R$ %.2f", subtotal));
    atualizarTotal(subtotal); // Passa o subtotal atualizado para atualizar o total

    return subtotal; // Agora o método retorna o subtotal calculado

    }

    
    private void atualizarTotal(double subtotal) {
        double valorDesconto = subtotal * (descontoTotal / 100); // Calcula o valor do desconto acumulado
        iddesconto.setText(String.format("R$ %.2f", valorDesconto)); // Atualiza a label com o valor do desconto

        double totalComDesconto = subtotal * (1 - (descontoTotal / 100)); // Aplica o desconto total
        idtotal.setText(String.format("R$ %.2f", totalComDesconto)); // Atualiza a label total
    }

    
    // Método para buscar o desconto do banco
    private double buscarDescontoDoBanco(String codigoCupom) {
        double desconto = 0;
        String sql = "SELECT Desconto FROM cupons WHERE Codigo = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_drogaria", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoCupom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                desconto = rs.getDouble("Desconto"); // Obtém o valor do desconto do banco
            }
        } catch (SQLException e) {
            exibirAlerta("Erro de Banco de Dados", "Erro ao acessar o banco de dados: " + e.getMessage());
        }
        return desconto;
    }
    
    
    @FXML
    private void removerDesconto() {
        // Limpa o campo do cupom
        idAdicionarDesconto.clear();

        // Reseta o valor do desconto
        iddesconto.setText("R$ 0,00");

        // Atualiza a variável descontoTotal
        descontoTotal = 0;

        // Recalcula o total sem desconto
        double subtotal = Double.parseDouble(idsubtotal.getText().replace("R$", "").replace(",", ".").trim());
        idtotal.setText(String.format("R$ %.2f", subtotal)); // Atualiza o total para ser igual ao subtotal
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText("Desconto removido");
        alerta.showAndWait();
    }
    
    
    private double calcularDesconto() {
       double subtotal = atualizarSubtotal();
       return subtotal * (descontoTotal / 100);
    }  
    
    
    private double obterTotalRecebido() {
    // Aqui você deve obter o valor total recebido do cliente, 
    // por exemplo, de um campo de texto onde o usuário insere esse valor
    String valorRecebidoTexto = idtotalrecebido.getText().replace("R$", "").replace(",", ".").trim();
    return Double.parseDouble(valorRecebidoTexto);
    }
    
    
    private String obterMetodoPagamento() {
       return mtdPagamento.getValue(); 
    }
    
    @FXML
    private void MetodoPagamentoChange() {
        String metodo = mtdPagamento.getValue();
        if ("Cartão".equalsIgnoreCase(metodo)) {
            inserirTotalRecebido.clear(); // Limpa o campo de Total Recebido
            idtroco.setText("R$ 0,00");  // Reseta o Troco
            idtotalrecebido.setText("R$ 0,00"); //reseta o total recebido
        }
    }

    
    
    private void atualizarTroco() {
    // Obtém o total a ser pago da label de total
    double total = Double.parseDouble(idtotal.getText().replace("R$", "").replace(",", ".").trim());
    
    // Verifica se o total recebido é válido e converte para double
    double totalRecebido = 0.0;
    try {
        totalRecebido = Double.parseDouble(inserirTotalRecebido.getText().trim());
    } catch (NumberFormatException e) {
        totalRecebido = 0.0;
    }

    // Calcula o troco apenas se o total recebido for maior que o total e o método de pagamento for dinheiro
    double troco = (obterMetodoPagamento().equals("Dinheiro") && totalRecebido >= total) ? totalRecebido - total : 0.0;
    
    // Atualiza a label de troco
    idtroco.setText(String.format("R$ %.2f", troco));
    }
    
    
    @FXML
    private void finalizarCompra() {
        // Exemplo de valor recebido pelo cliente
        double subtotal = Double.parseDouble(idsubtotal.getText().replace("R$", "").replace(",", "."));
        double valorDesconto = subtotal * (descontoTotal / 100); // Pode ser zero caso o desconto tenha sido removido
        double total = subtotal - valorDesconto;

        // Verifica o método de pagamento para calcular o troco
        double totalRecebido = obterTotalRecebido(); // Método para obter o valor que o cliente pagou
        double troco = 0;
        String metodoPagamento = obterMetodoPagamento(); // Método que define o método de pagamento

        if ("dinheiro".equalsIgnoreCase(metodoPagamento)) {
            troco = totalRecebido - total;
        }

        // Monta a mensagem de resumo da compra
        String resumo = String.format(
            "Método de Pagamento: %s\n" +
            "Resumo da Compra:\n" +
            "Subtotal: R$ %.2f\n" +
            "Desconto: R$ %.2f\n" +
            "Total: R$ %.2f\n" +
            "Total Recebido: R$ %.2f\n" +
            "Troco: R$ %.2f",
            metodoPagamento, subtotal, valorDesconto, total, totalRecebido, troco
        );

        // Exibe o alerta com o resumo
        Alert alertaResumo = new Alert(Alert.AlertType.INFORMATION);
        alertaResumo.setTitle("Resumo da Compra");
        alertaResumo.setHeaderText("Detalhes da compra");
        alertaResumo.setContentText(resumo);
        alertaResumo.showAndWait();
    }
}