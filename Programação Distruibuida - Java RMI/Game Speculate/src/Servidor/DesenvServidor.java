package Servidor;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ClassesJogo.Jogador;
import ClassesJogo.Partida;
import ClassesJogo.Tabuleiro;
import Configuracoes.InterfaceServidor;

public class DesenvServidor implements InterfaceServidor {

	private Map<Integer, Jogador> Lista_Jogadores = new HashMap<Integer, Jogador>();

	private Map<Integer, Partida> Lista_Partidas = new HashMap<Integer, Partida>();

	ArrayList<Integer> Lista_Espera = new ArrayList<Integer>();

	private static int id_jogador = new Random().nextInt();

	private static int id_partida = new Random().nextInt();

	private static int getIdJogador() {
		return ++id_jogador;
	}

	private static int getIdPartida() {
		return ++id_partida;
	}

	/*************
	 * 
	 * AUXILIARES:
	 * 
	 *************
	 */

	// Função para verificar se um jogador está em uma partida.
	private boolean VERIFICA_LISTA_JOGADORES_EM_PARTIDA(Jogador[] lista, int id) {
		for (int i = 0; i < lista.length; i++) {

			if (lista[i].getID() == id)
				return true;
		}
		return false;
	}

	// Função para pegar o ID da partida que um jogador se encontra.
	private int get_ID_PARTIDA(int idJogador) {

		// Percorre partidas.
		for (Map.Entry<Integer, Partida> p : Lista_Partidas.entrySet()) {

			// Verifica se a partida contem o ID do jogador.
			if (VERIFICA_LISTA_JOGADORES_EM_PARTIDA(p.getValue().getJogadores(), idJogador))
				return p.getValue().getID();
		}
		return -1;
	}

	// Função para remover os Jogadores da partida.
	private void REMOVE_JOGADORES_DA_PARTIDA(int idPartida) {

		// RESET JOGADORES PARA OS SEUS PARAMETROS INICIAS.
		Lista_Partidas.get(idPartida).getJogadores()[0].resetJogador();
		Lista_Partidas.get(idPartida).getJogadores()[1].resetJogador();
	}

	// Função para verificar se um jogador existe.
	private boolean VERIFICA_EXISTENCIA_JOGADOR(int id) {

		if (Lista_Jogadores.containsKey(id))
			return true;
		return false;
	}

	// Função para verificar se um jogador está em partida.
	private boolean VERIFICA_SE_ESTA_EM_PARTIDA(int id) {

		if (Lista_Jogadores.containsKey(id))
			if (!Lista_Jogadores.get(id).isEm_Partida())
				return true; // não está em partida
		return false; // está em partida
	}

	// Função para retornar um jogador.
	private Jogador GET_JOGADOR(int id) {

		if (Lista_Jogadores.containsKey(id))
			return Lista_Jogadores.get(id);
		return null;
	}

	// Função para remover jogadores da lista de espera.
	private void REMOVE_JOGADORES_DA_LISTA_DE_ESPERA(int jogador1, int jogador2) {

		if (Lista_Espera.contains(jogador1) && !Lista_Espera.isEmpty()) {
			Lista_Espera.remove(0);
		}

		if (Lista_Espera.contains(jogador2)) {
			Lista_Espera.remove(jogador2);
		}
	}

	// Set configurações iniciais para ambos os jogadores.
	private void ARRANJA_JOGADORES_PARA_JOGAR(Jogador j1, Jogador j2) {

		// player 1 - (inicia jogando)
		j1.setMinha_vez(true);
		j1.setEm_Partida(true);
		j1.setNumero_bolas(15);

		// player 2 - (segundo a jogar)
		j2.setMinha_vez(false);
		j2.setEm_Partida(true);
		j2.setNumero_bolas(15);
	}

