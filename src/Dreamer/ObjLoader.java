package Dreamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.Color;
//does not yet work
public class ObjLoader {
	public static Shape3d loadModel(File f) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Shape3d m = new Shape3d();
		String line;
		int a, b, c;
		ArrayList<Integer> vertStart = new ArrayList<Integer>();
		ArrayList<Integer> faceStart = new ArrayList<Integer>();
		
		int mark = 0;
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
			
			++mark;
			if (line.startsWith("o ")) {
				vertStart.add(mark);
				System.out.println("Obj start: " + mark);
			} else if (line.startsWith("s ")) {
				faceStart.add(mark);
				System.out.println("Face start: " + mark);
			}
			line = reader.readLine();
		}
		reader.close();		
		return m;
	}
}
