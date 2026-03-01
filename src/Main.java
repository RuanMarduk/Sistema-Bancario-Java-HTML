import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Pessoas> pessoas = Persistencia.carregarDados("clientes.dat");
        if (pessoas == null) {
            pessoas = new ArrayList<>();
        }

        Endereço endPadrao = new Endereço("MG", "Uberlândia", "Tibery");
        Agencia agenciaPadrao = new Agencia(1001, "Tiradentes", endPadrao);

        int opcao;
        do {
            System.out.println("\n==== MENU BANCÁRIO ====");
            System.out.println("1. Cadastrar Cliente");
            System.out.println("2. Listar Clientes");
            System.out.println("3. Realizar Saque");
            System.out.println("4. Realizar Depósito");
            System.out.println("5. Consultar Saldo");
            System.out.println("6. Realizar Pagamento");
            System.out.println("7. Consultar Transações");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1: // Cadastrar cliente
                    System.out.println("=== Cadastro de Cliente ===");
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("CPF: ");
                    String cpf = sc.nextLine();
                    System.out.print("Escolaridade: ");
                    String esc = sc.nextLine();
                    System.out.print("Estado Civil: ");
                    String estadoC = sc.nextLine();
                    System.out.print("Data de Nascimento (dd/mm/aaaa): ");
                    String nascimento = sc.nextLine();
                    System.out.print("Número da conta: ");
                    int numConta = sc.nextInt();
                    System.out.print("Saldo inicial: ");
                    double saldo = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Senha: ");
                    String senha = sc.nextLine();

                    ContaBancaria novaConta = null;
                    System.out.println("Tipo de Conta:");
                    System.out.println("1. Corrente");
                    System.out.println("2. Salário");
                    System.out.println("3. Poupança");
                    System.out.print("Escolha o tipo de conta: ");
                    int tipoConta = sc.nextInt();

                    switch (tipoConta) {
                        case 1: // Corrente
                            System.out.print("Limite de Cheque Especial: ");
                            int limiteCheque = sc.nextInt();
                            novaConta = new Corrente(limiteCheque, 10, numConta, saldo, LocalDate.now().toString(), null, senha, true);
                            break;

                        case 2: // Salário
                            System.out.print("Limite de Saque: ");
                            double limiteSaque = sc.nextDouble();
                            System.out.print("Limite de Transferência: ");
                            double limiteTransf = sc.nextDouble();
                            novaConta = new Salario(limiteSaque, limiteTransf, numConta, saldo, LocalDate.now().toString(), null, senha, true);
                            break;

                        case 3: // Poupança
                            System.out.print("Taxa de Rendimento (% ao mês): ");
                            double taxa = sc.nextDouble();
                            novaConta = new Poupança(taxa, numConta, saldo, LocalDate.now().toString(), null, senha, true);
                            break;

                        default:
                            System.out.println("Tipo de conta inválido.");
                            break;
                    }

                    if (novaConta != null) {
                        Pessoas novoCliente = new Cliente(cpf, nome, nascimento, endPadrao, esc, agenciaPadrao, estadoC, novaConta);
                        pessoas.add(novoCliente);
                        System.out.println("Cliente cadastrado com sucesso!");
                    } else {
                        System.out.println("Falha no cadastro do cliente.");
                    }
                    break;

                case 2: // Listar clientes
                    System.out.println("\n=== Lista de Clientes ===");
                    for (Pessoas p : pessoas) {
                        System.out.println("Nome: " + p.getNome() + " | Conta: " + p.getNumConta() + " | CPF: " + p.getCPF());
                    }
                    break;

                case 3: // Saque
                    System.out.print("Número da conta: ");
                    int numContaS = sc.nextInt();
                    sc.nextLine();
                    Pessoas p1 = buscarCliente(pessoas, numContaS);

                    if(p1 != null) {
                        System.out.print("Senha: ");
                        String SenhaS = sc.nextLine();
                        if(p1.getSenha().equals(SenhaS)) {
                            System.out.print("Valor do Saque: ");
                            double valorSaque = sc.nextDouble();
                            if (valorSaque > 0) {
                                try {
                                    Transacao saque = new Saque(p1.getConta(), LocalDate.now(), valorSaque, "Agência");
                                    saque.executar();
                                } catch (SaldoInsuficienteException e) {
                                    System.out.println(e.getMessage());
                                }
                            } else {
                                System.out.println("Valor não Permitido.");
                            }
                        } else {
                            System.out.println("Senha Incorreta.");
                        }
                    } else {
                        System.out.println("Conta não Encontrada.");
                    }
                    break;

                case 4: // Depósito
                    System.out.print("Número da conta: ");
                    int numContaD = sc.nextInt();
                    sc.nextLine();
                    Pessoas p2 = buscarCliente(pessoas, numContaD);

                    if (p2 != null) {
                        System.out.print("Senha: ");
                        String SenhaD = sc.nextLine();

                        if (p2.getSenha().equals(SenhaD)) {
                            System.out.print("Valor do depósito: ");
                            double valorDeposito = sc.nextDouble();

                            if (valorDeposito > 0) {
                                try {
                                    Transacao deposito = new Deposito(p2.getConta(), LocalDate.now(), valorDeposito, "Agência");
                                    deposito.executar();
                                } catch (SaldoInsuficienteException e) {
                                    System.out.println(e.getMessage());
                                }
                            } else {
                                System.out.println("Valor não permitido.");
                            }
                        } else {
                            System.out.println("Senha Incorreta.");
                        }
                    } else {
                        System.out.println("Conta não encontrada.");
                    }
                    break;

                case 5: // Consulta de saldo
                    System.out.print("Número da conta: ");
                    int numContaC = sc.nextInt();
                    sc.nextLine();
                    Pessoas p3 = buscarCliente(pessoas, numContaC);

                    if (p3 != null) {
                        System.out.print("Senha: ");
                        String SenhaC = sc.nextLine();

                        if (p3.getSenha().equals(SenhaC)) {
                            try {
                                Transacao consulta = new ConsultaSaldo(p3.getConta(), LocalDate.now(), "Agência");
                                consulta.executar();
                            } catch (SaldoInsuficienteException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Senha Incorreta.");
                        }
                    } else {
                        System.out.println("Conta não encontrada.");
                    }
                    break;

                case 6: // Pagamento
                    System.out.print("Número da conta: ");
                    int numContaP = sc.nextInt();
                    sc.nextLine();
                    Pessoas p4 = buscarCliente(pessoas, numContaP);

                    if (p4 != null) {
                        System.out.print("Senha: ");
                        String SenhaP = sc.nextLine();

                        if (p4.getSenha().equals(SenhaP)) {
                            System.out.print("Valor do pagamento: ");
                            double valorPag = sc.nextDouble();

                            if (valorPag > 0) {
                                try {
                                    Transacao pag = new Pagamento(p4.getConta(), LocalDate.now(), valorPag, "Agência");
                                    pag.executar();
                                } catch (SaldoInsuficienteException e) {
                                    System.out.println(e.getMessage());
                                }
                            } else {
                                System.out.println("Valor não permitido.");
                            }
                        } else {
                            System.out.println("Senha Incorreta.");
                        }
                    } else {
                        System.out.println("Conta não encontrada.");
                    }
                    break;

                case 7:
                    System.out.println("\n=== Lista de Transações ===");
                    System.out.print("Número da conta (0 = todas): ");
                    int numContaFiltro = sc.nextInt();
                    sc.nextLine();

                    Pessoas cliente = buscarCliente(pessoas, numContaFiltro);
                    if (cliente == null && numContaFiltro != 0) {
                        System.out.println("Conta não encontrada.");
                        break;
                    }

                    if (numContaFiltro != 0) {
                        System.out.print("Senha: ");
                        String senhaInformada = sc.nextLine();
                        if (!cliente.getSenha().equals(senhaInformada)) {
                            System.out.println("Senha incorreta.");
                            break;
                        }
                    }
                    
                    List<Transacao> todas = Transacao.getHistoricoTransacoes();
                    List<Transacao> historicoFiltrado = todas.stream()
                            .filter(t -> numContaFiltro == 0 || t.getConta().getNumConta() == numContaFiltro)
                            .collect(Collectors.toList());

                    if (historicoFiltrado.isEmpty()) {
                        System.out.println("Nenhuma transação encontrada.");
                    } else {
                        historicoFiltrado.forEach(t -> System.out.printf(
                                "Data: %s | Conta: %d | Valor: R$ %.2f | Tipo: %s | Canal: %s%n",
                                t.getData(),
                                t.getConta().getNumConta(),
                                t.getValor(),
                                t.getClass().getSimpleName(),
                                t.getCanal()
                        ));
                    }
                    break;

                case 0:
                    Persistencia.salvarDados("clientes.dat", pessoas);
                    System.out.println("Dados salvos com sucesso.");
                    System.out.println("Encerrando o sistema...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
        sc.close();
    }

    public static Pessoas buscarCliente(ArrayList<Pessoas> pessoas, int numeroConta) {
        for (Pessoas p : pessoas) {
            if (p.getNumConta() == numeroConta) {
                return p;
            }
        }
        return null;
    }
}