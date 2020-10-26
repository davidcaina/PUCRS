package Configuracoes;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceServidor extends Remote {
	
	// 1)
	
	/*
	 * # -1: usuario já está cadastrado.
	 * # -2: numero maximo de jogadores. 
	 * # ID: retorna id do usuario.
	 * */
	public int registraJogador(String nome) throws RemoteException;
	
	
	// 2)
	
	/*
	 * #  0: sucesso. 
	 * # -1: erro.
	 * */
	public int encerraPartida(int id) throws RemoteException;

	
	// 3)
	
	/*
	 * #  ID: sucesso. 
	 * #  -2: tempo esgotado.
	 * #  -1: erro.
	 * #   0: ainda não há partidas.
	 * #   1: encontrou partida (comeca jogando).
	 * #   2: encontrou partida (segundo a jogar).
	 * */
	public int temPartida(int id) throws RemoteException;

	
	// 4)
	
	/*
	 * #  ID: sucesso. 
	 * #  vazio: erro.
	 * #  nome do oponente.
	 * */
	public String obtemOponente(int id) throws RemoteException;

	
	// 5)
	
	/*
	 * #  -2: ainda não há jogadores. 
	 * #  -1: erro.
	 * #   0: não.
	 * #   1: sim.
	 * #   2: vencedor.
	 * #   3: perdedor.
	 * #   5: ganhou por WO
	 * #   0: perdeu por WO
	 * */
	public int ehMinhaVez(int id) throws RemoteException;
	
	
	// 6)
	
	/*
	 * #   -2: ainda não há dois jogadores. 
	 * #   -1: jogador não encontrado.
	 * */
	public int obtemNumBolas(int id) throws RemoteException;

	
	// 7)
	
	/*
	 * #   -2: ainda não há dois jogadores. 
	 * #   -1: jogador não encontrado.
	 * */
	public int obtemNumBolasOponente(int id) throws RemoteException;
	
	
	// 8)
	
	
	public String obtemTabuleiro(int id) throws RemoteException;
	
	
	// 9)
	public int defineJogadas(int id, int numeroJogadas) throws RemoteException;
	

	public String realizaJogada(int id, int numero_jogadas)throws RemoteException;
	
	public int retorna_se_perdeu(int id)throws RemoteException;
	
	public int retorna_se_ganhou(int id)throws RemoteException;
	
}
