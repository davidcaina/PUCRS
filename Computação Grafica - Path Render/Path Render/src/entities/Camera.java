package entities;

import javax.swing.text.Position;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import terrains.Terreno;

public class Camera {
		
	
	private float distancia_chao = 50;
	private float angulo = 0;
	
	private Vector3f pos = new Vector3f(0,5,0);
	private float anguloY = 100;
	private float anguloX ;
	private float roll;
	
	private float velocidadeA;
	private float velocidadeB;
	private Terreno terreno;
	
	public Camera(Terreno terreno){
		this.terreno = terreno;
		this.velocidadeA = 0.1f;
		this.velocidadeB = 0.8f;
	}
	
	public void move(){
		
		calculaZoom();
		calculaPitch();
		calculaAngulo();

		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			pos.z -= (float)Math.cos(Math.toRadians(anguloX) * velocidadeA);
			pos.x += (float)Math.sin(Math.toRadians(anguloX) * velocidadeA);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			pos.z -= -(float)Math.cos(Math.toRadians(anguloX)) * velocidadeB;
			pos.x -= (float)Math.sin(Math.toRadians(anguloX)) * velocidadeB;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			pos.z += (float)Math.sin(Math.toRadians(anguloX)) * velocidadeB;
			pos.x += (float)Math.cos(Math.toRadians(anguloX)) * velocidadeB;
			//yaw += velocidade;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			pos.z -= (float)Math.sin(Math.toRadians(anguloX)) * velocidadeB;
			pos.x -= (float)Math.cos(Math.toRadians(anguloX)) * velocidadeB;
			//yaw -= velocidade;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_Z)){
			pos.y+=velocidadeB;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_X)){
			pos.y-=velocidadeB;
		}

	}
	public Vector3f getPosition() {
		return pos;
	}

	public float getPitch() {
		return anguloY;
	}

	public float getYaw() {
		return anguloX;
	}

	public float getRoll() {
		return roll;
	}
	
	
	private void calculaZoom(){		
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		pos.y -= zoomLevel;
	}
	
	public void cameraPos(){
		pos.y = distancia_chao;
		pos.x = (terreno.getSize()/2)+1;
	}
	
	private void calculaPitch(){
		if(Mouse.isButtonDown(0)){
			float pitchChange = Mouse.getDY()*0.1f;
			anguloY -= pitchChange;
		}
	}
	
	private void calculaAngulo(){
		if(Mouse.isButtonDown(1)){
			float anguloChange = Mouse.getDX() * 0.3f;
			anguloX += anguloChange;
		}
	}

	private float calculaHorizonte(){
		return (float) (distancia_chao * Math.cos(Math.toRadians(anguloY)));
	}
	
	private float calculaVertical(){
		return (float) (distancia_chao * Math.sin(Math.toRadians(anguloY)));
	}
	
}
