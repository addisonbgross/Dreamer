package Dreamer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.SlickException;

import serial.RX;

public class Dreamer {

	public static void main(String[] argv) {		

		RX.go();
		
		// setFullscreen();
		setResolution(800, 600);	
		
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
	
	public static void testSerial() {
	    	
		    new Sun().add();
		    new SolidBackground(new org.newdawn.slick.Color(0, 0.9f, 0.5f)).add();
		    
		    Shape3d s = new Shape3d();
		    s.addVertex(-200, 0, 0);
		    s.addVertex(0, 200, 0);
		    s.addVertex(200, 0, 0);
		    Face f = new Face();
		    f.setVertices(0, 1, 2);
		    f.setColor(org.newdawn.slick.Color.cyan);
		    f.triangulate();
		    s.addFace(f);
		    s.add();
		    
		    Text m1 = new Text("WORKY?", 0, 100);
		m1.add();
		Text m2 = new Text("NOPE", 0, 150);
		m2.add();
		
		interfaces.Updateable u = new interfaces.Updateable() {
		
			@Override
			public void update() {
				
				try {
					
					RX.tryNextInt();
					Integer a = RX.serialData.a / 10;
					Integer b = RX.serialData.b / 10;
					s.vertices.get(0).set(-a, 0, 0);
					s.vertices.get(2).set(b, 0, 0);
					m1.name = "Ultrasound 1: " + a.toString();
					m2.name = "Ultrasound 2: " + b.toString();
					
				} catch(Exception e) {
					
				}
			}
		};
		
		Manager.add(u);
	}
}