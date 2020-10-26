
package Cliente;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import Configuracoes.InterfaceServidor;

//import ClassesJogo.Temporizacao;
public class MainCliente {

	private static int id = 0;
	private static Console console = System.console();
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private static InterfaceServidor servidor;

	private static String nome;

	public static void main(String[] args) throws Exception {
		Registry registry = LocateRegistry.getRegistry();
		servidor = (InterfaceServidor) registry.lookup("Speculate");

		gui();
	}

	private static void gui() throws RemoteException {

		int opt = 0;

		do {
			opt = menu("MENU INICIAL", new String[] { "Registrar-se", "Sair" });

			switch (opt) {

			// REGISTRA JOGADOR
			case 0:

				// VERIFICA SE FOI CONFIRMADO [s/n]
				if (registraJogador()) {

					do {

						System.out.println("================= ");
						System.out.println("Jogador: " + getNome());
						System.out.println("================= \n");

						int opt2 = menu("MENU JOGADOR", new String[] { "Encontrar Partida                  |",
								"Obter Nome do Oponente             |", "Obter Numero de bolas do Oponente  |",
								"Obter Numero de bolas Suas         |", "Obter Tabuleiro                    |",
								"Verificar Turno                    |", "Realizar Jogada                    |",
								"Encerrar Partida                   |", "Sair                               |" });

						switch (opt2) {

						// ENCONTRA PARTIDA
						case 0:
							temPartida(getId());
							break;

						// NOME OPONENENTE
						case 1:
							obtemNomeOponente(getId());
							break;

						// NUMERO BOLAS OPONENTE
						case 2:
							obtemNumBolasOponente(getId());
							break;

						// BOLAS SUAS
						case 3:
							obtemNumBolas(getId());
							break;

						// VISUALIZA TABULEIRO
						case 4:
							obtemTabuleiro(getId());
							break;

						// VERIFICAR TURNO
						case 5:
							ehMinhaVez(getId());
							break;

						// REALIZAR JOGADA
						case 6:
							try {
								inputMove(); // Pega e valida o input do jogador.

							} catch (Exception e) {
								e.printStackTrace();
							}
							break;

						// ENCERRA PARTIDA
						case 7:
							encerraPartida(getId());
							break;

						// SAIR
						case 8:
							break;
						}

					} while (opt != 1);
				}
				break;

			case 1:
				opt = 1;
				break;

			} // switch (opt)

		} while (opt != 1);

		// APLICAÇÃO ENCERRADA.
		System.out.println("Desconectado.");
	}

	/*
	 * ************************* APLICAÇÕES DE RMI *************************
	 */

