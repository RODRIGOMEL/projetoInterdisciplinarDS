package control;


import java.sql.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import conexao.Conexao;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField; // Campo de senha
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;


public class LoginController {
    
    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    private Conexao con_cliente;
    

    @FXML
    private void logar(ActionEvent event) {
    Node someNode = (Node) event.getSource();
    String usuario = txtUsuario.getText();
    String senha = txtSenha.getText();

    // Verifica se os campos estão preenchidos
    if (usuario.isEmpty() || senha.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Campo Vazio");
        alert.setHeaderText(null);
        alert.setContentText("Usuário e senha devem ser preenchidos!");
        alert.showAndWait();
        return;
    }

    // Verifica se o usuário e a senha estão corretos
    if (verificarUsuario(usuario, senha)) {
        String nomeAtendente = obterNomeAtendente(usuario); // Obtém o nome do atendente
        String idAtendente = obterIdAtendente(usuario, senha); // Obtém o ID do atendente
        
        
        if (idAtendente != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/frmTela.fxml"));
                Parent root = loader.load(); // Carrega o FXML
                
                FrmTelaController telaController = loader.getController(); // Referencia o controlador do caixa
                telaController.exibirNomeAtendente(nomeAtendente); // Atualiza o nome do atendente
                telaController.exibirIdAtendente(idAtendente); // Atualiza a label com o ID
                telaController.setStage((Stage) someNode.getScene().getWindow()); // Passa a referência do Stage

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Tela Principal");
                stage.initModality(Modality.APPLICATION_MODAL); // Isso pode ajudar também
                stage.show();

                // Fecha a janela atual
                ((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao carregar a tela principal: " + e.getMessage());
                alert.showAndWait();

            }
        } else {
            // Caso ID não seja encontrado, alerta de falha no login
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Falhou");
            alert.setHeaderText(null);
            alert.setContentText("Usuário ou senha inválidos!");
            alert.showAndWait();

        }
        } else {
            // Alerta de login inválido se a verificação falhar
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Falhou");
            alert.setHeaderText(null);
            alert.setContentText("Usuário ou senha inválidos!");
            alert.showAndWait();
        }
}


    private boolean verificarUsuario(String usuario, String senha) {
        String sql = "SELECT * FROM tblusuario WHERE usuario = ? AND senha = ?";
        
        try {
            // Cria uma nova conexão
            con_cliente = new Conexao();
            con_cliente.conecta();

            // Prepara a consulta com parâmetros
            PreparedStatement pst = con_cliente.getConnection().prepareStatement(sql);
            pst.setString(1, usuario);
            pst.setString(2, senha);

            // Executa a consulta
            ResultSet rs = pst.executeQuery();

            // Verifica se há um usuário correspondente
            return rs.next(); // Retorna true se encontrar um registro
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Banco de Dados");
            alert.setHeaderText(null);
            alert.setContentText("Ocorreu um erro ao acessar o banco de dados: " + e.getMessage());
            alert.showAndWait();
            return false; // Retorna false em caso de erro
        } 
    }
    
     private String obterNomeAtendente(String usuario) {
        String sql = "SELECT usuario FROM tblusuario WHERE usuario = ?";
        try {
            PreparedStatement pst = con_cliente.getConnection().prepareStatement(sql);
            pst.setString(1, usuario);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("usuario"); // Retorna o nome do atendente
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Banco de Dados");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao obter o nome do atendente" + e.getMessage());
            alert.showAndWait();
        }
        return "Desconhecido";
    }
     
    private String obterIdAtendente(String usuario, String senha) {
    String sql = "SELECT ID_usuario FROM tblusuario WHERE usuario = ? AND senha = ?";
    String idAtendente = null;

    try {

        // Prepara a consulta com parâmetros
        PreparedStatement pst = con_cliente.getConnection().prepareStatement(sql);
        pst.setString(1, usuario);
        pst.setString(2, senha);

        // Executa a consulta
        ResultSet rs = pst.executeQuery();

        // Verifica se há um usuário correspondente
        if (rs.next()) {
            idAtendente = rs.getString("ID_usuario"); // Pega o ID do atendente
        }
    } catch (SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Banco de Dados");
        alert.setHeaderText(null);
        alert.setContentText("Ocorreu um erro ao acessar o banco de dados: " + e.getMessage());
        alert.showAndWait();
    } 
    return idAtendente; // Retorna o ID do atendente ou null se não encontrar
}
         
    
    //método pra sair do programa
    @FXML
    private void fecharAba() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Saída");
        alerta.setHeaderText("Você deseja sair?");
        alerta.setContentText("Clique em OK para encerrar ou Cancelar para continuar.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            System.exit(0);
        }
    }
    
    
    @FXML
    private void esqueceuASenha() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Esqueceu a senha");
        alerta.setHeaderText("Entre em contato com os responsáveis do gerenciamento de sua unidade: drogaecia@gmail.com");
        alerta.showAndWait();
    }
    
    
    @FXML
    private void abrirTelaDoCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telaCadastro.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Cadastro de Funcionário");
            stage.show();
        } catch (IOException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Não foi possível abrir a tela de cadastro de funcionários");
            alerta.setHeaderText("Ocorreu um erro ao acessar a tela de cadastro " + e.getMessage());
            alerta.showAndWait();
        }
    }

}

