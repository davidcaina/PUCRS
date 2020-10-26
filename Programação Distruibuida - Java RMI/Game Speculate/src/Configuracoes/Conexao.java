package Configuracoes;

public class Conexao {

	public static final String CODEBASE = "java.rmi.server.codebase";	
	
	public static void setCodeBase(Class<?> c) {
		String root = c.getProtectionDomain().getCodeSource().getLocation().toString();
		
		String path = System.getProperty(CODEBASE);
		
		if(path != null && !path.isEmpty()) {
			root = path + " " + root;
		}
		System.setProperty(CODEBASE, root);
	}
	
}
