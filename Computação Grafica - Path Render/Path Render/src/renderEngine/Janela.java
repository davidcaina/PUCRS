package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class Janela {

	private static final int LARGURA = 1280;
	private static final int ALTURA = 720;
	private static final int FPS = 120;

	private static long ultimoFrame;
	private static float delta;

	public static void cria_Janela(){		
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(LARGURA,ALTURA));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("Bolinhas");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0,0, LARGURA, ALTURA);
	}

	public static void atualiza_Janela() {

		Display.sync(FPS);
		Display.update();
		long atual = getTime();
		delta = (atual - ultimoFrame) / 1000f;

	}

	public static void fecha_Janela() {

		Display.destroy();

	}

	public static float getFrameTime() {
		return delta;
	}

	private static long getTime() {

		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

}
