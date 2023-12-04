import java.io.Serializable;

public class ContaCorrente implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numeroConta;
    private double saldo;

    public ContaCorrente(int numeroConta, double saldoInicial) {
        this.numeroConta = numeroConta;
        this.saldo = saldoInicial;
    }

    public int getNumeroConta() {
        return numeroConta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        saldo += valor;
    }

    public void sacar(double valor) {
        saldo -= valor;
    }
}
