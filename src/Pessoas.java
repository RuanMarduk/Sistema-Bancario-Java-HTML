import java.io.Serializable;

public abstract class Pessoas implements Serializable {
    private String CPF;
    private String Nome;
    private String Nascimento;
    private Endereço EN;
    private String EstadoCivil;
    private ContaBancaria Conta;

    public Pessoas(String CPF, String Nome, String Nascimento, Endereço Endere, String EstadoCivil, ContaBancaria Conta){
        if(ValidaCPF.validaCPF(CPF)) this.CPF=CPF; else throw new CPFInvalido("CPF Inválido!");
        this.Nome=Nome;
        this.Nascimento=Nascimento;
        this.EN=Endere;
        this.EstadoCivil=EstadoCivil;
        this.Conta = Conta;
    }

    public Pessoas(){}

    public String getCPF(){return CPF;}
    public void setCPF(String CPF){this.CPF = CPF;}

    public String getNome(){return Nome;}
    public void setNome(String Nome){this.Nome = Nome;}

    public ContaBancaria getConta() {return Conta;}

    public String getNascimento(){return Nascimento;}
    public void setNascimento(String Nascimento){this.Nascimento = Nascimento;}

    public int getNumConta(){ return Conta.getNumConta();}

    public String getSenha() { return Conta.getSenha();}

    public Endereço getEndereço(){return EN;}
    public void setEndereço(Endereço EN){this.EN = EN;}

    public String getEC(){return EstadoCivil;}
    public void setEC(String EC){this.EstadoCivil = EC;}
}
