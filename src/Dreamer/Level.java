package Dreamer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/* Level is the base class for all games states, including menus, editors, debuggers etc
 * 
 * on a call to pauseCurrent, the current level saves it's state to it's own memory as a
 * SavedState object.
 * 
 * when calling a level's resume it will immediately instantiate itself
 * 
 * see class TestLevel for a simple example of a Level
 * 
 * TODO improve the way players are handled
 * Currently player states are shared between Levels, ideally each Level would
 * restore the players from a list and add keyhandlers
 */
abstract class Level {
	Enemy e; //Generic enemy pointer
	boolean restored = false;
	static HashMap<String, SavedState> paused = new HashMap<String, SavedState>();
	static Level current;
	static ArrayList<KeyHandler> keys = new ArrayList<KeyHandler>();
	static boolean frozen = false;
	static int freezeCounter = 0;
	
	Level() {
		freezeCounter = 2;
		try {
			pauseCurrent();
		} catch(NullPointerException n) {
			//current level does not exist yet, no big deal
		}
		current = this;
		Element.clearAll();
	}
	
	static void pauseCurrent() {
		paused.put(current.getClass().toString(), new SavedState(current.getClass().toString()));
	}
	void resume() {
		try {
			paused.get(getClass().toString()).restore();
			restored = true;
		} catch(NullPointerException e) {
			//no level to restore yet
			restored = false;
		}
	}
	//TODO move this function and ArrayList Keys into Keyhandler.java
	static void updateCurrent() {
		for(KeyHandler k: keys)
			k.getKeys();
	}
}

class TestLevel extends Level {
	
	TestLevel() {
		super();
		if(!restored) {			
			Background b = new Background();
			b.add();
			new Sun().add();
			int HBLOCK = 50, VBLOCK = 50;
			for(int i = 0; i < VBLOCK; i++)
				for(int j = 0; j < HBLOCK; j++)
					new Block3d(Color.blue, i * 100, j * 100, 0, 50, 50, 50).add();
			Camera.focus(new ClassFocus(Block3d.class));
		}
	}
}
class SimpleLevel extends Level {
	
	SimpleLevel() {
		super();
		if(!restored) {
			//new Sun().add();
			new Lamp(2000, -500, 0, 2000).add();
			new Lamp(0, -500, 0, 2000).add();
			new Lamp(-2000, -700, 0, 2000).add();
			Player p = Player.getFirst();
			new Lamp(p).add();
			new Katana(p).add();
			
			p.setCenterBottom(0, 1);
			p.add();
	
			MeshMaker.makeMesh(Library.getImage("maps/flat_elevation"), Library.getImage("maps/flat_colour"), true, Library.getImage("maps/flat_colour").getWidth(), -300, 100);
			MeshMaker.makeMesh(Library.getImage("maps/flat_elevation"), Library.getImage("maps/flat_colour"), true, Library.getImage("maps/flat_colour").getWidth() * 200, -300, 100);
			MeshMaker.makeMesh(Library.getImage("maps/flat_elevation"), Library.getImage("maps/flat_colour"), true, Library.getImage("maps/flat_colour").getWidth() * 300, -300, 100);
			MeshMaker.makeMesh(Library.getImage("maps/flat_elevation"), Library.getImage("maps/flat_colour"), true, Library.getImage("maps/flat_colour").getWidth() * -300, -300, 100);
			MeshMaker.makeMesh(Library.getImage("maps/flat_elevation"), Library.getImage("maps/flat_colour"), true, Library.getImage("maps/flat_colour").getWidth()* - 200, -300, 100);
			
			new GradientBackground(new Color(63, 63, 255), new Color(220, 63, 63)).add();
			
			Weapon w;
			e = new NinjaAlt(100, 500, new Speed(0.7f), new Follow(), new Duelist(), new Violent());
			w = new Naginata(e);
			w.add();
			e.add();
			e = new NinjaAlt(200, 800, new Speed(0.5f), new Follow(), new Duelist(), new Violent());
			w = new Naginata(e);
			w.add();
			e.add();
			
			new ActionJewel(100, 40, 0, new Action() {void perform() {new ForestLevel();}}).add();
			Camera.focus(new ClassFocus(200, Ninja.class));
		}
	}
}
class BirdLevel extends Level {
	
