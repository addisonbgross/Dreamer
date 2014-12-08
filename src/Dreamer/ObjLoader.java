package Dreamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.newdawn.slick.Color;

public class ObjLoader {	
	public static Shape3d loadModel(File f) throws FileNotFoundException, IOException {
		return loadModel(f, Constants.DEFAULTMODELSCALE);
	}
	public static Shape3d loadModel(File f, int scale) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Shape3d m = new Shape3d();
		Color faceColor = Color.red;
		String line;
		int a, b, c;
		
		MtlLoader.loadMaterials(new File(Constants.RESPATH + Constants.MODELPATH + f.getName().replace(".obj", ".mtl")));
		
		line = reader.readLine();
		while (line != null) {
			if (line.startsWith("usemtl "))
				faceColor = MtlLoader.getColor(line.substring("usemtl ".length(), line.length()));
			
			if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]) * scale;
				float y = Float.valueOf(line.split(" ")[2]) * scale;
				float z = Float.valueOf(line.split(" ")[3]) * scale;
				m.addVertex(x, y, z);
			} 
			if (line.startsWith("f ")) {
				a = Integer.valueOf(line.split(" ")[1]) - 1;
				b = Integer.valueOf(line.split(" ")[2]) - 1;
				c = Integer.valueOf(line.split(" ")[3]) - 1;
				m.addFace(new Face(faceColor, c, b, a));
			}
			line = reader.readLine();
		}
		reader.close();		
		return m;
	}
}
