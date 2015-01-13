package Dreamer;

public class MainMenu extends Level {
	
	final static int spacing = 80;
	static int currentOption = 0;
	
	static String[] options = {
			"NEW GAME",
			"LOAD SAVED",
			"OPTIONS",
			"Press k for regular keys"
	}; 
	static ShadowedMessage[] messages = new ShadowedMessage[options.length];
	
	static int space = options.length * spacing / 2;
	
	void createLevel() {
		Element.debug = false;
		Element.clearAll();
		Theme mono = new Theme();
		mono.addColor("light", 200, 200, 200);
		mono.addColor("dark", 25, 25, 25);
		mono.addColor("font", 225, 225, 225);
		
		Theme.current = mono;
		
		int i = 0;
		while(i < options.length) {
			ShadowedMessage sm = new ShadowedMessage(options[i], 0, space-= spacing);
			messages[i] = sm;
			sm.add();
			i++;
		}
		messages[0].highlight = true;
		
		new Background("space").add();
		new Block3d(0, -250, -200, 800, 20, 600).add();
		new Lamp(-400, -100, 0).add();
		//new Model("scene", 200, 100, -200, -1500).add();
		//new Sun().add();
		new BorderedForeground().add();
		Camera.focus(Dreamer.origin);
	}
	
	static void move(String s) {
		switch(s) {
		
		case "up":
			if(currentOption > 0)
				currentOption--;
			break;
			
		case "down":
			if(currentOption < options.length-1)
				currentOption++;
			break;
		}
		
		for(int i = 0; i < options.length; i++) {
			messages[i].highlight = (i == currentOption)? true : false;
		}
	}
}