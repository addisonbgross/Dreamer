package Dreamer;

public class Menu {
	
	Menu (Justification j, float xPosition, float yPosition) {
		justification = j;
		xposition = xPosition;
		yposition = yPosition;
	}
	
	private Menu parent;
	java.util.List<MenuOption> optionList = new java.util.ArrayList<MenuOption>();
	Justification justification = Justification.LEFT;
    float spacing = 40, xposition = 0, yposition = 0;
	int currentOption = 0;
	
	Menu open() {
		if(parent != null)
			parent.exit();
		KeyHandler.saveKeys();
		KeyHandler.openMenuKeys(this);
		optionList.get(currentOption).shadowMessage.highlight = true;
		for(MenuOption mo: optionList)
			mo.shadowMessage.add();
		return this;
	}
	
	void command(String s) {
		int size = optionList.size();
		
		switch(s) {
		
			case "up":
				currentOption = (size + currentOption - 1) % size ;
				break;
				
			case "down":
				currentOption = (currentOption + 1) % size;
				break;
				
			case "select":
				Action a = optionList.get(currentOption).action;
				Performable d = optionList.get(currentOption).performable;
				if(a != null)
					a.start();
				if(d != null)
					d.perform();
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
		if(parent != null)
			parent.open();
	}
	
	private class MenuOption {
		Action action;
		Performable performable;
		ShadowedMessage shadowMessage;
		
		MenuOption(String s, Action a) {
			action = a;
			shadowMessage = new ShadowedMessage(s, 0, 0);
		}
		MenuOption(String s, Performable d) {
			performable = d;
			shadowMessage = new ShadowedMessage(s, 0, 0);
		}
		MenuOption setPosition(float x, float y) {
			shadowMessage.setPosition(x, y, 0);
			return this;
		}
		MenuOption setJustification(Justification j) {
			shadowMessage.justification = j;
			return this;
		}
 	}

	Menu addExitOption() {
		addOption(
				"EXIT MENU",
				()-> { exit(); }
				);
		return this;
	}

	Menu addOption(String s, Action a) {
		optionList.add(
				new MenuOption(s, a)
				.setPosition(xposition, yposition -= spacing)
				.setJustification(justification)
				);
		return this;
	}
	Menu addOption(String s, Performable d) {
		optionList.add(
			new MenuOption(s, d)
			.setPosition(xposition, yposition -= spacing)
			.setJustification(justification)
			);
		return this;
	}

	Menu setParent(Menu m) {
		parent = m;
		return this;
	}
}