package Dreamer;

public class MainMenu extends Level {
	
	static Editor e;
	static int spacing = 80;
	
	String[] options = {
			"NEW GAME",
			"LOAD SAVED",
			"OPTIONS"
	}; 
	
	void createLevel() {
		Element.debug = false;
		Element.clearAll();
		//e = new Editor();
		Theme mono = new Theme();
		mono.addColor("light", 200, 200, 200);
		mono.addColor("dark", 25, 25, 25);
		mono.addColor("font", 225, 225, 225);
		
		Theme.current = mono;
		
		new Background("space").add();
		int i = options.length * spacing / 2;
		for(String s: options)
			new ShadowedMessage(s, 0, i-= spacing).add();
		new Block3d(0, -250, -200, 800, 20, 600).add();
		new Lamp(-400, -100, 0).add();
		new Model("desk",100, -200, -200, 0).add();
		//new Sun().add();
		new BorderedForeground().add();
		Camera.focus(Dreamer.origin);
	}
}