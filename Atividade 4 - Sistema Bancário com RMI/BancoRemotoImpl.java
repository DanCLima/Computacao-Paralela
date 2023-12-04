import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class BancoRemotoImpl extends UnicastRemoteObject implements BancoRemoto {
    private static final long serialVersionUID = 1L;
    private final Map<Integer, ContaCorrente> contas;

    protected BancoRemotoImpl() throws RemoteException {
        super();
        contas = new HashMap<>();
        inicializarContas();
    }

    @Override
    public double obterSaldo(int numeroConta) throws RemoteException {
        ContaCorrente conta = contas.get(numeroConta);
        return (conta != null) ? conta.getSaldo() : -1;
    }

    @Override
    public boolean realizarSaque(int numeroConta, double valor) throws RemoteException {
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null && conta.getSaldo() >= valor) {
            conta.sacar(valor);
            return true;
        }
        return false;
    }

    @Override
    public void realizarDeposito(int numeroConta, double valor) throws RemoteException {
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            conta.depositar(valor);
        }
    }

    @Override
    public boolean realizarTransferencia(int contaOrigem, int contaDestino, double valorTransferencia) throws RemoteException {
        ContaCorrente origem = contas.get(contaOrigem);
        ContaCorrente destino = contas.get(contaDestino);

        if (origem != null && destino != null && origem.getSaldo() >= valorTransferencia) {
            origem.sacar(valorTransferencia);
            destino.depositar(valorTransferencia);
            return true;
        }
        return false;
    }

    private static int maxContas = 50;
    
    private void inicializarContas() {
        int saldoConta = 500;

        for (int i = 1; i <= maxContas; i++) {
            contas.put(i, new ContaCorrente(i, saldoConta));
            saldoConta += 500;
        }
    }

    public static int obterQtdContas () {
        return maxContas;
    }
}
