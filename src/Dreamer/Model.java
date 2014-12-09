package Dreamer;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

public class Model extends Element {
	private ArrayList<Shape3d> models;
	
	Model(String s, int scale, int x, int y, int z) {
		models = Library.getModel(s, scale, x, y, z);
	}
	@Override
	void draw(Graphics g) {
		for (Shape3d s : models)
			if (s.isVisible())
				for (Face f: s.getFaces())
					f.addToDrawList();
	}
	@Override
	void add() {
		for (Shape3d s : models)
			s.add();
	}
}
