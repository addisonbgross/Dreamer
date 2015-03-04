package Dreamer;

public class MainMenu extends Level {
	Menu main = new Menu();	
	
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

class Menu {
	
	Menu (Justification j, float xPosition) {
		justification = j;
		xposition = 0;
	}
	
	java.util.List<MenuOption> optionList = new java.util.ArrayList<MenuOption>();
	Justification justification = Justification.LEFT;
	int spacing = 40, xposition = 0, yposition = 100;
	int currentOption = 0;
	
	void addOption(String s, Action a) {
		optionList.add(new MenuOption(s, a, xposition, yposition -= spacing));
	}
	
	void open() {
		KeyHandler.saveKeys();
		KeyHandler.openMenuKeys(this);
		optionList.get(currentOption).shadowMessage.highlight = true;
		for(MenuOption mo: optionList)
			mo.shadowMessage.add();
	}
	
	void command(String s) {
		int size = optionList.size();
		
		switch(s) {
		
			case "up":
				if(currentOption > 0)
					currentOption--;
				break;
				
			case "down":
				if(currentOption < size-1)
					currentOption++;
				break;
				
			case "select":
				optionList.get(currentOption).action.perform();
				break;
				
			case "exit":
				exit();
				break;
		}
		
		for(int i = 0; i < size; i++) {
			optionList.get(i).shadowMessage.highlight = (i == currentOption)? true : false;
		}
	}
	
	void exit() {
		KeyHandler.restoreKeys();
		for(MenuOption mo: optionList) {
			mo.shadowMessage.remove();
		}
	}
	
	private class MenuOption {
		Action action;
		ShadowedMessage shadowMessage;
		MenuOption(String s, Action a, int x, int y) {
			action = a;
			shadowMessage = new ShadowedMessage(s, x, y);
		}
	}
}