	// Função para registrar uma partida.
	private int registraPartida(Jogador j1, Jogador j2, Tabuleiro t) {

		int id_partida = getIdPartida();

		// Verifica se ambos estão cadastrados (garantia)
		if (VERIFICA_EXISTENCIA_JOGADOR(j1.getID()) && VERIFICA_EXISTENCIA_JOGADOR(j2.getID())) {

			// Verifica se já não está em uma partida (garantia)
			if (VERIFICA_SE_ESTA_EM_PARTIDA(j1.getID()) && VERIFICA_SE_ESTA_EM_PARTIDA(j2.getID())) {

				// Jogador jogador1 = GET_JOGADOR(j1.getID());
				// Jogador jogador2 = GET_JOGADOR(j2.getID());

				// Verifica se conseguiu retornar o objeto do jogador (garantia).
				if (j1 != null && j2 != null) {

					// Cria partida.
					Partida p = new Partida(id_partida, j1, j2, j1.getID(), t);
					Lista_Partidas.put(id_partida, p);

					// Remove jogadores que estavam na lista de espera.
					REMOVE_JOGADORES_DA_LISTA_DE_ESPERA(j1.getID(), j2.getID());

					// Set jogadores para a partida.
					ARRANJA_JOGADORES_PARA_JOGAR(GET_JOGADOR(j1.getID()), GET_JOGADOR(j2.getID()));

					return 0;
				}
			}
		}
		return -1; // erro
	}

	// Função para mudar a vez dos jogadores.
	private void trocaVez(int id) {

		// Encontra jogador.
		Jogador j = GET_JOGADOR(id);

		if (j.isEm_Partida()) {

			int idPartida = get_ID_PARTIDA(j.getID());
			Partida p = Lista_Partidas.get(idPartida);

			// VERIFICA SE É O JOGADOR QUE ESTÁ JOGANDOO
			if (p.getJogadorAtual() == j.getID()) {

				// VERIFICA SE O MESMO JOGADOR É O ATUAL
				if (p.getJogadores()[0].equals(j)) {
					p.setJogadorAtual(p.getJogadores()[1].getID());
					p.getJogadores()[0].setMinha_vez(false);
					p.getJogadores()[1].setMinha_vez(true);
					p.resetTempo();
				}

				// SE NÃO FOR
				else {
					p.setJogadorAtual(p.getJogadores()[0].getID());
					p.getJogadores()[0].setMinha_vez(true);
					p.getJogadores()[1].setMinha_vez(false);
					p.resetTempo();
				}
			}
		}
	}

	private boolean verificaTempoDeJogada(int id) {

		Partida p = Lista_Partidas.get(get_ID_PARTIDA(id));

		// VERIFICA SE O JOGADOR DEMOROU MAIS DE 1m PARA REALIZAR A JOGADA.
		if (Math.abs((p.getTempo() - System.currentTimeMillis())) > 60000 && p.getJogadorAtual() == id) {

			// TODO: SET PERDEDOR E GANHADOR
			p.setPerdedor(id);
			return true; // dando erro
		}
		return false;
	}

	private int jogaDado(int id) {

		int dado = (int) (Math.random() * 7);

		// Retorna numero do dado.
		if (dado > 0 && dado < 7)
			return Math.abs(dado);

		// -2 não há partida.
		else if (!GET_JOGADOR(id).isEm_Partida())
			return -2;

		// -3 não é a vez do jogador.
		else if (!GET_JOGADOR(id).isMinha_vez())
			return -3;

		// -4 é a vez do jogador, mas não para jogar dados.

		return -1;
	}

	/*************
	 * 
	 * PRINCIPAIS:
	 * 
	 *************
	 */

	// Função para registrar um Jogador (OK).
	@Override
	public int registraJogador(String nome) throws RemoteException {
		int novoID = getIdJogador();

		try {

			Jogador novoJogador = new Jogador(nome, novoID, false);
			Lista_Jogadores.put(novoID, novoJogador);
			return novoID;

		} catch (Exception e) {
			System.out.println(e);
		}

		return 0;
	}