	BirdLevel() {
		super();
		if(!restored) {
			new NinjaAlt(500, 300, new Speed(0.7f), new Follow(0.6f), new Jumpy()).add();
			Random r = new Random();
			new MousePointer().add();
			new MouseLight().add();
			new GradientBackground(Color.blue.darker(0.5f), Color.black).add();
			final int BLOCKWIDTH = 200, BLOCKHEIGHT = 25;
			Block3d b;
			for(int i = 1; i < 20; i ++) {
				b = new Block3d(new Color(r.nextInt()), BLOCKWIDTH * i, 	BLOCKHEIGHT / 2 * i, 0, BLOCKWIDTH, BLOCKHEIGHT * i, BLOCKWIDTH);
				//b.fading = true;
				b.generateCollidable();
				//b.generateMotionTracks();
				b.add();
				b = new Block3d(new Color(r.nextInt()), BLOCKWIDTH * i, 	BLOCKHEIGHT / 4 * i, BLOCKWIDTH, BLOCKWIDTH, BLOCKHEIGHT / 2 * i, BLOCKWIDTH);
				b.add();
			}
			Pyramid3d p = new Pyramid3d(-1000, 0, 0, 200, 200, Color.blue);
			p.generateMotionTracks();
			p.add();
			new Temple(-2000, 0, 0, 300);
			Weird w = new Weird(1000, 200, 0, 200);
			w.generateMotionTracks();
			w.add();
			for(int i = 0; i <100; i++)
				new Pillar(-1600, 100, -i * 150 + 2000, 12, 20, 200).add();
			for(int i = 0; i <100; i++)
				new Pillar(-1300, 100, -i * 150 + 2000, 12, 20, 200).add();
			new RovingGround(new Color(127, 20, 127, 127), new Color(192, 223, 255, 127), 0, -10000).add();			
			
			Player one = Player.getFirst();
			one.setVelocity(0, 0);
			one.setCenterBottom(-100, 100);
			new Lamp(one).add();
			Lamp l = new Lamp(500, 200, 0);
			l.add();
			l = new Lamp(100, 400, 0);
			l.add();
			l = new Lamp(900, 600, 0);
			l.add();
			l = new Lamp(0, 0, 0);
			l.add();
			one.add();
			Camera.focus(new ClassFocus(100, Ninja.class));
			new Ladder(0, 0, -50, 200).add();
			new LadderPlatform(200, 200, 0, 500, false).add();
			new Ladder(200, 200, -10, 210).add();
			for(int i = 0; i < 50; i++)
				new SpinningJewel(r.nextInt(500), r.nextInt(500), r.nextInt(500), r.nextInt(25) + 1, new Color(r.nextInt())).add();
			new Island(-200, 400, 0, 100).add();
			new Island(0, 500, 0, 100).add();
			new Island(-200, 600, 0, 100).add();
			Camera.focus(new ClassFocus(200, Ninja.class));
		}
	}
}
class ForestLevel extends Level {
	RovingGround grass;
	
	ForestLevel() {
		super();
		if(!restored) {
			/**
			 * Level setup
			 */
			new GradientBackground(new Color(63, 63, 255), new Color(220, 220, 255)).add();
			new Sun().add();
			
			Random r = new Random();
			float f = -9500f;
			int maxSize = 1000;
			int xm;
			
			for(int i = 0; i < 50; i++) {
				xm = (int)Math.sqrt((double)r.nextInt(200000000)) - 15000;
				new Mountain(
						xm * 3, 
						-500, 
						f, 
						(r.nextInt(maxSize) + maxSize / 2) * (30000 - Math.abs(xm)) / 9000
						).add();
				f += 20f;
				maxSize -= 4;
			}
			
			// Green field level
			diceMesh(Library.getImage("maps/rough_elevation"), Library.getImage("maps/rough_colour"), Library.getImage("maps/rough_colour").getWidth(), -300, 100);
			//diceMesh(Library.getImage("maps/rough_elevation"), Library.getImage("maps/rough_colour"), 95 * Library.getImage("maps/rough_colour").getWidth(), -300, 100);
			
			int offsetX = 250, offsetY = 100, size = 100;
			for(int i = 0; i < 10; i++) {
				offsetX += 10;
				offsetY += 5;
				size += 5;
				new Island(-250 + i * offsetX, 200 +  i * offsetY, 0, size).add();
			}
			for(int i = 0; i < 10; i++) {
				offsetX += 10;
				offsetY -=  5;
				size -=  5;
				new Island(2500 - i * offsetX, 1700 +  i * offsetY, 0, size).add();
			}
			new Island(-1000, 1000, 0, 400).add();
			new ActionJewel(-1000, 1040, 0, new Action() {void perform() {new TestLevel();}}).add();
			new Island(-300, 400, -1200, 100).add();
			new Island(700, 1200, -1250, 100).add();
			new Island(200, 200, -1500, 100).add();
			new Island(1000, 700, -1000f, 100).add();
			new SpinningJewel(0, 1000, -2000, 800, new Color(192, 192, 192, 63)).add();
			new SpinningJewel(-2000, 2500, -2500, 1000, new Color(255, 192, 192, 63)).add();
			new SpinningJewel(-1000, 2700, -2200, 700, new Color(192, 192, 255, 63)).add();
			
			/**
			 * Actor setup
			 */
			Weapon w;
			Enemy e;
			Player ninja = Player.getFirst();
			ninja.setCenterBottom(300, 100);
			w = new Katana(ninja);
			w.add();
			
			for (int i = 0; i < 5; ++i) {
				e = new NinjaAlt(-2000 * i, 0, new Speed(1.0f * (i % 3)), new Follow(), new Duelist(), new Violent());
				w = new Naginata(e);
				w.add();
				e.add();
			}
			
			/**
			 * Focus camera on player
			 */
			Camera.focus(new ClassFocus(150, Ninja.class));
		}
	}
//	public void LoadModel
	
	// dice that mesh into slices for faster resolve!
	public void diceMesh(Image imgH, Image imgC, int x, int y, int z) {
		int numDivs = 64;
		int width = imgH.getWidth() / numDivs;
		int depth = imgH.getHeight();
		Image tempH, tempC;
		
		for (int i = 0; i < numDivs; ++i) {
			if (i < numDivs - 1) {
				tempH = imgH.getSubImage(width * i, 0, width + 1, depth);
				tempC = imgC.getSubImage(width * i, 0, width + 1, depth);
			} else {
				tempH = imgH.getSubImage(width * i, 0, width, depth);
				tempC = imgC.getSubImage(width * i, 0, width, depth);
			}
			MeshMaker.makeMesh(tempH, tempC, true, x - MeshMaker.XSPACE * i * width, y, z);
		}
	}
	public void diceMesh(Image img, int x, int y, int z) {
		diceMesh(img, img, x, y, z); // for lazynes
	}
}
class Action {
	void perform() {}
	void perform(Actor a) {}
}










