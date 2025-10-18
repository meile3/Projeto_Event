package model;

public class User {
    private static int COUNTER = 1;
    private int id;
    private String nome;
    private String email;
    private String telefone;
    private String cidade;

    public User(String nome, String email, String telefone, String cidade) {
        this.id = COUNTER++;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cidade = cidade;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCidade() { return cidade; }

    @Override
    public String toString() {
        return String.format("%s (email: %s, tel: %s, cidade: %s)", nome, email, telefone, cidade);
    }
}
