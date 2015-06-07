package Dreamer;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Model {

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

	public void add() {
		for (Shape3d s : models) {
			s.recenter();
			// System.out.println(s.position);
			s.add();
		}
	}
	
	public Shape3d getFirst() {
		return models.get(0);
	}
}
