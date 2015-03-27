package Dreamer;

import java.io.IOException;

public class MainMenu extends Level {
	Menu main = new Menu(Justification.CENTER, 0, 0);	
	
	void createLevel() {
		Element.debug = false;
		Element.clearAll();
		
		Theme.current = Theme.mono;
	
		main.addOption(
				"START", 				
				new Action() {
					void perform() {
						KeyHandler.openGameKeys();
						new ForestLevel();
					}
				});
		main.addOption(
				"OPEN EDITOR",
				new Action() {
					void perform() {
						KeyHandler.clearKeys();
						Editor e = new Editor();
						Level.clear();
						e.start();
					}
				});
		main.addOption(
				"SET FULLSCREEN",
				new Action() {
					void perform() {
						Dreamer.setFullscreen();
						
						
						
						try {
							Library.load();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
		main.open();
		
		new Background("space").add();
		new Block3d(0, -250, -200, 800, 20, 600).add();
		new Lamp(-400, -100, 0).add();
		//new Model("scene", 200, 100, -200, -1500).add();
		//new Sun().add();
		new BorderedForeground().add();
		Camera.reset();
	}
}