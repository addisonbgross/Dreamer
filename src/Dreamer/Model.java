package Dreamer;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Model {
	public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	public ArrayList<ModelFace> faces = new ArrayList<ModelFace>();
	public Model() { }
	
}
