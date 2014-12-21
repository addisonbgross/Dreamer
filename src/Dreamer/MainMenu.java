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
		
		int i = options.length * spacing / 2;
		for(String s: options)
			new ShadowedMessage(s, 0, i-= spacing).add();
		new GreyRoom(0, -250, -200).add();
		new Lamp(-400, -100, 0).add();
		new Sun().add();
		new BorderedForeground().add();
		Camera.focus(Dreamer.origin);
	}
}