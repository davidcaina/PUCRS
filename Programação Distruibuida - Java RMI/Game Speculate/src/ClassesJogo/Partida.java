package ClassesJogo;


public class Partida {

	private Jogador jogadores[] = new Jogador[2];

	private Tabuleiro tabuleiro;

	private long tempo_de_inicio;
	private long tempo_de_jogada;
	private long tempo_de_encerrar;

	private int ID;
	private int jogadorAtual;
	private int perdedor;
	private int ganhador;
	private int numero_bolas_j1;
	private int numero_bolas_j2;

	private boolean partida_acabou;

	public Partida(int id, Jogador j1, Jogador j2, int atual, Tabuleiro t) {
		setID(id);
		getJogadores()[0] = j1;
		getJogadores()[1] = j2;

		setJogadorAtual(atual);
		setTabuleiro(t);
		setPerdedor(0);
		setPartida_acabou(false);

		setNumero_bolas_j1(15);
		setNumero_bolas_j2(15);

		// TEMPO SETADO PARA VERIFICAR O TEMPO DE JOGADAS.
		setTempo(System.currentTimeMillis());

		// TEMPO SETADO PARA VERIFICAR O TERMINO DE UMA PARTIDA.
		//setTempo_de_encerrar(System.currentTimeMillis());
		
		// TEMPO SETADO PARA VERIFICAR O INICIO DE UMA PARTIDA.
		//setTempo_de_inicio(System.currentTimeMillis());
	}

	public long getTempo_de_inicio() {
		return tempo_de_inicio;
	}

	public void setTempo_de_inicio(long tempo_de_inicio) {
		this.tempo_de_inicio = tempo_de_inicio;
	}

	public long getTempo_de_encerrar() {
		return tempo_de_encerrar;
	}

	public void setTempo_de_encerrar(long tempo_de_encerrar) {
		this.tempo_de_encerrar = tempo_de_encerrar;
	}

	public int getGanhador() {
		return ganhador;
	}

	public void setGanhador(int ganhador) {
		this.ganhador = ganhador;
	}

	public int getNumero_bolas_j1() {
		return numero_bolas_j1;
	}

	public void setNumero_bolas_j1(int numero_bolas_j1) {
		this.numero_bolas_j1 = numero_bolas_j1;
	}

	public int getNumero_bolas_j2() {
		return numero_bolas_j2;
	}

	public void setNumero_bolas_j2(int numero_bolas_j2) {
		this.numero_bolas_j2 = numero_bolas_j2;
	}

	public boolean isPartida_acabou() {
		return partida_acabou;
	}

	public void setPartida_acabou(boolean partida_acabou) {
		this.partida_acabou = partida_acabou;
	}

	public int getPerdedor() {
		return perdedor;
	}

	public void setPerdedor(int perdedor) {
		this.perdedor = perdedor;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public long getTempo() {
		return tempo_de_jogada;
	}

	public void setTempo(long l) {
		this.tempo_de_jogada = l;
	}

	public Jogador[] getJogadores() {
		return jogadores;
	}

	public void setJogadores(Jogador[] jogadores) {
		this.jogadores = jogadores;
	}

	public int getJogadorAtual() {
		return jogadorAtual;
	}

	public void setJogadorAtual(int jogadorAtual) {
		this.jogadorAtual = jogadorAtual;
	}

	public Tabuleiro getTabuleiro() {
		return tabuleiro;
	}

	public void setTabuleiro(Tabuleiro tabuleiro) {
		this.tabuleiro = tabuleiro;
	}

	public void resetTempo() {
		setTempo(System.currentTimeMillis());
	}
}
