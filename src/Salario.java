import java.io.Serializable;

public class Salario extends ContaBancaria implements Serializable {
    private double LimiteSaque;
    private double LimiteTransf;

    public Salario(double LSaque, double LTransação, int NumConta, double Saldo, String DataAbertura, String DataMovimentacao, String Senha, boolean Status){
        super(NumConta,Saldo,DataAbertura,DataMovimentacao,Senha,Status);
        LimiteSaque=LSaque;
        LimiteTransf=LTransação;
    }

    public double getLimiteSaque(){return LimiteSaque;}
    public double getLimiteTransf(){return LimiteTransf;}

    public void setLimiteSaque(double LimiteSaque){this.LimiteSaque=LimiteSaque;}
    public void setTaxaADM(double LimiteTransf){this.LimiteTransf=LimiteTransf;}

}
