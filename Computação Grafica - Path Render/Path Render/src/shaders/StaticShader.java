package shaders;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Luz;
import toolbox.Transformacoes;

public class StaticShader extends Shader {

	private static final String v_Path = "src/shaders/vertexShader.txt";
	private static final String f_Path = "src/shaders/fragmentShader.txt";

	private int matriz_transformacao;
	private int projecao;
	private int view;
	private int luz_pos;
	private int luz_cor;
	private int brilho;
	private int reflexo;

	public StaticShader() {
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

	public void loadBrilho(float b, float f) {
		super.loadFloat(brilho, b);
		super.loadFloat(reflexo, f);
	}

	public void loadMatriz_Transformacao(Matrix4f m) {
		super.loadMatrix(matriz_transformacao, m);
	}

	public void loadLuz(Luz l) {
		super.loadVector(luz_pos, l.getPos());
		super.loadVector(luz_cor, l.getCor());
	}

	public void loadView(Camera camera) {
		Matrix4f v = Transformacoes.cria_view(camera);
		super.loadMatrix(view, v);
	}

	public void loadProjecao(Matrix4f p) {
		super.loadMatrix(projecao, p);
	}
		

}
