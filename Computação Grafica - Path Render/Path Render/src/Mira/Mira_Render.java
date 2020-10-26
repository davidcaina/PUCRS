package Mira;

import java.awt.List;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.VAO;
import renderEngine.Loader;
import toolbox.Transformacoes;

public class Mira_Render {

	private final VAO quad;
	private Mira_Shader shader;
	
	public Mira_Render(Loader loader){
		float[] pos = { -1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.load_pro_VAO_Mira(pos);
		shader = new Mira_Shader();
	}
	
	public void render(ArrayList<Mira_Textura> mira){
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // internet - libera alpha blend, ou seja, deixa a mira transparente
		for(Mira_Textura m : mira){
			GL13.glActiveTexture(GL13.GL_TEXTURE0); // banco defaut
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getTextura());
			Matrix4f matriz = Transformacoes.cria_matriz_transformacao(m.getPos(), m.getEscala());
			shader.loadTransformation(matriz);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertices());
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void clean(){
		shader.cleanUp();
	}
	
}
