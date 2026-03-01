import java.time.LocalDate;

public class Deposito extends Transacao {
    public Deposito(ContaBancaria conta, LocalDate data, double valor, String canal) {
        super(conta, data, valor, canal);
    }

    @Override
    public void executar() {
        if (valor > 0) {
            conta.setSaldo(conta.getSaldo() + valor);
            registrarTransacao();
        } else {
            System.out.println("Valor de depósito deve ser maior que zero!");}}}