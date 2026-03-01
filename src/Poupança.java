import java.io.Serializable;

public class Poupança extends ContaBancaria implements Serializable {
    private double Rendimento;

    public Poupança(double Rendimento, int NumConta, double Saldo, String DataAbertura, String DataMovimentacao, String Senha, boolean Status){
        super(NumConta,Saldo,DataAbertura,DataMovimentacao,Senha,Status);
        this.Rendimento=Rendimento;
    }

    public double getRendimento(){return Rendimento;}
    public void SetRendimento(double Rendimento){this.Rendimento=Rendimento;}
}
