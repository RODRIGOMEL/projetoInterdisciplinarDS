package control;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import conexao.Conexao;
import java.sql.*;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import javafx.scene.control.ButtonType;


public class TelaCadastroController {

    @FXML
    private TextField IDfuncionario;
     
    @FXML
    private TextField nomeFunc;
    
    @FXML
    private TextField senhaFunc;
    
    @FXML
    private TextField CPFfunc;
    
    @FXML
    private TextField dataNascFunc;
    
    @FXML
    private TextField cargoFunc;
    
    
    
    public void cadastrar() {
    String nome = nomeFunc.getText();
    String senha = senhaFunc.getText();
    String cpf = CPFfunc.getText();
    String cargo = cargoFunc.getText();
    String dataNasc = dataNascFunc.getText();

    LocalDate localDate;
    try {
        localDate = LocalDate.parse(dataNasc); // Formato padrão: YYYY-MM-DD
    } catch (DateTimeParseException e) {
        exibirAlerta("Erro", "Data de nascimento inválida. Utilize o formato: YYYY-MM-DD.");
        return;
    }
    
        // Validações
        if (nome.isEmpty() || senha.isEmpty() || cpf.isEmpty() || cargo.isEmpty() || dataNasc.isEmpty()) {
            exibirAlerta("Erro", "Todos os campos devem ser preenchidos.");
            return; // Para a execução do método se algum campo estiver vazio
        }
        
        // Validação do CPF
        if (!validarCPF(cpf)) {
            exibirAlerta("Erro", "CPF inválido.");
            return;
        }



        String insert_sql = "INSERT INTO tblusuario (usuario, senha, CPF, Cargo, data_nasc) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/bd_drogaria", "root", "");
            PreparedStatement stmt = conn.prepareStatement(insert_sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, senha);
            stmt.setString(3, cpf);
            stmt.setString(4, cargo);
            stmt.setDate(5, Date.valueOf(localDate));

            stmt.executeUpdate();
             Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Sucesso");
            alerta.setHeaderText("Funcionário Cadastrado com Sucesso!");
            alerta.showAndWait();

        } catch (SQLException e) {
            exibirAlerta("Erro", "Erro no Cadastro de funcionário: " + e.getMessage());
        }
    }  
    
        private boolean validarCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false; // CPF deve ter 11 dígitos

        // Validação dos dígitos verificadores
        int soma = 0;
        int peso = 10;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
        }

        int primeiroDigito = (soma * 10) % 11;
        if (primeiroDigito == 10) primeiroDigito = 0;

        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) return false;

        soma = 0;
        peso = 11;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
        }

        int segundoDigito = (soma * 10) % 11;
        if (segundoDigito == 10) segundoDigito = 0;

        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }


    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    //método pra sair do programa
    @FXML
    private void fecharJanela() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Saída");
        alerta.setHeaderText("Você deseja sair?");
        alerta.setContentText("Clique em OK para encerrar ou Cancelar para continuar.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            System.exit(0);
        }
    }
    
}