	// Função para encerrar uma partida (OK).
	@Override
	public int encerraPartida(int id) throws RemoteException {

		if (GET_JOGADOR(id).isEm_Partida()) {

			int id_partida = get_ID_PARTIDA(id);

			if (id_partida != -1) {
				int idPartida_para_Encerrar = id_partida;

				if (Lista_Partidas.containsKey(idPartida_para_Encerrar)) {

					// RESET JOGADORES E REMOVE DA PARTIDA.
					REMOVE_JOGADORES_DA_PARTIDA(idPartida_para_Encerrar);

					// DELETA PARTIDA.
					Lista_Partidas.remove(idPartida_para_Encerrar);

					return 0;
				}

				// Partida não existe.
				return -2;
			}
		}
		// Jogador não está em partida.
		return -1;
	}

	// Função para controlar a fila de espera e o inicio das partidas (OK).
	@Override
	public int temPartida(int id) throws RemoteException {

		// Se Jogador não está na fila de espera e não existe mais ninguem
		if (Lista_Espera.isEmpty()) {
			Lista_Espera.add(id);

			// SET TEMPO PARA LISTA DE ESPERA.

			// Entrou na fila de espera.
			return -1;
		}

		// Se o Jogador já está na fila de espera e só existe ele esperando
		else if (Lista_Espera.contains(id) && Lista_Espera.size() < 1) {

			// Não existe oponente ainda.
			return -2;
		}

		// Se o jogador quer jogar e existe jogador esperando: seta partida.
		else if (!Lista_Espera.isEmpty() && !Lista_Espera.contains(id)) {

			if (Lista_Partidas.size() <= 500) {
				// registra uma partida.
				System.out.println("\n J1: " + Lista_Espera.get(0) + " J2: " + id);

				// pega o primeiro jogador da lista de espera e une em uma partida com o jogador
				// atual.
				registraPartida(GET_JOGADOR(Lista_Espera.get(0)), GET_JOGADOR(id), new Tabuleiro());

				return 0;
			}

			return -3; // NUMERO MAX DE PARTIDAS ATINGIDO.
		}
		return -2;
	}

	// Função para retornar o nome do oponente (OK).
	@Override
	public String obtemOponente(int id) throws RemoteException {

		if (GET_JOGADOR(id).isEm_Partida()) {

			int idP = get_ID_PARTIDA(id);
			Partida p = Lista_Partidas.get(idP);

			if (verificaTempoDeJogada(id)) { // Caso o tempo tenha acabado:
				p.resetTempo();
				trocaVez(id); // Troca a vez para o perdedor não poder mais jogar.
				p.setPartida_acabou(true); // set flag de partida finalizada.
				return "Tempo acabou"; // retornar a informação de derrota para o perdedor.
			}

			else if (p.getJogadores()[0].equals(GET_JOGADOR(id)))
				return p.getJogadores()[1].getNome();
			return p.getJogadores()[0].getNome();
		}
		return null;
	}

	// Função para verificar se é a vez do jogador (OK).
	@Override
	public int ehMinhaVez(int id) throws RemoteException {

		if (GET_JOGADOR(id).isEm_Partida()) {

			int idP = get_ID_PARTIDA(id);
			Partida p = Lista_Partidas.get(idP);

			if (p.isPartida_acabou()) { // Caso a partida tenha sido finalizada.
				return -5; // Informa que o atual jogador ganhou.
			}

			else if (verificaTempoDeJogada(id)) { // Caso o tempo tenha acabado:
				p.resetTempo();
				trocaVez(id); // Troca a vez para o perdedor não poder mais jogar.
				p.setPartida_acabou(true); // set flag de partida finalizada.
				return -2; // retornar a informação de derrota para o perdedor.
			}

			else if (GET_JOGADOR(id).isMinha_vez())

				return 0;
		}
		return -1;
	}

