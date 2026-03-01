import java.io.Serializable;
import java.time.Year;

public class Funcionarios extends Pessoas implements CalculaSalarios, Serializable {
    private int NumCarteira;
    private char Sexo;
    private String Cargo;
    private double Salario;
    private int AnoIngresso;

    public Funcionarios(int NumCarteira, char Sexo, String Cargo, double Salario, int AnoIngresso, String CPF, String Nome, String Nascimento, Endereço EN, String EC, ContaBancaria Conta){
        super(CPF,Nome,Nascimento,EN,EC,Conta);
        this.NumCarteira=NumCarteira;
        this.Sexo=Sexo;
        this.Cargo=Cargo;
        this.Salario=Salario;
        this.AnoIngresso=AnoIngresso;
    }
    public double CalculaSalario(){
    int AnoAtual = Year.now().getValue();

        if((AnoAtual-AnoIngresso)>15){
            return Salario*1.1;}
            else return Salario;
    }

    public int getNumCarteira(){return NumCarteira;}
    public void setNumCarteira(int NumCarteira){this.NumCarteira = NumCarteira;}

    public char getSexo(){return Sexo;}
    public void setSexo(char Sexo){this.Sexo = Sexo;}

    public String getCargo(){return Cargo;}
    public void setCargo(String Cargo){this.Cargo = Cargo;}

    public double getSalario(){return Salario;}
    public void setSalario(double Salario){this.Salario = Salario;}

    public int getAnoIngresso(){return AnoIngresso;}
    public void setAnoIngresso(int AnoIngresso){this.AnoIngresso = AnoIngresso;}

}
