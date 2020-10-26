package Servidor;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import Configuracoes.Conexao;
import Configuracoes.InterfaceServidor;

public class MainServidor {
	// TODO
		// rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false

		public static void main(String[] args) throws RemoteException {

			Conexao.setCodeBase(InterfaceServidor.class);

			try {
				DesenvServidor servidor = new DesenvServidor();
				InterfaceServidor remote = (InterfaceServidor) UnicastRemoteObject.exportObject(servidor, 8888);

				Registry registry = LocateRegistry.getRegistry();
				registry.rebind("Speculate", remote);

				System.err.println(" ####################");
				System.err.println(" # SERVIDOR RODANDO #");
				System.err.println(" #################### \n");

			} catch (ConnectException e) {
				System.out.println("\n\n#Necessario rodar servidor com RMI registry# \n Comando:");

				System.err.println(" Java -> JDK -> BIN \n rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false");

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
}
