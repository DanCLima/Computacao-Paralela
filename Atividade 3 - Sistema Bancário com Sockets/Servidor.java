import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {

    private static final int PORTA = 12345;
    private static final Map<Integer, ContaCorrente> contas = new HashMap<>();

    public static void main(String[] args) {
        inicializarContas();

        try (ServerSocket servidorSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor aguardando conexões na porta " + PORTA);

            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress().getHostAddress());

                Thread threadCliente = new Thread(() -> {
                    try (
                        ObjectOutputStream saidaObjeto = new ObjectOutputStream(clienteSocket.getOutputStream());
                        ObjectInputStream entradaObjeto = new ObjectInputStream(clienteSocket.getInputStream())
                    ) {
                        while (true) {
                            // Recebe a operação desejada do cliente
                            String operacao = (String) entradaObjeto.readObject();

                            // Executa a operação correspondente
                            switch (operacao) {
                                case "saldo":
                                    int numeroContaSaldo = entradaObjeto.readInt();
                                    double saldo = obterSaldo(numeroContaSaldo);
                                    saidaObjeto.writeDouble(saldo);
                                    saidaObjeto.flush();
                                    break;
                                case "saque":
                                    int numeroContaSaque = entradaObjeto.readInt();
                                    double valorSaque = entradaObjeto.readDouble();
                                    boolean saqueSucesso = realizarSaque(numeroContaSaque, valorSaque);
                                    saidaObjeto.writeBoolean(saqueSucesso);
                                    saidaObjeto.flush();
                                    break;
                                case "deposito":
                                    int numeroContaDeposito = entradaObjeto.readInt();
                                    double valorDeposito = entradaObjeto.readDouble();
                                    realizarDeposito(numeroContaDeposito, valorDeposito);
                                    break;
                                case "transferencia":
                                    int contaOrigem = entradaObjeto.readInt();
                                    int contaDestino = entradaObjeto.readInt();
                                    double valorTransferencia = entradaObjeto.readDouble();
                                    boolean transferenciaSucesso = realizarTransferencia(contaOrigem, contaDestino, valorTransferencia);
                                    saidaObjeto.writeBoolean(transferenciaSucesso);
                                    saidaObjeto.flush();
                                    break;
                                case "encerrar":
                                    System.out.println("Conexão encerrada pelo cliente: " + clienteSocket.getInetAddress().getHostAddress());
                                    return;
                                default:
                                    System.out.println("Operação desconhecida: " + operacao);
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });

                threadCliente.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void inicializarContas() {
        int saldoConta = 500;
        int maxContas = 50;

        for (int i = 1; i <= maxContas; i++) {
            contas.put(i, new ContaCorrente(i, saldoConta));
            saldoConta += 500;
        }
    }

    private static double obterSaldo(int numeroConta) {
        ContaCorrente conta = contas.get(numeroConta);
        return (conta != null) ? conta.getSaldo() : -1;
    }

    private static boolean realizarSaque(int numeroConta, double valorSaque) {
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null && conta.getSaldo() >= valorSaque) {
            conta.sacar(valorSaque);
            return true;
        }
        return false;
    }

    private static void realizarDeposito(int numeroConta, double valorDeposito) {
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            conta.depositar(valorDeposito);
        }
    }

    private static boolean realizarTransferencia(int contaOrigem, int contaDestino, double valorTransferencia) {
        ContaCorrente origem = contas.get(contaOrigem);
        ContaCorrente destino = contas.get(contaDestino);

        if (origem != null && destino != null && origem.getSaldo() >= valorTransferencia) {
            origem.sacar(valorTransferencia);
            destino.depositar(valorTransferencia);
            return true;
        }
        return false;
    }
}