	// Função para retornar o numero de bolas de um jogador (OK).
	@Override
	public int obtemNumBolas(int id) throws RemoteException {

		if (GET_JOGADOR(id).isEm_Partida()) {

			int idP = get_ID_PARTIDA(id);
			Partida p = Lista_Partidas.get(idP);

			if (p.isPartida_acabou()) { // Caso a partida tenha sido finalizada.
				return -5; // Informa que o atual jogador ganhou.
			}

			else if (verificaTempoDeJogada(id)) { // Caso o tempo tenha acabado:
				p.resetTempo();
				trocaVez(id); // Troca a vez para o perdedor não poder mais jogar.
				p.setPartida_acabou(true); // set flag de partida finalizada.
				return -2; // retornar a informação de derrota para o perdedor.
			}

			// Atualiza numero de bolas
			else if (Lista_Partidas.get(id_partida).getJogadores()[0].getID() == GET_JOGADOR(id).getID()) {
				return Lista_Partidas.get(id_partida).getNumero_bolas_j1();
			}

			else if (Lista_Partidas.get(id_partida).getJogadores()[1].getID() == GET_JOGADOR(id).getID()) {
				return Lista_Partidas.get(id_partida).getNumero_bolas_j2();
			}
		}
		return -1;
	}

	// Função para retornar o numero de bolas do oponente (OK).
	@Override
	public int obtemNumBolasOponente(int id) throws RemoteException {

		if (GET_JOGADOR(id).isEm_Partida()) {

			int id_partida = get_ID_PARTIDA(id);

			int idP = get_ID_PARTIDA(id);
			Partida p = Lista_Partidas.get(idP);

			if (p.isPartida_acabou()) { // Caso a partida tenha sido finalizada.
				return -5; // Informa que o atual jogador ganhou.
			}

			else if (verificaTempoDeJogada(id)) { // Caso o tempo tenha acabado:
				p.resetTempo();
				trocaVez(id); // Troca a vez para o perdedor não poder mais jogar.
				p.setPartida_acabou(true); // set flag de partida finalizada.
				return -2; // retornar a informação de derrota para o perdedor.
			}

			else if (Lista_Partidas.get(id_partida).getJogadores()[0].getID() == GET_JOGADOR(id).getID()) {
				return Lista_Partidas.get(id_partida).getNumero_bolas_j2();
			}

			else if (Lista_Partidas.get(id_partida).getJogadores()[1].getID() == GET_JOGADOR(id).getID()) {
				return Lista_Partidas.get(id_partida).getNumero_bolas_j1();
			}
		}
		return -1;
	}

	// Função para retonar o tabulerio atual da partida ().
	@Override
	public String obtemTabuleiro(int id) throws RemoteException {

		// SE O JOGADOR ESTIVER EM PARTIDA
		if (GET_JOGADOR(id).isEm_Partida()) {

			int idP = get_ID_PARTIDA(id);
			Partida p = Lista_Partidas.get(idP);

			if (p.isPartida_acabou()) { // Caso a partida tenha sido finalizada.
				return "Ganhou"; // Informa que o atual jogador ganhou.
			}

			else if (verificaTempoDeJogada(id)) { // Caso o tempo tenha acabado:
				p.resetTempo();
				trocaVez(id); // Troca a vez para o perdedor não poder mais jogar.
				p.setPartida_acabou(true); // set flag de partida finalizada.
				return "Tempo acabou"; // retornar a informação de derrota para o perdedor.
			}

			String outputTabuleiro = "";

			for (int i = 0; i < p.getTabuleiro().getTabuleiro().length; i++) {

				outputTabuleiro = outputTabuleiro + " | " + p.getTabuleiro().getTabuleiro()[i];
			}
			return outputTabuleiro;
		}
		return null;
	}

