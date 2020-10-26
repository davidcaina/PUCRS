package Teste;

import java.time.LocalDateTime;
import java.util.Calendar;

import ClassesJogo.Jogador;

public class Teste {

	public static void main(String[] args) throws InterruptedException {

		long tempo = System.currentTimeMillis();
		
		System.out.println("Tempo atual: " + tempo);
		
		Thread.sleep(10000); // 10 seg
		
		long tempo2 = System.currentTimeMillis();
		
		System.out.println("Tempo depois: " + tempo2);
		
		System.out.println(tempo - tempo2);
		
		if( Math.abs((tempo - tempo2)) > 10000)
			System.out.println("passou 10 seg");
		
	}
}
