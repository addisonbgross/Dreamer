package Dreamer;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Model extends Element {

	private static final long serialVersionUID = 1L;
	private ArrayList<Shape3d> models;
	private ArrayList<Vector3f> lights;

	Model(String s, int scale, int x, int y, int z) {
		models = Library.getModel(s, scale, x, y, z);
		lights = Library.getModelLights(s, scale, x, y, z);
		if (lights.size() > 0)
			addLights();
	}

	public void addLights() {
		for (Vector3f v : lights)
			new Lamp(v.x, v.y, v.z, 500).add();
	}

	@Override
	void add() {
		for (Shape3d s : models) {
			s.recenter();
			s.add();
		}
	}
}
