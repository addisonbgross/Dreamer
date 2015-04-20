package Dreamer;

import java.io.IOException;

public class MainMenu extends Level {
	Menu main = new Menu(Justification.CENTER, 0, 150);	
	
	void createLevel() {
		Element.debug = false;
		Element.clearAll();
		
		Theme.current = Theme.mono;
	
		main.addOption(
				"START", ()-> {
					KeyHandler.openGameKeys();
					new ForestLevel();
				});
		main.addOption(
				"OPEN EDITOR", ()-> {
						KeyHandler.clearKeys();
						Editor e = new Editor();
						Level.clear();
						e.start();
				});
		main.addOption(
				"TEST MOTIONTRACKS", ()-> { new TestLevel(); });
		main.addOption(
				"OPEN LEVEL", ()-> { new TestLevel(); });
		main.addOption(
				"TEST CHARACTERS", (/*why does one drift left?*/)-> {
					new GrassSoldier(-100, 0, Brains.makeSoldier()).add();
					Player.getFirst().add();
					new Skeleton(100, 0, Brains.makeSoldier()).add();
				});
		main.addOption(
				"SET FULLSCREEN", ()-> {
					Dreamer.setFullscreen();		
					try {
						Library.load();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
		main.open();
		
		new Background("space").add();
		new Block3d(0, -250, -200, 800, 20, 600).generateCollidable().add();
		new Lamp(-400, -100, 0).add();
		//new Model("scene", 200, 100, -200, -1500).add();
		//new Sun().add();
		new BorderedForeground().add();
		Camera.reset();
	}
}