package terrains;

import models.VAO;
import renderEngine.Loader;
import textures.Textura;

public class Terreno {

	private static final float SIZE = 300;
	private static final int VERTICES_C = 128;

	private float x;
	private float z;
	private VAO vao;
	private Textura textura;

	public Terreno(int gridX, int gridZ, Loader loader, Textura textura) {
		this.textura = textura;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.vao = gera_Terreno(loader);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public VAO getVAO() {
		return vao;
	}

	public Textura getTexture() {
		return textura;
	}
	
	public float getSize(){
		return SIZE;
	}

	private VAO gera_Terreno(Loader loader) {
		int count = VERTICES_C * VERTICES_C;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		
		int[] indices = new int[6 * (VERTICES_C - 1) * (VERTICES_C - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTICES_C; i++) {
			for (int j = 0; j < VERTICES_C; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTICES_C - 1) * SIZE;
				vertices[vertexPointer * 3 + 1] = 0;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTICES_C - 1) * SIZE;
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTICES_C - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTICES_C - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTICES_C - 1; gz++) {
			for (int gx = 0; gx < VERTICES_C - 1; gx++) {
				int topLeft = (gz * VERTICES_C) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTICES_C) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.load_pro_VAO(vertices, textureCoords, normals, indices);
	}

}
