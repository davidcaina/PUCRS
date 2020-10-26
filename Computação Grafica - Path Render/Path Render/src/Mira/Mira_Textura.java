package Mira;

import org.lwjgl.util.vector.Vector2f;

public class Mira_Textura {
	
	private int textura;
	private Vector2f pos;
	private Vector2f escala;
	
	public Mira_Textura(int textura, Vector2f pos, Vector2f escala) {
		this.textura = textura;
		this.pos = pos;
		this.escala = escala;
	}

	public int getTextura() {
		return textura;
	}

	public Vector2f getPos() {
		return pos;
	}

	public Vector2f getEscala() {
		return escala;
	}
	
	

}
