package view;

public class Produto {
    private String codBarra;
    private String nome;
    private int qtd;
    private double preco; // Preço total
    private double precoUnitario; // Preço unitário

    // Construtor
    public Produto(String codBarra, String nome, int qtd, double precoUnitario) {
        this.codBarra = codBarra;
        this.nome = nome;
        this.qtd = qtd;
        this.precoUnitario = precoUnitario;
        this.preco = precoUnitario * qtd; // Inicializa o preço total
    }

    // Getters e Setters
    public String getCodBarra() {
        return codBarra;
    }

    public void setCodBarra(String codBarra) {
        this.codBarra = codBarra;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
        this.preco = this.precoUnitario * qtd; // Atualiza o preço total
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
        this.preco = precoUnitario * this.qtd; // Atualiza o preço total
    }

    // Sobrescrevendo equals para comparação de códigos de barras
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Produto produto = (Produto) obj;
        return codBarra.equals(produto.codBarra); // Compara o código de barras
    }

    // Sobrescrevendo hashCode
    @Override
    public int hashCode() {
        return codBarra.hashCode(); // Gera o hash com base no código de barras
    }
}
