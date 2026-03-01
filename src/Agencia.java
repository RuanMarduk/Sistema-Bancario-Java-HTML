import java.io.Serializable;

public class Agencia implements Serializable {
    private int NumAgencia;
    private String NomeAgencia;
    private Endereço EndereçoAgencia;

    public Agencia(int NumAgencia, String NomeAgencia, Endereço EndereçoAgencia){
        this.NumAgencia=NumAgencia;
        this.NomeAgencia=NomeAgencia;
        this.EndereçoAgencia=EndereçoAgencia;
    }

    public int getNumAgencia(){return NumAgencia;}
    public void setNumAgencia(int NumAgencia){this.NumAgencia = NumAgencia;}

    public String getNomeAgencia(){return NomeAgencia;}
    public void setNomeAgencia(String NomeAgencia){this.NomeAgencia = NomeAgencia;}

    public Endereço getEndereçoAgencia(){return EndereçoAgencia;}
    public void setEndereçoAgencia(Endereço EndereçoAgencia){this.EndereçoAgencia = EndereçoAgencia;}

}
