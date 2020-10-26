package engineTester;

public class Coordenadas {
	private int coordX;
	private int coordY;
	private int time;

	public int getCoordX() {
		return coordX;
	}

	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}

	public int getCoordY() {
		return coordY;
	}

	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Coordenadas [coordX=" + coordX + ", coordY=" + coordY + ", time=" + time + "]";
	}

}