	// RMI - REGISTRO DE CLIENTE (OK).
	private static boolean registraJogador() throws RemoteException {

		String nome = input("Registrando-se ", " Insira seu nome: ");

		setNome(nome);

		if (nome != null && !nome.isEmpty() && nome != "n") {

			try {
				if (id != -1) {
					setId(servidor.registraJogador(nome));
					// varN = retornaJogadorNome(id);
					// varC = retornaJogadorCor(id);
					System.out.println("============== Cadastro realizado ==============");
					System.out.println(" Jogador: " + getNome());
					System.out.println("\n Id: " + getId());
					System.out.println("================================================= \n\n");
					return true;
				}
				return false;
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return false;
	}

	// RMI - ENCERRA PARTIDA
	private static void encerraPartida(int id) throws RemoteException {

		int res = servidor.encerraPartida(id);

		if (res == -1)
			System.out.println("Erro ao encerrar a partida.\n");
		else if (res == -2)
			System.out.println("Jogador não está em uma partida.\n");
		else if (res == 0)
			System.out.println("Partida encerrada com sucesso.\n");
	}

	// RMI - TEM PARTIDA
	private static void temPartida(int id) throws RemoteException {

		int verificao = servidor.retorna_se_perdeu(id);

		// VERIFICA SE AINDA NÃO HOUVE UM GANHADOR.
		if (verificao == 1) {
			System.out.println("Você perdeu. O tempo de jogada se encerrou.");
			encerraPartida(id);

		} else if (verificao == 0) {
			System.out.println("Você perdeu. O jogador encerrou o numero de bolas primeiro.");
			encerraPartida(id);
		} else {
			int res = servidor.temPartida(id);

			if (res == -1)
				System.out.println("Jogador entrou na fila de espera.\n");
			else if (res == -2)
				System.out.println("Jogador já se encontra na fila.\n");
			else if (res == 0)
				System.out.println("Jogador encontrou um oponente.\n");
		}
	}

	// RMI - OBTEM NOME DO OPONENTE
	private static void obtemNomeOponente(int id) throws RemoteException {

		int verificao = servidor.retorna_se_perdeu(id);

		// VERIFICA SE AINDA NÃO HOUVE UM GANHADOR.
		if (verificao == 1) {
			System.out.println("Você perdeu. O tempo de jogada se encerrou.");
			encerraPartida(id);

		} else if (verificao == 0) {
			System.out.println("Você perdeu. O jogador encerrou o numero de bolas primeiro.");
			encerraPartida(id);
		} else {
			String res = servidor.obtemOponente(id);

			if (res == "Ganhou") {
				System.out.println("Parabens! Você ganhou a partida.");
				encerraPartida(id);
			} else if (res == "Tempo acabou") {
				System.out.println("Você perdeu devido o tempo.");
			} else if (res == null)
				System.out.println("Jogador não está em uma partida");
			else if (res != null)
				System.out.println("O nome do oponente é: " + res);
		}

	}

	// RMI - EH MINHA VEZ
	private static void ehMinhaVez(int id) throws RemoteException {

		int verificao = servidor.retorna_se_perdeu(id);

		// VERIFICA SE AINDA NÃO HOUVE UM GANHADOR.
		if (verificao == 1) {
			System.out.println("Você perdeu. O tempo de jogada se encerrou.");
			encerraPartida(id);

		} else if (verificao == 0) {
			System.out.println("Você perdeu. O jogador encerrou o numero de bolas primeiro.");
			encerraPartida(id);

		} else {

			int res = servidor.ehMinhaVez(id);

			 if (res == -5) {
				System.out.println("Parabens! Você ganhou a partida");
			} else if (res == -2) {
				System.out.println("Você perdeu devido o tempo.");
			} else if (res == -1)
				System.out.println("Jogador não está em uma partida.");
			else if (res == 0)
				System.out.println("É a vez do jogador.");
		}
	}

	// RMI - OBTEM TABULEIRO
	private static void obtemTabuleiro(int id) throws RemoteException {

		int verificao = servidor.retorna_se_perdeu(id);

		// VERIFICA SE AINDA NÃO HOUVE UM GANHADOR.
		if (verificao == 1) {
			System.out.println("Você perdeu. O tempo de jogada se encerrou.");
			encerraPartida(id);

		} else if (verificao == 0) {
			System.out.println("Você perdeu. O jogador encerrou o numero de bolas primeiro.");
			encerraPartida(id);
		} else {
			String res = servidor.obtemTabuleiro(id);

			 if (res == "Ganhou") {
				System.out.println("Parabens! Você ganhou a partida");
			} else if (res == "Tempo acabou") {
				System.out.println("Você perdeu devido o tempo.");
			} else if (res == null)
				System.out.println("Jogador não está em uma partida.");
			else if (res != null)
				System.out.println("Tabuleiro atual: \n\n" + res);
		}
	}

	// RMI - REALIZA MOVIMENTO
	private static void obtemNumBolas(int id) throws RemoteException {

		int verificao = servidor.retorna_se_perdeu(id);

		// VERIFICA SE AINDA NÃO HOUVE UM GANHADOR.
		if (verificao == 1) {
			System.out.println("Você perdeu. O tempo de jogada se encerrou.");
			encerraPartida(id);

		} else if (verificao == 0) {
			System.out.println("Você perdeu. O jogador encerrou o numero de bolas primeiro.");
			encerraPartida(id);
		} else {
			int res = servidor.obtemNumBolas(id);

			if (res == -5) {
				System.out.println("Parabens! Você ganhou a partida");
			} else if (res == -2) {
				System.out.println("Você perdeu devido o tempo.");
			} else if (res == -1)
				System.out.println("Jogador não está em uma partida.");
			else if (res != -1)
				System.out.println("Numero de bolas é igual a: " + res);
		}
	}

	// RMI -
	private static void obtemNumBolasOponente(int id) throws RemoteException {

		int verificao = servidor.retorna_se_perdeu(id);

		// VERIFICA SE AINDA NÃO HOUVE UM GANHADOR.
		if (verificao == 1) {
			System.out.println("Você perdeu. O tempo de jogada se encerrou.");
			encerraPartida(id);

		} else if (verificao == 0) {
			System.out.println("Você perdeu. O jogador encerrou o numero de bolas primeiro.");
			encerraPartida(id);
		} else {
			int res = servidor.obtemNumBolasOponente(id);

			if (res == -5) {
				System.out.println("Parabens! Você ganhou a partida");
			} else if (res == -2) {
				System.out.println("Você perdeu devido o tempo.");
			} else if (res == -1)
				System.out.println("Jogador não está em uma partida.");
			else if (res != -1)
				System.out.println("Numero de bolas do oponente é: " + res);
		}
	}

	private static void realizarJogada(int id, int numero_jogadas) throws RemoteException {

		int verificao = servidor.retorna_se_perdeu(id);

		// VERIFICA SE AINDA NÃO HOUVE UM GANHADOR.
		if (verificao == 1) {
			System.out.println("Você perdeu. O tempo de jogada se encerrou.");
			encerraPartida(id);

		} else if (verificao == 0) {
			System.out.println("Você perdeu. O outro jogador encerrou o numero de bolas primeiro.");
			encerraPartida(id);
		} else {
			String res = servidor.realizaJogada(id, numero_jogadas);

			if (res == null) {
				System.out.println("Jogador não está em partida.");
			} else if (res != null) {
				System.out.println(" \n\n###### RELATORIO DE JOGADAS ######\n");
				System.out.println(res);
				System.out.println("\n#######################################\n");
			}
		}
	}

	/*
	 * ************************* APLICAÇÕES DO CONSOLE *************************
	 */

	// VALIDA OS INPUTS DO CLIENTE E FAZ JOGADA.
	private static void inputMove() throws Exception {

		if (servidor.ehMinhaVez(id) == 0) {
			while (true) {

				System.out.printf("Insira o numero de jogadas que quer realizar (1 a %d): ",
						servidor.obtemNumBolas(getId()));
				String num_jogadas = readLine();

				try {
					int eh_inteiro = Integer.parseInt(num_jogadas);

					if (eh_inteiro < 0 || eh_inteiro > servidor.obtemNumBolas(getId()))
						System.out.println("Jogada invalida!");

					else if (eh_inteiro > 0 && eh_inteiro <= servidor.obtemNumBolas(getId())) {
						realizarJogada(getId(), eh_inteiro);
						break;
					}
				} catch (NumberFormatException ex) {
					System.out.println("É necessario informar um numero.");
				}
			}
		}
		System.out.println("Não é a vez do jogador.");
	}

	// INPUT DE DADOS
	@SuppressWarnings("unused")
	private static String[] input(String name, String[] msgs) {
		String[] inputs = new String[msgs.length];
		System.out.println(" === " + name + " ===");

		for (int i = 0; i < msgs.length; i++) {
			inputs[i] = input(msgs[i]);
		}

		return inputs;
	}

	// INPUT DE DADOS - Inserir nome.
	private static String input(String name, String msg) {
		System.out.println("===== " + name + " =====");

		return input(msg);
	}

	// INPUT DE DADOS - Confirmação de nome.
	private static String input(String msg) {
		System.out.println(msg);
		String line = readLine();

		int opt = 2;
		while (opt == 2) {

			System.out.println("Confirmar? [s/n] ");
			String sn = readLine();

			if (sn.startsWith("s"))
				opt = 1;
			if (sn.startsWith("n"))
				opt = 0;

			System.out.println();

		}
		if (opt == 1)
			return line;
		else
			return "n";
	}

	// AUX
	private static String readLine() {
		if (console != null)
			return console.readLine();

		try {
			return reader.readLine();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// MENU
	private static int menu(String name, String[] entradas) throws RemoteException {

		System.out.println("=== " + name + " === \n");
		System.out.println("Selecione uma opção:");

		for (int i = 0; i < entradas.length; i++) {
			System.out.println(i + 1 + ".- " + entradas[i]);
		}

		int opt = -1;

		String aux_opt;

		do {

			System.out.print("\n Opção: ");

			aux_opt = readLine().trim();

			try {
				opt = Integer.parseInt(aux_opt);
			} catch (NumberFormatException ex) {
				System.err.println(" \n Opção invalida.");
				opt = -1;
			}

			if (opt - 1 >= entradas.length || opt <= 0) {
				System.out.println("Entre com uma opção de 1 até " + entradas.length);
				opt = -1;
			}
		} while (opt == -1);

		System.out.println("\n Opção selecionada: " + opt + " ");

		System.out.println("========================= \n");

		return opt - 1;
	}

	public static int getId() {
		return id;
	}

	public static void setId(int id) {
		MainCliente.id = id;
	}

	public static String getNome() {
		return nome;
	}

	public static void setNome(String nomeA) {
		nome = nomeA;
	}
}
