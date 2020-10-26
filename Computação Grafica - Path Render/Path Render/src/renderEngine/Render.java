package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Entidade;
import entities.Luz;
import models.Textura_Aplicada;
import shaders.Shader_Terreno;
import shaders.StaticShader;
import terrains.Terreno;

public class Render {

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;

	private Matrix4f projectionMatrix;

	private StaticShader shader = new StaticShader();
	private Render_Entidade render_entidade;

	private Render_Terreno render_terreno;
	private Shader_Terreno shader_terreno = new Shader_Terreno();

	private Map<Textura_Aplicada, List<Entidade>> entidades = new HashMap<Textura_Aplicada, List<Entidade>>();
	private List<Terreno> terrenos = new ArrayList<Terreno>();

	public Render() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		cria_Projecao();
		render_entidade = new Render_Entidade(shader, projectionMatrix);
		render_terreno = new Render_Terreno(shader_terreno, projectionMatrix);
	}

	public void render(Luz fonte, Camera camera) {
		prepare();
		shader.start();
		shader.loadLuz(fonte);
		shader.loadView(camera);
		render_entidade.render(entidades);
		shader.stop();
		shader_terreno.start();
		shader_terreno.loadLight(fonte);
		shader_terreno.loadViewMatrix(camera);
		render_terreno.render(terrenos);
		shader_terreno.stop();
		terrenos.clear();
		entidades.clear();
	}

	public void processTerrain(Terreno terrain) {
		terrenos.add(terrain);
	}

	public void processEntity(Entidade e) {
		Textura_Aplicada m = e.getObj();
		List<Entidade> batch = entidades.get(m);
		if (batch != null) {
			batch.add(e);
		} else {
			List<Entidade> newBatch = new ArrayList<Entidade>();
			newBatch.add(e);
			entidades.put(m, newBatch);
		}
	}

	public void clean() {
		shader.cleanUp();
		shader_terreno.cleanUp();
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.49f, 89f, 0.98f, 1);
	}

	private void cria_Projecao() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

}
