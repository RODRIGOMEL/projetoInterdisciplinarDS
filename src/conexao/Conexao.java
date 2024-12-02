package conexao;
import javax.swing.JOptionPane;
import java.sql.*;


public class Conexao {
    final private String driver = "com.mysql.jdbc.Driver"; // definicao do driver
    final private String url = "jdbc:mysql://localhost:3306/bd_drogaria?useUnicode=true&characterEncoding=UTF-8";
    final private String usuario = "root"; // usuario do mysql
    final private String senha = ""; // senha do MySql
    private Connection conexao; // variavel q armazena a Conexao aberta
    public Statement statement; // variavel que executa os comandos sql dentro do java
    public ResultSet resultset; // variavel que armazena o resultado da execucao
    
    public boolean conecta() {
        boolean result = true;
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, usuario, senha);
        } catch (ClassNotFoundException Driver) {
            JOptionPane.showMessageDialog(null, "Driver não localizado "+Driver, "Mensagem do Programa", JOptionPane.INFORMATION_MESSAGE);
            result = false;
        } catch (SQLException Fonte) {
            JOptionPane.showMessageDialog(null, "Fonte de dados não localizada "+Fonte, "Mensagem do Programa", JOptionPane.INFORMATION_MESSAGE);
            result = false;
        }
        return result;
    }
    
    public void desconecta() {
        try {
            conexao.close();
            JOptionPane.showMessageDialog(null, "Conexão com o banco fechada", "Mensagem do Programa", JOptionPane.INFORMATION_MESSAGE);           
        } catch (SQLException fecha) {
            
        }
    }
    
    public void executaSQL(String sql) {
        try {
            statement = conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(sql);
        } catch (SQLException excecao) {
            JOptionPane.showMessageDialog(null, "Erro no comando SQL! \n Erro: "+excecao, "Mensagem do Programa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public Connection getConnection() {
        return conexao;
    }
    
  
}