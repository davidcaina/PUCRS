package textures;

public class Textura {

	private int texturaID;

	private float brilho = 1;
	private float reflexo = 0;

	public Textura(int texture) {
		this.texturaID = texture;
	}

	public int getID() {
		return texturaID;
	}

	public float getBrilho() {
		return brilho;
	}

	public void setBrilho(float shineDamper) {
		this.brilho = shineDamper;
	}

	public float getReflexo() {
		return reflexo;
	}

	public void setReflexo(float reflectivity) {
		this.reflexo = reflectivity;
	}

}
