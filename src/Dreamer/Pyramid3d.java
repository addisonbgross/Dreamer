package Dreamer;

import org.newdawn.slick.Color;

class Pyramid3d extends Shape3d {
	Pyramid3d(float x, float y, float z, float w, float h, float d, Color c) {
		super(x, y, z);
		addVertex(w/2, 0, -d/2);  //0
		addVertex(-w/2, 0, -d/2); //1
		addVertex(-w/2, 0, d/2);  //2
		addVertex(w/2, 0, d/2);   //3
		addVertex(0, h, 0);	  //4
		
		addFace(c, 0, 1, 2, 3);
		addFace(c, 0, 4, 1);
		addFace(c, 1, 4, 2);
		addFace(c, 2, 4, 3);
		addFace(c, 3, 4, 0);
	}
	Pyramid3d(float x, float y, float z, float base, float h, Color c) {
		this(x, y, z, base, h, base, c);
	}
}