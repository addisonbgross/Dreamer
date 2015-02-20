package Dreamer;

import org.newdawn.slick.Color;

class Pyramid3d extends Shape3d {
	Pyramid3d(float x, float y, float z, float w, float h, float d) {
		super(x, y, z);
		addVertex(w/2, 0, -d/2);  //0
		addVertex(-w/2, 0, -d/2); //1
		addVertex(-w/2, 0, d/2);  //2
		addVertex(w/2, 0, d/2);   //3
		addVertex(0, h, 0);	  //4
		
		addFace(Theme.current.getColor(Theme.Default.LIGHT), 0, 1, 2, 3);
		addFace(Theme.current.getColor(Theme.Default.LIGHT), 0, 4, 1);
		addFace(Theme.current.getColor(Theme.Default.LIGHT), 1, 4, 2);
		addFace(Theme.current.getColor(Theme.Default.LIGHT), 2, 4, 3);
		addFace(Theme.current.getColor(Theme.Default.LIGHT), 3, 4, 0);
	}
	Pyramid3d(float x, float y, float z, float base, float h) {
		this(x, y, z, base, h, base);
	}
}