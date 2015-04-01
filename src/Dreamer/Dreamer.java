package Dreamer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Dreamer {
	
	static int numberOfCollisions = 0;
	static BufferedWriter logWriter = null;
	static Graphics g = new Graphics();
	static Marker origin = new Marker("Origin", 0, 0);

	public static void main(String[] argv) {		
		
		// setFullscreen();
		setResolution(800, 600);	
		
		try {
			play();
		} catch(SlickException e) {
			e.printStackTrace();
		}		
	}

	static void play() throws SlickException {	
		
		PerformanceMonitor updateMonitor = new PerformanceMonitor("update");
		PerformanceMonitor renderMonitor = new PerformanceMonitor("render");
		PerformanceMonitor.addMonitor(updateMonitor, renderMonitor);
		
		while (true) {
			Display.processMessages();
			
			updateMonitor.start();
			update();
			updateMonitor.stop();
			
			renderMonitor.start();
			render();
			renderMonitor.stop();
			
			PerformanceMonitor.printAll();
			
			// update screen
			Display.sync(70);
			Display.update(false);
			
			
			if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				Display.destroy();
				System.exit(0);
				try {
					logWriter.close();
				}
				catch(Exception e) {}
			}
		}
	}
	
	static void init() {
		
		try {
			//initialize GL and open window
			Display.create(new PixelFormat(2, 2, 0, 2));
			Display.setVSyncEnabled(true);

			glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
			// clear z buffer to 1
			glClearDepth(1);
			
			// enable alpha blending
			glEnable(GL_BLEND);
			// enables depth buffering to draw faces in the appropriate order
			glDepthFunc(GL_LESS);
			glCullFace(GL_FRONT);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			glViewport(0, 0, Constants.screenWidth, Constants.screenHeight);
			glMatrixMode(GL_MODELVIEW);
			
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			//NOTE this was the culprit for upside-down drawing
			glOrtho(0, Constants.screenWidth, Constants.screenHeight, 0, 1, -1);
			glMatrixMode(GL_MODELVIEW);
		}
		catch(Exception e) {
			e.printStackTrace(System.err);
			System.exit(0);
		}
		//init logger
		try {
			logWriter = new BufferedWriter(new FileWriter("log.txt"));
			logWriter.write("debugging log\r\n");
		}
		catch(Exception e) {e.printStackTrace();}
		
		try {
			Library.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		KeyHandler.init();
	    g.setFont(Library.defaultFont);
	    
	    new Ninja(0, 0).addToGame();
		new MainMenu();
	}
	
	static void update() {	
		
		numberOfCollisions = 0;
		Level.updateCurrent();
		KeyHandler.getKeys();
		if(Level.freezeCounter == 0); // to blank screen during level transitions
			Element.updateAll();
		Camera.update();
		Element.clearActive();
		Element.activateVisible();
	}
	
	static void render() {	
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		g.setWorldClip(new Rectangle(0, 0, Constants.screenWidth, Constants.screenHeight));
		if(Level.freezeCounter > 0) {
			Level.freezeCounter--;
		} else {
			Element.drawActive(g);
		}
	}
	
	static void displayInfo() {
		
		g.setColor(Library.defaultFontColor);
		g.drawString(
				Element.numberActive()+" ACTIVE ELEMENTS, "
				+Element.numberXRangeSets()+" SETS IN X, "
				+Element.numberYRangeSets()+" SETS IN Y",
				20, 
				20
		);
		g.drawString(Element.numberTotal()+" TOTAL ELEMENTS", 20, 40);
		g.drawString(numberOfCollisions+" COLLISIONS CHECKED", 20, 60);
		Camera.draw(g);
	}
	
	static void displayMem() {
		
		g.setColor(Library.defaultFontColor);
		Runtime runtime = Runtime.getRuntime();
		NumberFormat format = NumberFormat.getInstance();  
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();
	    g.drawString("free memory: " + format.format(freeMemory / 1024), 20, 80);
	    g.drawString("allocated memory: " + format.format(allocatedMemory / 1024), 20, 100);
	    g.drawString("max memory: " + format.format(maxMemory / 1024),20, 120);
	    g.drawString("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024), 20, 140);
	}
	
	static void setResolution(int x, int y) { 
	
		try {
			Display.setDisplayMode(new DisplayMode(x, y));
			Display.destroy();
			setScreenConstants();
			init();
		} catch (LWJGLException e) {
			setFullscreen();
		}	
	}
	
	static void setFullscreen() {
		
		try {
			Display.setFullscreen(true);
			Display.destroy();
			setScreenConstants();
			init();
		} catch (LWJGLException e1) {
			System.err.println("DISPLAY MODE UNAVAILABLE");
			e1.printStackTrace();
		}
	}
	
	static void setScreenConstants() {
		
		DisplayMode dm = Display.getDisplayMode();
		Constants.screenWidth = dm.getWidth();
		Constants.screenHeight = dm.getHeight();
	}
}