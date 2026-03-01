import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Transacao {
    protected ContaBancaria conta;
    protected LocalDate data;
    protected double valor;
    protected String canal;

    private static ArrayList<Transacao> historico = new ArrayList<>();

    public Transacao(ContaBancaria conta, LocalDate data, double valor, String canal) {
        this.conta = conta;
        this.data = data;
        this.valor = valor;
        this.canal = canal;
    }

    public ContaBancaria getConta() {
        return conta;
    }
    public LocalDate getData() {
        return data;
    }
    public double getValor() {
        return valor;
    }
    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public abstract void executar() throws SaldoInsuficienteException;

    public void registrarTransacao() {
        historico.add(this);
    }

    public static ArrayList<Transacao> getHistoricoTransacoes() {
        return historico;
    }
}