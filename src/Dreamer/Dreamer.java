package Dreamer;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.SlickException;

public class Dreamer {

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
		PerformanceMonitor otherMonitor = new PerformanceMonitor("other");
		PerformanceMonitor.addMonitor(updateMonitor, renderMonitor, otherMonitor);
		
		while (true) {
			Display.processMessages();
			otherMonitor.stop();
			
			updateMonitor.start();
			update();
			updateMonitor.stop();
			
			renderMonitor.start();
			render();
			renderMonitor.stop();
			
			otherMonitor.start();
			PerformanceMonitor.printAll();
			
			// update screen
			Display.sync(70);
			Display.update(false);
			
			
			if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				Display.destroy();
				System.exit(0);
				try {
					PerformanceMonitor.logWriter.close();
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
			OpenGL.init();
		}
		catch(Exception e) {
			e.printStackTrace(System.err);
			System.exit(0);
		}
		
		try {
			Library.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		KeyHandler.init();
	    Drawer.graphics.setFont(Library.defaultFont);
	    
	    new Ninja(0, 0).addToGame();
		new MainMenu();
	}
	
	static void update() {	
		
		PerformanceMonitor.numberOfCollisions = 0;
		Level.updateCurrent();
		KeyHandler.getKeys();
		if(Level.freezeCounter == 0); // to blank screen during level transitions
			Element.updateAll();
		Camera.update();
		Element.clearActive();
		Element.activateVisible();
	}
	
	static void render() {	
		
		OpenGL.clearBuffers();
		Drawer.setWorldClip(0, 0, Constants.screenWidth, Constants.screenHeight);
		
		if(Level.freezeCounter > 0) {
			Level.freezeCounter--;
		} else {
			Element.drawActive();
		}
	}
	
	static void setResolution(int x, int y) { 
	
		try {
			Display.setDisplayMode(new DisplayMode(x, y));
			Display.destroy();
			setScreenConstants();
			init();
		} catch (LWJGLException e) {
			setFullscreen();
			e.printStackTrace();
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