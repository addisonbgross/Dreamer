package Dreamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

import org.newdawn.slick.Color;

import Dreamer.enums.Justification;
import Dreamer.enums.Status;

// Level is the base class for all games states, including menus, editors, debuggers etc
class World {
	
	//-----------FIELDS

	static String directory = "";
	static String levels[];
	
	//-----------METHODS
	
	static void select(String s) {
		
		levels = new File(Constants.LEVELPATH + (directory = s + Constants.slash)).list();
	}
	
	private static void selectLevel(int i) {
		
		System.out.println("selecting level " + i);
		
		try {
			
			System.out.println("opening " + directory + levels[i]);
			String nextLevel = levels[i].replace(".level", "");
			Level.read(directory + nextLevel);
			
		} catch(ArrayIndexOutOfBoundsException aioobe) {
			
			System.err.println("Level.java err: Level " + i + " not available");
			
			switch(i) {
			
				case 0:
					new Dusk_1();
					break;
				case 1:
					new Dusk_2();
					break;
				case 2:
					new Dusk_3();
					break;
				case 3:
					new Dusk_4();
					break;
				case 4:
					new Dusk_5();
					break;
				default:
					new MainMenu();
			}
		}
	}
	
	static void playLevel(int i) {	
		
		Manager.clearAll();
		selectLevel(i);
		Level.initializePlayer();
	}
}

class Level {
	
	static Marker playerSpawn = new Marker("playerSpawn", 0, 50);
	Enemy e; //Generic enemy pointer
	Weapon w;
	static Level currentLevel;
	static boolean frozen = false, levelChanged = false;
	static int freezeCounter = 0;
	
	Level() {

		levelChanged = true;
		currentLevel = this;
	}

	void start() { initializePlayer(); }
	
	static void initializePlayer() {
		
		Keys.openGameKeys();
		
		Player p = Player.getFirst();
		p.setCenterBottom(playerSpawn.getX(), playerSpawn.getY());
		System.out.println(playerSpawn.toString());
		p.add();
		System.out.println(p.toString());
		
		Camera.focus(new ClassFocus(200, Ninja.class));
	}
	
	static void updateCurrent() {
		
		if(levelChanged) {
			levelChanged = false;
			freezeCounter = 2;
			Manager.clearAll();
			currentLevel.createLevel();
			currentLevel.start();
		}
	}
	
