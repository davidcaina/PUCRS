package ClassesJogo;

import java.util.Random;

public class Dado {

	// Lança dado ( numero 1 a 6).
	public int jogaDado() {
		Random r = new Random();
		return r.nextInt(7-1) + 1;
	}
}
