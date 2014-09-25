package Dreamer;

import java.util.ArrayList;

public class MainMenu extends Level {
	
	ArrayList<SavedState> games = new ArrayList<SavedState>();  
	String[] options = {
			"1: Testlevel", 
			"2: SimpleLevel", 
			"3: BirdLevel",
			"4: ForestLevel",
			"T: Test Mode/Run Mode",
			"C: Change Font Colour",
			"Z: Zoom Mode"
	}; 
	
	MainMenu() {
		Element.debug = false;
		Element.clearAll();
		int i = 0;
		for(String s: options)
			new ShadowedMessage(s, 0, i-= 80).add();
		new BorderedBackground().add();
		Camera.focus(new ClassFocus(ShadowedMessage.class));
		//TODO remove this
	}
	
	@Override
	void update() {
	}
}