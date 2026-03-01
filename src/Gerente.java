import java.io.Serializable;

public class Gerente extends Funcionarios implements CalculaSalarios, Serializable {
    private String DataIngressoCargo;
    private Agencia AG;
    private boolean Curso;
    private static double Comissão;

    public Gerente(int NumCarteira, char Sexo, String Cargo, double Salario, int AnoIngresso, String CPF, String Nome, String Nascimento, Endereço EN, String EC, String DataIngressoCargo, Agencia AG, boolean Curso, double Comissão,ContaBancaria Conta){
        super(NumCarteira,Sexo,Cargo,Salario,AnoIngresso,CPF,Nome,Nascimento,EN,EC, Conta);
        this.DataIngressoCargo=DataIngressoCargo;
        this.AG=AG;
        this.Curso=Curso;
        this.Comissão=Comissão;
    }

    public double CalculaSalario(){return (getSalario()+Comissão);}

    public static double getComissão(){return Comissão;}
    public static void setComissão(double Co){Comissão=Co;}
}
