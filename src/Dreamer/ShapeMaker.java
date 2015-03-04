package Dreamer;

import org.newdawn.slick.Color;

public class ShapeMaker {

	static Shape3d make(String name) {
		
		if(name.equals("block")) {
			return new Block3d(Color.red, 0, 0, 0, 100, 100, 100);
		}
		
		return null;
	}
}
