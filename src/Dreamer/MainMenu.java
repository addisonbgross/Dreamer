package Dreamer;

import Dreamer.enums.Justification;

public class MainMenu extends Level {
	
	Menu main = new Menu(Justification.CENTER, 0, 150);	
	static Editor editor;
	
	void createLevel() {
		
		Element.debug = false;		
		Theme.current = Theme.mono;
		
		main.addOption("TEST WORLD", ()-> {
			
			new World("dusk").playLevel(0);
		});
		
		main.addOption("START", ()-> {
			
			Level l = new Dusk_1();
		});
		
		main.addOption("OPEN EDITOR (TAB IN-GAME)", ()-> {
		
			KeyHandler.clearKeys();
			Level.clear();
			editor = new Editor();
			editor.start();
		});
		
		main.addOption("OPEN LEVEL", ()-> { Level.openSelectionMenu(main); });
		
		main.addOption("TEST CHARACTERS", (/*why does one drift left?*/)-> {
		
			new GrassSoldier(-100, 0, Brains.makeSoldier()).add();
			Player.getFirst().add();
			new Skeleton(100, 0, Brains.makeSoldier()).add();
		});
		
		new Background("space").add();
		new Block3d(0, -250, -200, 800, 20, 600).generateCollidable().add();
		new Lamp(-400, -100, 0).add();
		new BorderedForeground().add();
		Camera.reset();
	}
	
	@Override
	void start() { main.open(); }
}