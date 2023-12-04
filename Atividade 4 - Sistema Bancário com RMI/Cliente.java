import java.rmi.Naming;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            BancoRemoto bancoRemoto = (BancoRemoto) Naming.lookup("//localhost/BancoRemoto");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                exibirMenu();

                int escolha = scanner.nextInt();
                scanner.nextLine(); 

                switch (escolha) {
                    case 1:
                        // Operação: Saldo
                        System.out.print("Número da conta: ");
                        int numeroContaSaldo = scanner.nextInt();
                        double saldo = bancoRemoto.obterSaldo(numeroContaSaldo);
                        System.out.println("Saldo da conta " + numeroContaSaldo + ": " + saldo);
                        break;

                    case 2:
                        // Operação: Saque
                        System.out.print("Número da conta: ");
                        int numeroContaSaque = scanner.nextInt();
                        System.out.print("Valor do saque: ");
                        double valorSaque = scanner.nextDouble();
                        boolean saqueSucesso = bancoRemoto.realizarSaque(numeroContaSaque, valorSaque);
                        if (saqueSucesso) {
                            System.out.println("Saque realizado com sucesso na conta " + numeroContaSaque);
                        } else {
                            System.out.println("Saldo insuficiente para saque na conta " + numeroContaSaque);
                        }
                        break;

                    case 3:
                        // Operação: Depósito
                        System.out.print("Número da conta: ");
                        int numeroContaDeposito = scanner.nextInt();
                        System.out.print("Valor do depósito: ");
                        double valorDeposito = scanner.nextDouble();
                        bancoRemoto.realizarDeposito(numeroContaDeposito, valorDeposito);
                        System.out.println("Depósito realizado com sucesso na conta " + numeroContaDeposito);
                        break;

                    case 4:
                        // Operação: Transferência
                        System.out.print("Conta de origem: ");
                        int contaOrigem = scanner.nextInt();
                        System.out.print("Conta de destino: ");
                        int contaDestino = scanner.nextInt();
                        System.out.print("Valor da transferência: ");
                        double valorTransferencia = scanner.nextDouble();
                        boolean transferenciaSucesso = bancoRemoto.realizarTransferencia(contaOrigem, contaDestino, valorTransferencia);
                        if (transferenciaSucesso) {
                            System.out.println("Transferência realizada com sucesso da conta " + contaOrigem + " para a conta " + contaDestino);
                        } else {
                            System.out.println("Saldo insuficiente para a transferência da conta " + contaOrigem + " para a conta " + contaDestino);
                        }
                        break;

                    case 5:
                        // Sair
                        System.out.println("Encerrando cliente...");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                
                }

                System.out.println("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    private static void exibirMenu() {
        System.out.println("+------------------------------------------------+");
        System.out.println("| Sistema Bancário iniciado com " + BancoRemotoImpl.obterQtdContas() + " contas (1-" + BancoRemotoImpl.obterQtdContas() + ") |");
        System.out.println("| Escolha uma operação:                          |");
        System.out.println("|                                                |");
        System.out.println("| 1. Saldo                                       |");
        System.out.println("| 2. Saque                                       |");
        System.out.println("| 3. Depósito                                    |");
        System.out.println("| 4. Transferência                               |");
        System.out.println("| 5. Sair                                        |");
        System.out.println("+------------------------------------------------+");
        System.out.print("\nOpção: ");
    }
}
