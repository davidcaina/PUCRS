package entities;


import org.lwjgl.util.vector.Vector3f;

import models.Textura_Aplicada;

public class Entidade {

	private Textura_Aplicada obj;
	private Vector3f pos;
	private float rotacao_X, rotacao_Y, rotacao_Z;
	private float escala;

	public Entidade(Textura_Aplicada obj, Vector3f pos, float rotacao_X, float rotacao_Y, float rotacao_Z,
			float escala) {
		this.obj = obj;
		this.pos = pos;
		this.rotacao_X = rotacao_X;
		this.rotacao_Y = rotacao_Y;
		this.rotacao_Z = rotacao_Z;
		this.escala = escala;
	}

	public void incrementa_Pos(float ix, float iy, float iz) {
		this.pos.x += ix;
		this.pos.y += iy;
		this.pos.z += iz;
	}

	public void incrementa_Rot(float ix, float iy, float iz) {
		this.rotacao_X += ix;
		this.rotacao_Y += iy;
		this.rotacao_Z += iz;
	}

	public Textura_Aplicada getObj() {
		return obj;
	}

	public void setObj(Textura_Aplicada obj) {
		this.obj = obj;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public float getRotacao_X() {
		return rotacao_X;
	}

	public void setRotacao_X(float rotacao_X) {
		this.rotacao_X = rotacao_X;
	}

	public float getRotacao_Y() {
		return rotacao_Y;
	}

	public void setRotacao_Y(float rotacao_Y) {
		this.rotacao_Y = rotacao_Y;
	}

	public float getRotacao_Z() {
		return rotacao_Z;
	}

	public void setRotacao_Z(float rotacao_Z) {
		this.rotacao_Z = rotacao_Z;
	}

	public float getEscala() {
		return escala;
	}

	public void setEscala(float escala) {
		this.escala = escala;
	}


}
