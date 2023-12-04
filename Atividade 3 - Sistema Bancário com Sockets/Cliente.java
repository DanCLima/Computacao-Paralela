import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private static final String SERVIDOR_IP = "localhost";
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVIDOR_IP, PORTA);
             ObjectOutputStream saidaObjeto = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entradaObjeto = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                exibirMenu();
                int escolha = scanner.nextInt();
                scanner.nextLine(); 

                switch (escolha) {
                    case 1:
                        realizarOperacao("saldo", saidaObjeto, entradaObjeto, scanner);
                        break;
                    case 2:
                        realizarOperacao("saque", saidaObjeto, entradaObjeto, scanner);
                        break;
                    case 3:
                        realizarOperacao("deposito", saidaObjeto, entradaObjeto, scanner);
                        break;
                    case 4:
                        realizarOperacao("transferencia", saidaObjeto, entradaObjeto, scanner);
                        break;
                    case 5:
                        System.out.println("Encerrando cliente...");
                        saidaObjeto.writeObject("encerrar");
                        return;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void exibirMenu() {
        System.out.println("Escolha uma operação:");
        System.out.println("1. Saldo");
        System.out.println("2. Saque");
        System.out.println("3. Depósito");
        System.out.println("4. Transferência");
        System.out.println("5. Sair");
        System.out.print("Opção: ");
    }

    private static void realizarOperacao(String operacao, ObjectOutputStream saidaObjeto, ObjectInputStream entradaObjeto, Scanner scanner) throws IOException {
        saidaObjeto.writeObject(operacao);

        switch (operacao) {
            case "saldo":
            case "saque":
            case "deposito":
                System.out.print("Número da conta: ");
                int numeroConta = scanner.nextInt();
                saidaObjeto.writeInt(numeroConta);
                saidaObjeto.flush();

                if (operacao.equals("saque") || operacao.equals("deposito")) {
                    System.out.print("Valor: ");
                    double valor = scanner.nextDouble();
                    saidaObjeto.writeDouble(valor);
                    saidaObjeto.flush();
                }

                if (operacao.equals("saldo")) {
                    double saldo = entradaObjeto.readDouble();
                    System.out.println("Saldo da conta: " + saldo);
                } else if (operacao.equals("saque")) {
                    boolean saqueSucesso = entradaObjeto.readBoolean();
                    if (saqueSucesso) {
                        System.out.println("Saque realizado com sucesso.");
                    } else {
                        System.out.println("Saldo insuficiente para saque.");
                    }
                }
                break;

            case "transferencia":
                System.out.print("Conta de origem: ");
                int contaOrigem = scanner.nextInt();
                saidaObjeto.writeInt(contaOrigem);
                saidaObjeto.flush();

                System.out.print("Conta de destino: ");
                int contaDestino = scanner.nextInt();
                saidaObjeto.writeInt(contaDestino);
                saidaObjeto.flush();

                System.out.print("Valor: ");
                double valorTransferencia = scanner.nextDouble();
                saidaObjeto.writeDouble(valorTransferencia);
                saidaObjeto.flush();

                boolean transferenciaSucesso = entradaObjeto.readBoolean();
                if (transferenciaSucesso) {
                    System.out.println("Transferência realizada com sucesso.");
                } else {
                    System.out.println("Saldo insuficiente para a transferência.");
                }
                break;

            default:
                System.out.println("Operação não suportada.");
        }
    }
}
