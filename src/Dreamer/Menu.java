package Dreamer;

import jdk.nashorn.internal.runtime.options.Options;

public class Menu {
	
	Menu (Justification j, float xPosition, float yPosition) {
		justification = j;
		xposition = xPosition;
		yposition = yPosition;
	}
	
	Menu parent;
	java.util.List<MenuOption> optionList = new java.util.ArrayList<MenuOption>();
	Justification justification = Justification.LEFT;
    float spacing = 40, xposition = 0, yposition = 0;
	int currentOption = 0;
	
	Menu addOption(String s, Action a) {
		optionList.add(
				new MenuOption(s, a)
				.setPosition(xposition, yposition -= spacing)
				.setJustification(justification)
				);
		return this;
	}
	
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
		if(parent != null)
			parent.open();
	}
	
	private class MenuOption {
		Action action;
		ShadowedMessage shadowMessage;
		MenuOption(String s, Action a) {
			action = a;
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
				new MenuAction(this, "exit")
				);
		return this;
	}
}