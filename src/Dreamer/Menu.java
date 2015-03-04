package Dreamer;

public class Menu {
	
	Menu (Justification j, float xPosition, float yPosition) {
		justification = j;
		xposition = xPosition;
		yposition = yPosition;
	}
	
	java.util.List<MenuOption> optionList = new java.util.ArrayList<MenuOption>();
	Justification justification = Justification.LEFT;
    float spacing = 40, xposition = 0, yposition = 0;
	int currentOption = 0;
	
	void addOption(String s, Action a) {
		optionList.add(
				new MenuOption(s, a)
				.setPosition(xposition, yposition -= spacing)
				.setJustification(justification)
				);
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
}