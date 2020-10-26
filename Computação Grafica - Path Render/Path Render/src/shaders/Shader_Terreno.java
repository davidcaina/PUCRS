package shaders;

import org.lwjgl.util.vector.Matrix4f;

import toolbox.Transformacoes;
import entities.Camera;
import entities.Luz;

public class Shader_Terreno extends Shader {

	private static final String v_Path = "src/shaders/terrainVertexShader.txt";
	private static final String f_Path = "src/shaders/terrainFragmentShader.txt";

	private int matriz_transformacao;
	private int projecao;
	private int view;
	private int luz_pos;
	private int luz_cor;
	private int brilho;
	private int reflexo;

	public Shader_Terreno() {
		super(v_Path, f_Path);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		matriz_transformacao = super.getUniformLocation("transformationMatrix");
		projecao = super.getUniformLocation("projectionMatrix");
		view = super.getUniformLocation("viewMatrix");
		luz_pos = super.getUniformLocation("lightPosition");
		luz_cor = super.getUniformLocation("lightColour");
		brilho = super.getUniformLocation("shineDamper");
		reflexo = super.getUniformLocation("reflectivity");

	}

	public void loadBrilho(float d, float r) {
		super.loadFloat(brilho, d);
		super.loadFloat(reflexo, r);
	}

	public void loadMatriz_Transformacao(Matrix4f m) {
		super.loadMatrix(matriz_transformacao, m);
	}

	public void loadLight(Luz l) {
		super.loadVector(luz_pos, l.getPos());
		super.loadVector(luz_cor, l.getCor());
	}

	public void loadViewMatrix(Camera c) {
		Matrix4f v = Transformacoes.cria_view(c);
		super.loadMatrix(view, v);
	}

	public void loadProjectionMatrix(Matrix4f p) {
		super.loadMatrix(projecao, p);
	}

}
