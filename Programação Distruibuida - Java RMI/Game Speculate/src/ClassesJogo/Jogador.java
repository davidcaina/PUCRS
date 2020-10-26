package ClassesJogo;

public class Jogador {

	private int numero_bolas;
	private int ID;
	private int numero_jogadas_fazer;
	
	private String nome;
	
	private boolean em_Partida;
	private boolean minha_vez;
	private boolean ja_definiu_numero_jogadas;

	public Jogador(String n, int id, boolean eP) {
		setID(id);
		setNome(n);
		setNumero_bolas(15);
		setEm_Partida(eP);
		setMinha_vez(false);
		setJa_definiu_numero_jogadas(false);
	}

	
	public void resetJogador() {
		setNumero_bolas(15);
		setEm_Partida(false);
		setMinha_vez(false);
		setJa_definiu_numero_jogadas(false);
	}
	
	public int getNumero_bolas() {
		return numero_bolas;
	}

	public void setNumero_bolas(int numero_bolas) {
		this.numero_bolas = numero_bolas;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public boolean isJa_definiu_numero_jogadas() {
		return ja_definiu_numero_jogadas;
	}

	public void setJa_definiu_numero_jogadas(boolean ja_definiu_numero_jogadas) {
		this.ja_definiu_numero_jogadas = ja_definiu_numero_jogadas;
	}

	public int getNumero_jogadas_fazer() {
		return numero_jogadas_fazer;
	}

	public void setNumero_jogadas_fazer(int numero_jogadas_fazer) {
		this.numero_jogadas_fazer = numero_jogadas_fazer;
	}

	public boolean isEm_Partida() {
		return em_Partida;
	}

	public void setEm_Partida(boolean em_Partida) {
		this.em_Partida = em_Partida;
	}

	public boolean isMinha_vez() {
		return minha_vez;
	}

	public void setMinha_vez(boolean minha_vez) {
		this.minha_vez = minha_vez;
	}

	// Metodo para decrementar o numero de bolas
	public void incrementaBolas() {
		setNumero_bolas(getNumero_bolas() + 1);
	}

	// Metodo para decrementar o numero de bolas
	public void decrementaBolas() {
		setNumero_bolas(getNumero_bolas() - 1);
	}
}