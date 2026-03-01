import java.io.Serializable;

public class Endereço implements Serializable {
    private String Estado;
    private String Cidade;
    private String Bairro;

    Endereço(String Estado, String Cidade, String Bairro){
        this.Estado=Estado;
        this.Cidade=Cidade;
        this.Bairro=Bairro;
    }

    public String getEstado(){return Estado;}
    public void setEstado(String Estado){this.Estado = Estado;}

    public String getCidade(){return Cidade;}
    public void setCidade(String Cidade){this.Cidade = Cidade;}

    public String getBairro(){return Bairro;}
    public void setBairro(String Bairro){this.Bairro = Bairro;}

}
