package Dreamer;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

//import Dreamer.runnableExamples.ShaderProgram;

public class Dreamer {
	//for testing
	static boolean sampled = true;
	static int measuredDelta = 0;
	static int numberOfCollisions = 0;
	static int numberOfUpdates = 0;
	//various printing things
	static BufferedWriter logWriter = null;
	//the meat
	static Player player;
	static Player player2;
	static Graphics g = new Graphics();
	static Marker origin = new Marker("Origin", 0, 0);

	public static void main(String[] argv) 
	{		
		init();	
		Library.load();

		new ZoomKeys().add();
		new FunctionKeys().add();
		
		player = new Ninja(-500, 0);
		new WASDKeys(player).add();
		player.addToGame();
		
		System.out.println(java.lang.Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MBees");
		
		origin.add();
		new MainMenu();
		
		try {
			play();
		} catch(SlickException e) {
			e.printStackTrace();
		}
	}
	
	static void init() {
		try {
			//initialize GL and open window
			//Display.setDisplayMode(new DisplayMode(Constants.screenWidth,Constants.screenHeight));
			//to set fullscreen mode uncomment the following line and comment the preceding one
			//Display.setFullscreen(true);
			DisplayMode dm = Display.getDisplayMode();
			Constants.screenWidth = dm.getWidth();
			Constants.screenHeight = dm.getHeight();
			Display.create();
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
			
			// Attemped OBJ loading
			int objectDisplayList = glGenLists(1);
			glNewList(objectDisplayList, GL_COMPILE); 
			{
				Model m = null;
				try {
					m = ObjLoader.loadModel(new File("res/house.obj"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Display.destroy();
					System.exit(1);
				} catch (IOException e) {
					e.printStackTrace();
					Display.destroy();
					System.exit(1);
				}
				glBegin(GL_TRIANGLES);
					for (ModelFace face : m.faces) {
						Vector3f n1 = m.normals.get((int) face.normal.x - 1);
						glNormal3f(n1.x, n1.y, n1.z);
						Vector3f v1 = m.vertices.get((int) face.vertex.x - 1);
						glVertex3f(v1.x, v1.y, v1.z);
						
						Vector3f n2 = m.normals.get((int) face.normal.y - 1);
						glNormal3f(n2.x, n2.y, n2.z);
						Vector3f v2 = m.vertices.get((int) face.vertex.y - 1);
						glVertex3f(v2.x, v2.y, v2.z);
						
						Vector3f n3 = m.normals.get((int) face.normal.z - 1);
						glNormal3f(n3.x, n3.y, n3.z);
						Vector3f v3 = m.vertices.get((int) face.vertex.z - 1);
						glVertex3f(v3.x, v3.y, v3.z);
					}
				glEnd();
			}
			glEndList();
			
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
		
	    g.setFont(Library.defaultFont);
	}
	static void play() throws SlickException
	{	
		// main game loop
		while (true)
		{
			Display.processMessages();
			update();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			render();
			// update screen
			Display.sync(60);
			Display.update(false);
			
			if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			{
				Display.destroy();
				System.exit(0);
				try {
					logWriter.close();
				}
				catch(Exception e) {}
			}
		}
	}
	static void update() {	
		//writes to log only for the first 200 updates
		numberOfUpdates++;
		try {
			if(numberOfUpdates < 200) {
				logWriter.write(player.toString()+"\r\n");
				logWriter.write(player.getStatus()+"\r\n");
				for (Element e: Element.activeSet)
					logWriter.write(e.getClass() + "  ");
				logWriter.write("\n");
			}
			else
				logWriter.close();
		}
		catch(Exception e) {e.printStackTrace();}
		
		numberOfCollisions = 0;
		
		Level.updateCurrent(); //keyhandling
		
		//this counter is to blank the screen during level switches
		if(Level.freezeCounter == 0);
			Element.updateAll();
		Camera.update();
		
		//activeSet must be cleared each update
		Element.clearActive();
		//all elements to be drawn should be active by the end of this function
		Element.activateVisible();
	}
	static void render() {	
		//ShaderProgram shader = new ShaderProgram();
		 
		// do the heavy lifting of loading, compiling and linking
		// the two shaders into a usable shader program
		//shader.init("src/Dreamer/runnableExamples/simple.vertex", "src/Dreamer/runnableExamples/simple.fragment");		
		//TODO pass translation and perspective off to graphics card using shaders
		//GL20.glUseProgram(shader.getProgramId());
		
		g.setWorldClip(new Rectangle(0, 0, Constants.screenWidth, Constants.screenHeight));
		if(Level.freezeCounter > 0) {
			Level.freezeCounter--;
		} else {
			Light.lightActive();
			Element.drawActive(g);
		}
		
		//TODO seperate this function measurement timer loop out somewhere
		if(Element.debug)
		{
			g.setFont(Library.defaultFont);
			displayInfo();
			displayMem();
			if(!sampled) {
				//this whole bit runs a function MEASURES*SAMPLES times and computes how long
				//it takes in nanoseconds for the function to complete
				measuredDelta = 0;
				final int MEASURES = 10;
				final int SAMPLES = 100;
				long[] timeList = new long[MEASURES];;
				long[] delta = new long[MEASURES - 1];
				timeList[0] = Sys.getTime(); 
				//taking MEASURES measurement consisting of SAMPLES runs of the function
				for(int i = 1; i < MEASURES; i++) {
					for(int j = 0; j < SAMPLES; j++)
						
						//function to measure goes in here
						//this will execute MEASURES*SAMPLES times when you press enter!
						
						//new Block("brick", i - 1000, j).add();
						
					delta[i - 1] = ((timeList[i] = Sys.getTime() * 1000 / Sys.getTimerResolution()) - timeList[i - 1]);
				}
				//computing the average
				for(int i = 1; i < MEASURES - 1; i++)
					measuredDelta += delta[i];
				measuredDelta *= (1000 / SAMPLES);
				measuredDelta /= (MEASURES - 1);
				sampled = true;
			}
			displayTestTime();
		}
		else
			sampled = false;
	}
	static void displayInfo() {
		g.setColor(Library.defaultFontColor);
		//g.drawString("RUN MODE", 650, 20);
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
	static void displayTestTime() {
		g.setColor(Library.defaultFontColor);
	    g.drawString("delta: "+measuredDelta+" us(?)" , 20, 160);
	}
}