import java.io.Serializable;

public abstract class ContaBancaria implements Serializable {
    protected int NumConta;
    protected double Saldo;
    protected String DataAbertura;
    protected String DataMovimento;
    protected String Senha;
    protected boolean Status;

    public ContaBancaria(int NumConta, double Saldo, String DataAbertura, String DataMovimentacao, String Senha, boolean Status){
        this.NumConta=NumConta;
        this.Saldo=Saldo;
        this.DataAbertura=DataAbertura;
        DataMovimento=DataMovimentacao;
        this.Senha=Senha;
        this.Status=Status;
    }

    public String getSenha(){return Senha;}

    public int getNumConta(){return NumConta;}

    public double getSaldo() {return Saldo;}

    public void setNumConta(int NumConta){this.NumConta = NumConta;}

    public void setSaldo(double Saldo){this.Saldo = Saldo;}

    public String getDataAbertura(){return DataAbertura;}
    public void setDataAbertura(String DataAbertura){this.DataAbertura = DataAbertura;}

    public String getDataMovimento(){return DataMovimento;}
    public void setDataMovimento(String DataMovimento){this.DataMovimento = DataMovimento;}

    public void setSenha(String senha){this.Senha = Senha;}

    public boolean getStatus(){return Status;}
    public void setStatus(boolean Status){this.Status = Status;}
}