	// Função que determina o número de lançamentos que o jogador realizará ().
	@Override
	public int defineJogadas(int id, int numeroJogadas) throws RemoteException {

		// 1) Verifica se o jogador está em partida
		if (GET_JOGADOR(id).isEm_Partida()) {

			if (ehMinhaVez(id) == 0) {

				// 2) Verifica se o numero de jogadas é valido
				if (numeroJogadas > 1 && numeroJogadas < GET_JOGADOR(id).getNumero_bolas()) {
					GET_JOGADOR(id).setNumero_jogadas_fazer(numeroJogadas);

					return 1;
				}

				return -3; // não é minha vez.
			}
			return -2; // ainda não está em partida.
		}
		return -1;
	}

	// Função para realizar o lançamento do dado ().

	public int retorna_se_perdeu(int id) {

		if (GET_JOGADOR(id).isEm_Partida()) {

			int id_partida = get_ID_PARTIDA(id);

			if (verificaTempoDeJogada(id)) {
				return 1;
			}

			else if (Lista_Partidas.get(id_partida).getPerdedor() == id) {
				return 0;
			}
			return -1; // NÃO ESTÁ EM PARTIDA.
		}
		return -2; // ERRO
	}

	public int retorna_se_ganhou(int id) {

		if (GET_JOGADOR(id).isEm_Partida()) {

			int id_partida = get_ID_PARTIDA(id);

			if (Lista_Partidas.get(id_partida).getGanhador() == id) {
				return 0;
			}

			return -1; // NÃO ESTÁ EM PARTIDA
		}
		return -2; // ERRO
	}

