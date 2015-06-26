package Dreamer;

import io.Serial;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.SlickException;

public class Dreamer {

	public static void main(String[] argv) {		

		/*
		io.Test t = new io.Test();
		t.start();
		*/
		
		Serial.begin(115200);
		
		boolean b = true;
		while(b == true) {
			Serial.write((byte)'g');
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Serial.write((byte)'s');
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		
		setFullscreen();
		// setResolution(800, 600);	
		
		try {
			play();
		} catch(SlickException e) {
			e.printStackTrace();
		}
	}
	
	static void play() throws SlickException {	
		
		PerformanceMonitor.getGlobal().start();
		
		while (true) {
			
			PerformanceMonitor.getGlobal().stop();
			PerformanceMonitor.getGlobal().start();
			
			Display.processMessages();

			update();

			render();

			// update screen
			Display.sync(70);
			Display.update(false);			
			
			if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				Manager.performance.log();
				Display.destroy();
				try {
					PerformanceMonitor.logWriter.close();
				}
				catch(Exception e) {}
				System.exit(0);
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
		
		Library.load();
		Keys.init();
	    Drawer.graphics.setFont(Library.defaultFont);
	    

	    new Ninja(0, 0);
		new MainMenu();
	}
	
	static void update() {	
	
		PerformanceMonitor.numberOfCollisions = 0;
		Level.updateCurrent();
		Keys.getKeys();
		if(Level.freezeCounter == 0); // to blank screen during level transitions
			Manager.updateAll();
		Camera.update();
		Manager.clearActive();
		Manager.activateVisible();
	}
	
	static void render() {	
		
		OpenGL.clearBuffers();
		Drawer.setWorldClip(0, 0, Constants.screenWidth, Constants.screenHeight);
		
		if(Level.freezeCounter > 0) {
			Level.freezeCounter--;
		} else {
			Manager.drawActive();
		}
		
		if(Manager.debug) { PerformanceMonitor.displayInfo(); }
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