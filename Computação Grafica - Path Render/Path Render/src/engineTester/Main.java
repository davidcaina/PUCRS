package engineTester;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Mira.Mira_Render;
import Mira.Mira_Textura;
import renderEngine.Janela;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Render;
import terrains.Terreno;
import textures.Textura;
import entities.Camera;
import entities.Entidade;
import entities.Luz;
import entities.Pessoas;
import models.Textura_Aplicada;
import models.VAO;

public class Main {

	Graphics2D g;
	private static final char SEPARATOR_VALUE = ' ';
	private static final String SEPARATOR_COORDENATES = ",";
	private static final String FIRST_PARENTESES_SPLIT = "\\(";
	private static final String LAST_PARENTESES = ")";

	class Pessoa {

		private int pixels;

		private List<Coordenadas> listaCoordenadas;

		public int getPixels() {
			return pixels;
		}

		public void setPixels(int pixels) {
			this.pixels = pixels;
		}

		public List<Coordenadas> getListaCoordenadas() {
			return listaCoordenadas;
		}

		public void setListaCoordenadas(List<Coordenadas> listaCoordenadas) {
			this.listaCoordenadas = listaCoordenadas;
		}

	}

	private static int readValue(char[] line) {
		StringBuilder value = new StringBuilder();
		for (char digit : line) {
			if (digit == SEPARATOR_VALUE) {
				return Integer.valueOf(value.toString());
			}
			value.append(digit);
		}
		return 0;
	}

	private static int readCoordinates(List<String> coordinatesGroup, List<Coordenadas> coordResultList) {
		for (String values : coordinatesGroup) {
			String[] cordinateGroup = values.split(SEPARATOR_COORDENATES);
			Coordenadas cord = new Coordenadas();
			for (int i = 0; i < cordinateGroup.length; i++) {
				if (i == 0) {
					cord.setCoordX(Integer.valueOf(cordinateGroup[i]));
				} else if (i == 1) {
					cord.setCoordY(Integer.valueOf(cordinateGroup[i]));
				} else if (i == 2) {
					String aux = cordinateGroup[i];
					if (aux.contains(LAST_PARENTESES)) {
						aux = aux.replace(LAST_PARENTESES, "");
					}
					cord.setTime(Integer.valueOf(aux));
				}
			}
			coordResultList.add(cord);
		}
		return 0;
	}
	
	public static void main(String[] args) {

		 String nome = "src/Paths_D.txt";
			List<List<Coordenadas>> coordinates = new ArrayList<List<Coordenadas>>();
			String[] coordenadasAtual;
			String aux;
			
		try {
			FileReader arq = new FileReader(nome);
			BufferedReader lerArq = new BufferedReader(arq);
			String linha = lerArq.readLine();

			String r = linha;
			String metros_pixel = r.replaceAll("[^\\d,]", "");
			System.out.println(metros_pixel);

			linha = lerArq.readLine(); // ignora primeira linha
			Integer value = 0;


			while (linha != null) {
				List<Coordenadas> listaAux = new ArrayList<Coordenadas>();
				coordenadasAtual = linha.split(" ");
				aux = coordenadasAtual[0];
				value = readValue(linha.toCharArray());
				List<String> listaCoordenadas = new ArrayList<String>(
				Arrays.asList(linha.split(FIRST_PARENTESES_SPLIT)));
				listaCoordenadas.remove(0);
				readCoordinates(listaCoordenadas, listaAux);
				
				//System.out.println(listaAux); // imprime lista de coordenadas
				linha = lerArq.readLine();    // le da segunda até a ultima
				// ----------------------------------------------------------

				coordinates.add(listaAux);
		}
			
			}catch (IOException e){	System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());}
				
		// ***************************************************************************************************************
		
		
		
		
		// Cria o display do jogo
		Janela.cria_Janela();

		// Cria um objeto para carregar
		Loader loader = new Loader();
		
		// Cria um novo VAO com um obj como parametro
		VAO model = OBJLoader.loadObjModel("person", loader);
		
		// Cria uma nova textura
		Textura_Aplicada staticModel = new Textura_Aplicada(model, new Textura(loader.loadTextura("white")));
		
		// Cria um novo terreno
		Terreno terrain = new Terreno(0, -1, loader, new Textura(loader.loadTextura("grass")));
		
		// Cria um objeto para renderizar
		Render renderer = new Render();

		//  Cria a lista de pessoas que seram atiradas no jogo
		List<Entidade> entidades_Pessoas = new ArrayList<Entidade>();
		List<Pessoas> e = new ArrayList<Pessoas>();
		
		// Le as coordenadas e "normaliza" para o novo terreno --  AINDA NÃO FUNCIONA
		for (List<Coordenadas> a : coordinates) {
			
			float new_cX = (float) Math.floor(((float) a.get(0).getCoordX() / 1000) * terrain.getSize());
			float new_cZ = (float) Math.floor(((float) a.get(0).getCoordY() / 1000) * terrain.getSize());

			System.out.println(" (" + a.get(0).getCoordX() + " / 1000) * 500 = " + new_cX );
			e.add(new Pessoas(staticModel, new Vector3f(new_cZ, 0, new_cX), 0, 0, 0, 1)); 			
		}
		 

		// cria uma luz com intensidade
		Luz light = new Luz(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));


		// Cria uma camera na posicao atual do terreno passado por parametro
		Camera camera = new Camera(terrain);
		camera.cameraPos();


		//Cria uma pessoa na posicao (x,y,z)
		//Pessoas p = new Pessoas(staticModel, new Vector3f(0, 0, -1), 0, 0, 0, 1);

		
		// Carrega a mira no meio da tela (0,0), em um quadrado de (0.05f,0.05f)
		ArrayList<Mira_Textura> mira = new ArrayList<Mira_Textura>();
		Mira_Textura m = new Mira_Textura(loader.loadTextura("mira"), new Vector2f (0.0f, 0.0f), new Vector2f(0.15f, 0.15f));
		mira.add(m);
		
		Mira_Render mira_render = new Mira_Render(loader);
		
		// Main loop do game
		while (!Display.isCloseRequested()) {
			camera.move();
			//renderer.processEntity(p);
			renderer.processTerrain(terrain);

			
			for (Pessoas aux_e : e) { renderer.processEntity(aux_e); }
			
			renderer.render(light, camera);
			mira_render.render(mira);
			Janela.atualiza_Janela();
			
		}
		mira_render.clean();
		renderer.clean();
		loader.clean();
		Janela.fecha_Janela();

	}
}