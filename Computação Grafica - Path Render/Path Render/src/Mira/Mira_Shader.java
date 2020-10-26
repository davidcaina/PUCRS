package Mira;

import org.lwjgl.util.vector.Matrix4f;

import shaders.Shader;
import shaders.StaticShader;

public class Mira_Shader extends Shader {

    private static final String VERTEX_FILE = "src/Mira/Mira_VertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Mira/Mira_FragmentShader.txt";
     
    private int matrix_transformacao;
 
    public Mira_Shader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(matrix_transformacao, matrix);
    }
 
    @Override
    protected void getAllUniformLocations() {
    	matrix_transformacao = super.getUniformLocation("matrix_transformacao");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "pos");
    }
     
}
