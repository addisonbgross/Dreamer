package Dreamer;

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
						e.start();
					}
				});
		main.addOption(
				"RUN TEST",
				new Action() {
					void perform() {
						ShadowedMessage sm;
						sm = new ShadowedMessage("LEFT", -Constants.screenWidth / 2, 0);
						sm.justification = Justification.LEFT;
						sm.add();
						sm = new ShadowedMessage("CENTER", 0, 0);
						sm.justification = Justification.CENTER;
						sm.add();
						sm = new ShadowedMessage("RIGHT", Constants.screenWidth / 2, 0);
						sm.justification = Justification.RIGHT;
						sm.add();
						Library.getModel("sphere", 100, 0, 0, 0).get(0).add();
						/*
						OnDemandLoader.Start();
						Resource r = new Resource("space", FileType.IMG);
						r.getResource();
						*/
					}
				});
		main.open();
		
		new Background("space").add();
		new Block3d(0, -250, -200, 800, 20, 600).add();
		new Lamp(-400, -100, 0).add();
		//new Model("scene", 200, 100, -200, -1500).add();
		//new Sun().add();
		new BorderedForeground().add();
		Camera.focus(Dreamer.origin);
	}
}