	public String realizaJogada(int id, int numero_jogadas) throws RemoteException {

		if (GET_JOGADOR(id).isEm_Partida()) { // Verifica se o jgoador está em partida.

			System.out.println("Numero de bolas atuais: " + GET_JOGADOR(id).getNumero_bolas());

			// PARAMETROS:
			int id_partida = get_ID_PARTIDA(id);
			String relatorio = new String();
			Partida p = Lista_Partidas.get(id_partida);

			if (!verificaTempoDeJogada(id)) { // verifica se já expirou o tempo de jogada.

				p.resetTempo();

				for (int i = 0; i <= numero_jogadas; i++) { // LAÇO COM O NUMERO DE JOGADAS

					int numero_dado = jogaDado(id) - 1;

					// VERIFICA SE O JOGADOR GANHOU
					// (OBS: NÃO IMPORTA A VERIFICAÇÃO, POIS É O JOGADOR ATUAL QUE ESTÁ JOGANDO).
					if (Lista_Partidas.get(id_partida).getNumero_bolas_j1() == 0
							|| Lista_Partidas.get(id_partida).getNumero_bolas_j2() == 0) {
						relatorio = "Ganhou";

						// SET O JOGADOR QUE FOI DERROTADO E O QUE GANHOU.
						if (Lista_Partidas.get(id_partida).getJogadores()[0] == GET_JOGADOR(id)) {

							// set perdedor.
							Lista_Partidas.get(id_partida)
									.setPerdedor(Lista_Partidas.get(id_partida).getJogadores()[1].getID());

							// set ganhador.
							Lista_Partidas.get(id_partida)
									.setGanhador(Lista_Partidas.get(id_partida).getJogadores()[0].getID());

						} else if (Lista_Partidas.get(id_partida).getJogadores()[1] == GET_JOGADOR(id)) {

							// set perdedor.
							Lista_Partidas.get(id_partida)
									.setPerdedor(Lista_Partidas.get(id_partida).getJogadores()[0].getID());

							// set ganhador.
							Lista_Partidas.get(id_partida)
									.setGanhador(Lista_Partidas.get(id_partida).getJogadores()[1].getID());
						}

						// RETORNAR PARA O JOGADOR ATUAL QUE ELE GANHOU A PARTIDA.
						return relatorio;
					}

					else if (i == numero_jogadas) {

						// Atualiza numero de bolas
						if (Lista_Partidas.get(id_partida).getJogadores()[0].getID() == GET_JOGADOR(id).getID()) {
							relatorio += "\n Numero de bolas restantes: "
									+ Lista_Partidas.get(id_partida).getNumero_bolas_j1();
						}

						if (Lista_Partidas.get(id_partida).getJogadores()[1].getID() == GET_JOGADOR(id).getID()) {
							relatorio += "\n Numero de bolas restantes: "
									+ Lista_Partidas.get(id_partida).getNumero_bolas_j2();
						}

						trocaVez(id);
						return relatorio;
					}

					else if (Lista_Partidas.get(id_partida).getTabuleiro().getTabuleiro()[Math
							.abs(numero_dado)] == 'X') {

						// Atualiza numero de bolas
						if (Lista_Partidas.get(id_partida).getJogadores()[0].getID() == GET_JOGADOR(id).getID()) {
							System.out.println("É o jogador 1.");
							Lista_Partidas.get(id_partida)
									.setNumero_bolas_j1(Lista_Partidas.get(id_partida).getNumero_bolas_j1() + 1);
						}
						if (Lista_Partidas.get(id_partida).getJogadores()[1].getID() == GET_JOGADOR(id).getID()) {
							System.out.println("É o jogador 2.");
							Lista_Partidas.get(id_partida)
									.setNumero_bolas_j2(Lista_Partidas.get(id_partida).getNumero_bolas_j2() + 1);
						}

						// Atualiza relatorio de jogadas.
						Lista_Partidas.get(id_partida).getTabuleiro().getTabuleiro()[Math.abs(numero_dado)] = '-';
						relatorio += " Jogador atingiu a casa " + (Math.abs(numero_dado) + 1)
								+ " (OCUPADA). +1 bola \n";
					}

					else if (Lista_Partidas.get(id_partida).getTabuleiro().getTabuleiro()[Math
							.abs(numero_dado)] == '-') {

						if ((numero_dado + 1) == 6) {

							// Atualiza numero de bolas
							if (Lista_Partidas.get(id_partida).getJogadores()[0].getID() == GET_JOGADOR(id).getID()) {
								System.out.println("É o jogador 1.");
								Lista_Partidas.get(id_partida)
										.setNumero_bolas_j1(Lista_Partidas.get(id_partida).getNumero_bolas_j1() - 1);
							}
							if (Lista_Partidas.get(id_partida).getJogadores()[1].getID() == GET_JOGADOR(id).getID()) {
								System.out.println("É o jogador 2.");
								Lista_Partidas.get(id_partida)
										.setNumero_bolas_j2(Lista_Partidas.get(id_partida).getNumero_bolas_j2() - 1);
							}

							// Atualiza relatorio de jogadas.
							relatorio += " Jogador atingiu a casa " + (Math.abs(numero_dado) + 1)
									+ " (CANELETA).   -1 bola \n";
						}

						else if ((numero_dado + 1) != 6) {
							// Atualiza numero de bolas
							if (Lista_Partidas.get(id_partida).getJogadores()[0].getID() == GET_JOGADOR(id).getID()) {
								System.out.println("É o jogador 1.");
								Lista_Partidas.get(id_partida)
										.setNumero_bolas_j1(Lista_Partidas.get(id_partida).getNumero_bolas_j1() - 1);
							}
							if (Lista_Partidas.get(id_partida).getJogadores()[1].getID() == GET_JOGADOR(id).getID()) {
								System.out.println("É o jogador 2.");
								Lista_Partidas.get(id_partida)
										.setNumero_bolas_j2(Lista_Partidas.get(id_partida).getNumero_bolas_j2() - 1);
							}
							// Atualiza relatorio de jogadas.
							Lista_Partidas.get(id_partida).getTabuleiro().getTabuleiro()[Math.abs(numero_dado)] = 'X';
							relatorio += " Jogador atingiu a casa " + (Math.abs(numero_dado) + 1)
									+ " (LIVRE).   -1 bola \n";
						}
					}
				}
			}

			relatorio = "Acabou o tempo";
			Lista_Partidas.get(id_partida).setPerdedor(id);
			return relatorio;
		}
		return null;
	}

}
