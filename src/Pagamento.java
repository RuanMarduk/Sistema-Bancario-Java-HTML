import java.time.LocalDate;

public class Pagamento extends Transacao {
    public Pagamento(ContaBancaria conta, LocalDate data, double valor, String canal) {
        super(conta, data, valor, canal);
    }

    @Override
    public void executar() throws SaldoInsuficienteException {
        if (conta.getSaldo() >= valor) {
            conta.setSaldo(conta.getSaldo() - valor);
            registrarTransacao();
        } else {
            throw new SaldoInsuficienteException("Saldo insuficiente para pagamento!");}}}