package Dreamer;

public class MainMenu extends Level {
	
	void createLevel() {
		Element.debug = false;
		Element.clearAll();
		
		Theme mono = new Theme();
		mono.addColor("light", 200, 200, 200);
		mono.addColor("dark", 25, 25, 25);
		mono.addColor("font", 225, 225, 225);
		Theme.current = mono;
		
		Menu main = new Menu();	
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
		main.display();
		
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
	java.util.List<MenuOption> optionList = new java.util.ArrayList<MenuOption>();
	int spacing = 40, position = 200;
	int currentOption = 0;
	
	Menu() {
		//adds keys to control menu
		KeyHandler.saveKeys();
		KeyHandler.openMenuKeys(this);
	}
	
	void addOption(String s, Action a) {
		optionList.add(new MenuOption(s, a, position -= spacing));
	}
	
	void display() {
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
				KeyHandler.clearKeys();
				KeyHandler.restoreKeys();
				for(MenuOption mo: optionList) {
					mo.shadowMessage.remove();
				}
				break;
		}
		
		for(int i = 0; i < size; i++) {
			optionList.get(i).shadowMessage.highlight = (i == currentOption)? true : false;
		}
	}
	
	private class MenuOption {
		Action action;
		ShadowedMessage shadowMessage;
		MenuOption(String s, Action a, int i) {
			action = a;
			shadowMessage = new ShadowedMessage(s, 0, i);
		}
	}
}