	static void write(String s) {
		
		ObjectOutputStream out;
		
		try {
		 	out = new ObjectOutputStream(new FileOutputStream(Constants.LEVELPATH + s + ".level"));
			out.writeObject(Manager.masterList);
			out.writeObject(Background.background);
			out.writeObject(Foreground.foreground);
			out.writeObject(playerSpawn);
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	static void read(String s) {
		
		try{ 
			FileInputStream door = new FileInputStream(Constants.LEVELPATH + s + ".level"); 
			ObjectInputStream reader = new ObjectInputStream(door); 
			
			for(Element e: (HashSet<Element>) reader.readObject()) {
				// TODO unhack this, don't save players
				// perhaps create spawn points for all Actors?
				if(e.getClass() != Ninja.class)
					Manager.add(e);
			}
			for(Element e: (ArrayList<Element>) reader.readObject())
				Manager.add(e);
			for(Element e: (ArrayList<Element>) reader.readObject())
				Manager.add(e);
			playerSpawn = ( (Marker) reader.readObject());
			
			reader.close();
		} catch (IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	void createLevel() {
		//Override this method to create objects in level
	}
	
	public static void openSelectionMenu(Menu callbackMenu, String path) {

		Menu levelMenu = new Menu(Justification.CENTER, 0, 150);
		levelMenu.setParent(callbackMenu);
		
		for (File file : new File(Constants.LEVELPATH + path).listFiles()) {
			
			if(file.isDirectory()) {
				
				levelMenu.addOption("[D]" + file.getName(), ()-> {
					
					openSelectionMenu(levelMenu, file.getName() + Constants.slash);
				});
				
			} else {
				
				levelMenu.addOption(file.getName(), ()-> {
					
					Manager.clearAll();
					Level.read(path + file.getName().replace(".level", ""));
					Keys.openGameKeys();
					Player.getFirst().add();
					Player.getFirst().setCenterBottom(0, 200);
					Camera.focus(new ClassFocus(200, Ninja.class));
				});
			}
		}
		
		levelMenu.addExitOption();
		levelMenu.open();
	}
	public static void openSelectionMenu(Menu callbackMenu) {
		openSelectionMenu(callbackMenu, "");
	}
}
class TestLevel extends Level {
	@Override
	void createLevel() {
		Shape3d s = new Block3d(Color.red, 0, 0, 0, 100, 100, 100);
		s.generateMotionTracks();
		s.add();
	}
}
class SimpleLevel extends Level {	
	@Override
	void createLevel() {
		Player p = Player.getFirst();
		new Lamp(p).add();
		new Lamp(p.getX() - 3000, 200, -5000).add();
		new Lamp(p.getX() - 5000, 300, -3000).add();
		new Lamp(p.getX() + 3000, 300, -5000).add();
		new Katana(p).add();
		
		p.setCenterBottom(0, 1);
		p.add();

		// create a terrain mesh
		MeshMaker.diceMesh(Library.getImage(Constants.MAPPATH + "madness_elevation"), Library.getImage(Constants.MAPPATH + "madness_colour"), 35000, 0, 100);
		
		new GradientBackground(new Color(63, 63, 255), new Color(220, 63, 63)).add();
		
		Camera.focus(new ClassFocus(200, Ninja.class));
	}
}
class BirdLevel extends Level {	
	@Override
	void createLevel () {
		new NinjaAlt(500, 300, Brains.makeSoldier()).add();
		Random r = new Random();
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
		one.dynamics.setVelocity(0, 0, 0);
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
			new SpinningJewel(r.nextInt(500), r.nextInt(500), r.nextInt(500), r.nextInt(25) + 1).add();
		new Island(-200, 400, 0, 100).add();
		new Island(0, 500, 0, 100).add();
		new Island(-200, 600, 0, 100).add();
		
		Camera.focus(new ClassFocus(200, Ninja.class));
	}
}
class ForestLevel extends Level {
	@Override
	void createLevel () {
		/**
		 * Level setup
		 */
		new SolidBackground(new Color(0, 100, 50)).add();
		// new GradientBackground(new Color(63, 63, 255), new Color(220, 220, 255)).add();
		new Sun().add();
		
		Theme.current = Theme.ice;
		
		// create a terrain mesh
		MeshMaker.diceMesh(Library.getImage(Constants.MAPPATH + "madness_elevation"), Library.getImage(Constants.MAPPATH + "madness_colour"), 35000, 0, 0);
		
		Player one = Player.getFirst();
		w = new Knife(one);
		one.setCenterBottom(2900, 1551);
		new Lamp(one).add();
		one.add();
		w.add();
	
		Enemy en = new Skeleton(500, 300, Brains.makeSoldier());
		w = new Katana(en);
		en.add();
		w.add();
		
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
		new Island(-300, 400, -1200, 100).add();
		new Island(700, 1200, -1250, 100).add();
		new Island(200, 200, -1500, 100).add();
		new Island(1000, 700, -1000f, 100).add();	

		Theme.current = Theme.fire;
		
		new ActionJewel(-1000, 1040, 0, ()-> { new BirdLevel(); }).add();
		new ActionJewel(-1000, 50, 0, (Actor a)-> { a.addStatus(Status.TRYJUMP); }).add();
		new ActionJewel(-800, 50, 0, (Actor a)-> { a.addStatus(Status.TRYJUMP); }).add();
		
		Theme.current = Theme.transparentFire;
		
		new SpinningJewel(0, 100, -1000, 400).add();
		new SpinningJewel(2000, 2500, -2500, 400).add();
		new SpinningJewel(1000, 2700, -2200, 700).add();
		
		Theme.current = Theme.fire;
		
		new MovingMotionTrack(-300, 300, -200, 300).add();
		
		/**
		 * Focus camera on player
		 */
		Camera.focus(new ClassFocus(150, Ninja.class));
	}
}

class EmptyLevel extends Level {

	@Override
	void createLevel() {
		
		Theme.current = Theme.fire;
		
		Block3d b = new Block3d(0, 0, 0, 100, 100, 100);
		b.add();
		Camera.focus(b);
	}
}

/** Dusk Level **/
class Dusk_1 extends Level {
	
	void createLevel() {
		
		Weapon w = new Katana(null);
		w.setCenter(1300, 100);
		w.add();
		
		playerSpawn.setCenter(1400, 200);
		
		Enemy y = new Skeleton(1300, 100, Brains.makeSoldier());
		new Knife(y).add();
		y.add();
		
		new GradientBackground(new Color(50, 50, 100), new Color(0, 0, 0)).add();
		new Model("dusk_1", 200, 0, 0, 0).add();
		new ActionJewel(-1300, 125, 0, ()-> World.playLevel(1) ).add();
		new Sunset().add();		
	}
}

class Dusk_2 extends Level {
	
	void createLevel() {
	
		Weapon w = new Katana(null);
		w.setCenter(0, 0);
		w.add();
		
		playerSpawn.setCenter(1400, 50);

		new GradientBackground(new Color(50, 50, 100), new Color(0, 0, 0)).add();
		new Model("dusk_2", 200, 0, 0, 0).add();
		new Sunset().add();
		new ActionJewel(-1400, 30, 0, ()-> World.playLevel(2) ).add();
	}
}

class Dusk_3 extends Level {
	
	void createLevel() {
	
		Weapon w = new Katana(null);
		w.setCenter(1400, -500);
		w.add();
		
		playerSpawn.setCenter(1450, -550);
		
		new GradientBackground(new Color(50, 50, 100), new Color(0, 0, 0)).add();
		new Model("dusk_3", 200, 0, 0, 0).add();
		new Sunset().add();
		new Lamp(Player.getFirst()).add();
		new ActionJewel(-1400, 1400, 0, ()-> World.playLevel(3) ).add();
	}
}

class Dusk_4 extends Level {
	
	void createLevel() {
		
		Weapon w = new Katana(null);
		w.setCenter(3000, 200);
		w.add();
		
		playerSpawn.setCenter(3500, 50);
		
		new GradientBackground(new Color(50, 50, 100), new Color(0, 0, 0)).add();
		new Model("dusk_4", 500, 0, 0, 0).add();
		new Sunset().add();
		new Lamp(Player.getFirst()).add();
		new ActionJewel(-1900, 100, 0, ()-> World.playLevel(4) ).add();
		new ActionJewel(-1950, 100, 0, ()-> World.playLevel(4) ).add();
	}
}

class Dusk_5 extends Level {

	void createLevel() {
		
		Weapon w = new Katana(null);
		w.setCenter(5500, 2100);
		w.add();
		
		playerSpawn.setCenter(5700, 2025);
		
		new SolidBackground(new Color(0, 0, 0)).add();
		new Model("dusk_5", 500, 0, 0, 0).add();
		new Lamp(Player.getFirst()).add();
		new ActionJewel(-2950, -3900, 0, ()-> World.playLevel(5) ).add();
	}
}

class Dusk_6 extends Level {

	void createLevel() {

		Weapon w = new Katana(null);
		w.setCenter(5500, 2100);
		w.add();
		
		playerSpawn.setCenter(-1200, -900);
		
		new SolidBackground(new Color(0, 0, 0)).add();
		new Model("dusk_6", 500, 0, 0, 0).add();
		new Lamp(Player.getFirst()).add();
	}
}