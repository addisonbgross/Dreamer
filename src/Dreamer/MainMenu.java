package Dreamer;

public class MainMenu extends Level {
	
	static Editor e;
	
	String[] options = {
			"1: Testlevel", 
			"2: SimpleLevel", 
			"3: BirdLevel",
			"4: ForestLevel",
			"T: Test Mode/Run Mode",
			"C: Change Font Colour",
			"Z: Zoom Mode"
	}; 
	
	void createLevel() {
		Element.debug = false;
		Element.clearAll();
		//e = new Editor();
		/*
		int i = 0;
		for(String s: options)
			new ShadowedMessage(s, 0, i-= 80).add();
		new BorderedBackground().add();
		Camera.focus(new ClassFocus(ShadowedMessage.class));
		*/
	}
}