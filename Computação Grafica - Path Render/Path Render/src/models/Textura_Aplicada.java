package models;

import textures.Textura;

public class Textura_Aplicada {
	
	private VAO vao;
	private Textura textura;

	
	public Textura_Aplicada(VAO vao, Textura textura){
		this.vao = vao;
		this.textura = textura;
	}

	public VAO getRawModel() {
		return vao;
	}

	public Textura getTexture() {
		return textura;
	}

}
