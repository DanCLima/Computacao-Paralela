import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Servidor {

    public static void main(String[] args) {
        try {
            BancoRemotoImpl bancoRemotoImpl = new BancoRemotoImpl();

            // Inicializa o registro do RMI na porta 1099
            LocateRegistry.createRegistry(1099);

            // Registra o serviço RMI com o nome "BancoRemoto"
            Naming.rebind("//localhost/BancoRemoto", bancoRemotoImpl);

            System.out.println("Servidor RMI aguardando chamadas...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}