import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BancoRemoto extends Remote {
    double obterSaldo(int numeroConta) throws RemoteException;
    boolean realizarSaque(int numeroConta, double valor) throws RemoteException;
    void realizarDeposito(int numeroConta, double valor) throws RemoteException;
    boolean realizarTransferencia(int contaOrigem, int contaDestino, double valor) throws RemoteException;
}
