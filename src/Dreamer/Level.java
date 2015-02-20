package Dreamer;

import java.util.Random;

import org.newdawn.slick.Color;

// Level is the base class for all games states, including menus, editors, debuggers etc

class Level {
	Enemy e; //Generic enemy pointer
	Weapon w;
	static Level current;
	static boolean frozen = false, levelChanged = false;
	static int freezeCounter = 0;
	
	Level() {
		levelChanged = true;
		current = this;
	}
	static void clear() {
		Element.clearAll();
	}
	//TODO move this function and ArrayList Keys into Keyhandler.java
	static void updateCurrent() {
		if(levelChanged) {
			levelChanged = false;
			freezeCounter = 2;
			Element.clearAll();
			current.createLevel();
		}
	}
	void createLevel() {
		//Override this method to create objects in level
	}
}
class TestLevel extends Level {
	void createLevel() {
		new GradientBackground(new Color(50, 50, 100), new Color(0, 0, 0)).add();
		new Model("mountainTest", 300, 0, 200, -500).add();
		
		Player p = Player.getFirst();
		p.setCenterBottom(0,  500);
		w = new Katana(p);
		w.add();
		p.add();
		
		NinjaAlt a;
		a = new NinjaAlt(500, 500, Brains.makeSoldier());
		w = new Katana(a);
		w.add();
		a.add();
		a = new NinjaAlt(700, 500, Brains.makeSoldier());
		w = new Katana(a);
		w.add();
		a.add();
		a = new NinjaAlt(850, 500, Brains.makeSoldier());
		w = new Katana(a);
		w.add();
		a.add();
		
		//new Sun().add();
		//new Lamp(p).add();
		
		Camera.focus(new ClassFocus(200, Ninja.class));
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
		
		//Library.getModel("colourcube", 0, 150, -250).add();
        
        //Library.getModel("scene", -500,  0,  0).add();
		
		//Library.getModel("monkey", 300, 101, -250).add();
        
        //Library.getModel("monkey", 600, 101, -250).add();
		
		//new ActionJewel(100, 40, 0, new Action() {void perform() {new ForestLevel();}}).add();
		Camera.focus(new ClassFocus(200, Ninja.class));
	}
}
class BirdLevel extends Level {	
	@Override
	void createLevel () {
		new NinjaAlt(500, 300, Brains.makeSoldier()).add();
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
		Pyramid3d p = new Pyramid3d(-1000, 0, 0, 200, 200);
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
		new GradientBackground(new Color(63, 63, 255), new Color(220, 220, 255)).add();
		new Sun().add();
		
		Theme.current = Theme.ice;
		
		// create a terrain mesh
		MeshMaker.diceMesh(Library.getImage(Constants.MAPPATH + "madness_elevation"), Library.getImage(Constants.MAPPATH + "madness_colour"), 35000, 0, 0);
		
		Player one = Player.getFirst();
		w = new Katana(one);
		one.setCenterBottom(2900, 1551);
		//new Lamp(one).add();
		one.add();
		w.add();
		
		Enemy en = new NinjaAlt(500, 300, Brains.makeSoldier());
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
		
		new ActionJewel(-1000, 1040, 0, new Action() {void perform() {new BirdLevel();}}).add();
		new ActionJewel(-1000, 50, 0, new Action() {
			void perform(Actor a) {
				a.addStatus("tryjump");
			}
		}).add();
		new ActionJewel(-800, 50, 0, new KeyedActorAction(Player.getFirst(), "tryjump")).add();
		
		Theme.current = Theme.transparentFire;
		
		new SpinningJewel(0, 100, -1000, 400).add();
		new SpinningJewel(2000, 2500, -2500, 400).add();
		new SpinningJewel(1000, 2700, -2200, 700).add();
		
		/**
		 * Focus camera on player
		 */
		Camera.focus(new ClassFocus(150, Ninja.class));
	}
}










