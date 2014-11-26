package Dreamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.newdawn.slick.Color;

public class ObjLoader {
	public static Shape3d loadModel(File f) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Shape3d m = new Shape3d();
		String line;
		int a, b, c;
		
		line = reader.readLine();
		while (line != null) {
			if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]) * 100;
				float y = Float.valueOf(line.split(" ")[2]) * 100;
				float z = Float.valueOf(line.split(" ")[3]) * 100;
				m.addVertex(x, y, z);
			} 
			if (line.startsWith("f ")) {
				a = Integer.valueOf(line.split(" ")[1]) - 1;
				b = Integer.valueOf(line.split(" ")[2]) - 1;
				c = Integer.valueOf(line.split(" ")[3]) - 1;
				// reversed the order of these as so to wind the 
				//   faces in the correct orientation (normals).
				m.addFace(new Face(Color.darkGray, c, b, a));
			}
			line = reader.readLine();
		}
		reader.close();		
		return m;
	}
}
