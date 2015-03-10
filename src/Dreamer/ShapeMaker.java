package Dreamer;

import java.util.Random;

import org.newdawn.slick.Color;

public class ShapeMaker {

	static Shape3d make(String name) {
		
		if(name.equals("block")) {
			java.util.Random r = new java.util.Random();
			return new Block3d(new Color(r.nextInt()), 0, 0, 0, 100, 100, 100);
		}
		
		if(name.equals("island")) {
			java.util.Random r = new java.util.Random();
			return new Island(0, 0, 0, 100);
		}
		
		return null;
	}
}
