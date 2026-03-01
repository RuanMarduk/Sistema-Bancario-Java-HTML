import java.time.LocalDate;

public class ConsultaSaldo extends Transacao {
    public ConsultaSaldo(ContaBancaria conta, LocalDate data, String canal) {
        super(conta, data, 0, canal);
    }

    @Override
    public void executar() {
        System.out.println("Saldo atual da conta " + conta.getNumConta() + ": R$ " + conta.getSaldo());
        registrarTransacao();}
}