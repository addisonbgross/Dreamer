package Dreamer;

import org.newdawn.slick.Color;

public class ShapeMaker {

	static Shape3d make(String name) {
		
		if(name.equals("block")) {
			Shape3d s = new Shape3d(0, 0, 0);
			
			int w = 100, h = 100, d = 100;
			Color c = new Color(100, 100, 100);
			
			s.addVertex(w / 2, -h / 2, -d / 2);
			s.addVertex(w / 2, -h / 2, d / 2);
			s.addVertex(-w / 2, -h / 2, d / 2);
			s.addVertex(-w / 2, -h / 2, -d / 2);
			s.addVertex(w / 2, h / 2, -d / 2);
			s.addVertex(w / 2, h / 2, d / 2);
			s.addVertex(-w / 2, h / 2, d / 2);
			s.addVertex(-w / 2, h / 2, -d / 2);
			
			s.addFace(c, 0, 3, 2, 1);
			s.addFace(c, 4, 5, 6, 7);
			s.addFace(c, 1, 2, 6, 5);
			s.addFace(c, 7, 6, 2, 3);
			s.addFace(c, 3, 7, 4, 0);
			s.addFace(c, 0, 1, 5, 4);		
	
			return s;
		}
		
		return null;
	}
}
