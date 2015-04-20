package Dreamer;

public class MainMenu extends Level {
	
	Menu main = new Menu(Justification.CENTER, 0, 150);	
	
	void createLevel() {
		
		Element.debug = false;		
		Theme.current = Theme.mono;
	
		main.addOption("START", ()-> {
			KeyHandler.openGameKeys();
			new ForestLevel();
		});
		main.addOption("OPEN EDITOR", ()-> {
			KeyHandler.clearKeys();
			Editor e = new Editor();
			Level.clear();
			e.start();
		});
		main.addOption("OPEN LEVEL", ()-> { Level.openMenu(main); });
		main.addOption("TEST CHARACTERS", (/*why does one drift left?*/)-> {
			new GrassSoldier(-100, 0, Brains.makeSoldier()).add();
			Player.getFirst().add();
			new Skeleton(100, 0, Brains.makeSoldier()).add();
		});
		main.addOption("SET FULLSCREEN", ()-> {
			Dreamer.setFullscreen();		
			Library.load();
		});
		main.open();
		
		new Background("space").add();
		new Block3d(0, -250, -200, 800, 20, 600).generateCollidable().add();
		new Lamp(-400, -100, 0).add();
		new BorderedForeground().add();
		Camera.reset();
	}
}