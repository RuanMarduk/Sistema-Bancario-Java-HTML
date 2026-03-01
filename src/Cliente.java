import java.io.Serializable;

public class Cliente extends Pessoas implements Serializable {
    private String Escolaridade;
    private Agencia AG;

    public Cliente(String CPF, String Nome, String Nascimento, Endereço EN, String Escolaridade, Agencia AG, String EC, ContaBancaria Conta){
        super(CPF, Nome, Nascimento, EN, EC, Conta);
        this.Escolaridade=Escolaridade;
        this.AG=AG;
    }

    public Cliente(String CPF, String Nome){
        super(CPF, Nome, null, null, null, null);
        this.Escolaridade=null;
        this.AG=null;
    }

    public Cliente(){}

    public String getEscolaridade(){return Escolaridade;}
    public void setEscolaridade(String Escolaridade){this.Escolaridade = Escolaridade;}

    public Agencia getAgencia(){return AG;}
    public void setAgencia(Agencia AG){this.AG = AG;}

}
