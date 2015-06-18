package Dreamer;

import enums.*;

public class MainMenu extends Level {
	
	Menu main = new Menu(Justification.CENTER, 0, 0);	
	static Editor editor = new Editor();
	
	void createLevel() {
		
		Manager.debug = false;		
		Theme.current = Theme.mono;
		
		main.addOption("TEST WORLD", ()-> {
			
			main.exit();
			World.select("dusk");
			World.playLevel(0);
		});
		
		main.addOption("START", ()-> {
			
			World.select("dusk");
			new Dusk_1();
		});
		
		main.addOption("OPEN EDITOR (TAB IN-GAME)", ()-> {
		
			Manager.clearAll();
			editor.start();
		});
		
		main.addOption("OPEN LEVEL", ()-> { Level.openSelectionMenu(main, ""); });
		
		main.addOption("TEST CHARACTERS", (/*why does one drift left?*/)-> {
		
			new GrassSoldier(-100, 0, Brains.makeSoldier()).add();
			Player.getFirst().add();
			new Skeleton(100, 0, Brains.makeSoldier()).add();
		});
		
		
		
		main.addOption("CHANGE FONT", (/*why does one drift left?*/)-> {
			
			main.setFont(FontType.DEFAULT);
		});
		
		new Background("space").add();
		new Block3d(0, -250, -200, 800, 20, 600).generateCollidable().add();
		new Lamp(-400, -100, 0).add();
		new BorderedForeground().add();
		Camera.reset();
	}
	
	void start() {
		main.open();
	}
}