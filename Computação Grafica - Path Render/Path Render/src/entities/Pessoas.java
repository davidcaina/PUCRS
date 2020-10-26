package entities;

import org.lwjgl.util.vector.Vector3f;

import models.Textura_Aplicada;

public class Pessoas extends Entidade {

	private static final float velocidade = 20;
	private static float turn = 160;
	
	public Pessoas(Textura_Aplicada obj, Vector3f pos, float rotacao_X, float rotacao_Y, float rotacao_Z,
			float escala) {
		super(obj, pos, rotacao_X, rotacao_Y, rotacao_Z, escala);

	}
	
	public void move(){}

	private void checkInputs(){
		
	}
	
	
}
