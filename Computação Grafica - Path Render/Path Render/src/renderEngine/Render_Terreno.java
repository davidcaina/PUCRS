package renderEngine;

import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.VAO;
import shaders.Shader_Terreno;
import terrains.Terreno;
import textures.Textura;
import toolbox.Transformacoes;

public class Render_Terreno {

	private Shader_Terreno shader;

	public Render_Terreno(Shader_Terreno shader, Matrix4f projecao) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projecao);
		shader.stop();
	}

	public void render(List<Terreno> t) {
		for (Terreno aux : t) {
			prepara_Terreno(aux);
			load_Matriz(aux);
			GL11.glDrawElements(GL11.GL_TRIANGLES, aux.getVAO().getVertices(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}

	private void prepara_Terreno(Terreno t) {
		VAO vao = t.getVAO();
		GL30.glBindVertexArray(vao.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Textura texture = t.getTexture();
		shader.loadBrilho(texture.getBrilho(), texture.getReflexo());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void load_Matriz(Terreno t) {
		Matrix4f m = Transformacoes.cria_matriz_transformacao(new Vector3f(t.getX(), 0, t.getZ()), 0, 0, 0, 1);
		shader.loadMatriz_Transformacao(m);
	}

}
