package Dreamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
//does not yet work
public class ObjLoader {
	public static Shape3d loadModel(File f) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		//Model m = new Model();
		Shape3d m = new Shape3d();
		String line;
		int a, b, c;
		Random r = new Random();
		
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.addVertex(x, y, z);
			} else if (line.startsWith("vn ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				//m.addVertex(x, y, z);
			} else if (line.startsWith("vn ")) {
				Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]),
													  Float.valueOf(line.split(" ")[2].split("/")[0]),
													  Float.valueOf(line.split(" ")[3].split("/")[0]));
				
				Vector3f normalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]),
													  Float.valueOf(line.split(" ")[2].split("/")[2]),
													  Float.valueOf(line.split(" ")[3].split("/")[2]));
				//m.(new ModelFace(vertexIndices, normalIndices));
			}
			if (line.startsWith("f ")) {
				a = Integer.valueOf(line.split(" ")[1]);
				b = Integer.valueOf(line.split(" ")[2]);
				c = Integer.valueOf(line.split(" ")[3]);
				m.addFace(new Face(new Color(r.nextInt()), a, b, c));
			}
		}
		reader.close();		
		return m;
	}
}
