package ClassesJogo;

public class Tabuleiro {

	// Representa as casas do tabuleiro
	private char tabuleiro[];

	public Tabuleiro() {

		setTabuleiro(new char[6]);
		
		// Inicia as casas com as posições 1,3 e 5 ocupadas.
		for (int i = 0; i < 6; i++) {
			tabuleiro[i] = '-';
		}

		tabuleiro[0] = 'X';
		tabuleiro[2] = 'X';
		tabuleiro[4] = 'X';
	}

	public char[] getTabuleiro() {
		return tabuleiro;
	}

	public void setTabuleiro(char[] tabuleiro) {
		this.tabuleiro = tabuleiro;
	}

	// Verifica se a casa está ocupada ou não (com bola ou sem)
	public boolean verificaOcupacao(int casa) {
		if (getTabuleiro()[casa] == 'X')
			return true;
		return false;
	}

	// Metodo para auxiliar na alteração das casas.
	public void alteraCasa(int casa) {

		if (verificaOcupacao(casa))
			getTabuleiro()[casa] = '-';
		getTabuleiro()[casa] = 'X';
	}
}
