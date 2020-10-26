package entities;

import org.lwjgl.util.vector.Vector3f;

public class Luz {

	private Vector3f pos;
	private Vector3f cor;

	public Luz(Vector3f pos, Vector3f cor) {
		this.pos = pos;
		this.cor = cor;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getCor() {
		return cor;
	}

	public void setCor(Vector3f cor) {
		this.cor = cor;
	}

}
