import java.io.Serializable;

public class Corrente extends ContaBancaria implements Serializable {
    private double LimiteCheque;
    private double TaxaADM;

    public Corrente(double LimiteCheque, double TaxaADM, int NumConta, double Saldo, String DataAbertura, String DataMovimentacao, String Senha, boolean Status){
        super(NumConta,Saldo,DataAbertura,DataMovimentacao,Senha,Status);
        this.LimiteCheque=LimiteCheque;
        this.TaxaADM=TaxaADM;
    }

    public double getLimiteCheque(){return LimiteCheque;}
    public double getTaxaADM(){return TaxaADM;}

    public void setLimiteCheque(double LimiteCheque){this.LimiteCheque=LimiteCheque;}
    public void setTaxaADM(double TaxaADM){this.TaxaADM=TaxaADM;}
}
