import javafx.beans.property.SimpleStringProperty;

public class User {
    private final int id;
    private final SimpleStringProperty nome;
    private final SimpleStringProperty usuario;
    private final SimpleStringProperty senha;

    public User(int id, String nome, String usuario, String senha) {
        this.id = id;
        this.nome = new SimpleStringProperty(nome);
        this.usuario = new SimpleStringProperty(usuario);
        this.senha = new SimpleStringProperty(senha);
    }

    public String getNome() {
        return nome.get();
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public String getUsuario() {
        return usuario.get();
    }

    public void setUsuario(String usuario) {
        this.usuario.set(usuario);
    }

    public String getSenha() {
        return senha.get();
    }

    public void setSenha(String senha) {
        this.senha.set(senha);
    }

    public int getId() {
        return 0;
    }
}
