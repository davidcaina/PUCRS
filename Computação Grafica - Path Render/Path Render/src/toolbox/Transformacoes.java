package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Transformacoes {

	public static Matrix4f cria_matriz_transformacao(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f cria_matriz_transformacao(Vector3f translacao, float rotacao_X, float rotacao_Y,
			float rotacao_Z, float escala) {
		Matrix4f matriz = new Matrix4f();
		matriz.setIdentity();
		Matrix4f.translate(translacao, matriz, matriz);
		Matrix4f.rotate((float) Math.toRadians(rotacao_X), new Vector3f(1,0,0), matriz, matriz);
		Matrix4f.rotate((float) Math.toRadians(rotacao_Y), new Vector3f(0,1,0), matriz, matriz);
		Matrix4f.rotate((float) Math.toRadians(rotacao_Z), new Vector3f(0,0,1), matriz, matriz);
		Matrix4f.scale(new Vector3f(escala,escala,escala), matriz, matriz);
		return matriz;
	}
	
	public static Matrix4f cria_view(Camera camera) {
		Matrix4f view = new Matrix4f();
		view.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), view,
				view);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), view, view);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, view, view);
		return view;
	}

}
