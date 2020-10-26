package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entidade;
import models.Textura_Aplicada;
import models.VAO;
import shaders.StaticShader;
import textures.Textura;
import toolbox.Transformacoes;

public class Render_Entidade {

	private StaticShader shader;

	public Render_Entidade(StaticShader shader,Matrix4f projecao) {
		this.shader = shader;
		shader.start();
		shader.loadProjecao(projecao);
		shader.stop();
	}

	public void render(Map<Textura_Aplicada, List<Entidade>> entidade) {

		for (Textura_Aplicada m : entidade.keySet()) {
			prepara_Textura_Aplicada(m);

			List<Entidade> list_e = entidade.get(m);

			for (Entidade e : list_e) {
				prepareInstance(e);
				GL11.glDrawElements(GL11.GL_TRIANGLES, m.getRawModel().getVertices(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	private void prepara_Textura_Aplicada(Textura_Aplicada t) {
		VAO rawModel = t.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Textura texture = t.getTexture();
		shader.loadBrilho(texture.getBrilho(), texture.getReflexo());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, t.getTexture().getID());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entidade e) {
		Matrix4f matriz_transformacao = Transformacoes.cria_matriz_transformacao(e.getPos(), e.getRotacao_X(),
				e.getRotacao_Y(), e.getRotacao_Z(), e.getEscala());
		shader.loadMatriz_Transformacao(matriz_transformacao);
	}

}
