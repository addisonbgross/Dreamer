package Dreamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.newdawn.slick.Color;

public class ObjLoader {	
	public static ArrayList<Shape3d> loadModel(File f, int scale) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));;
		ArrayList<Shape3d> modelList = new ArrayList<Shape3d>();
		Shape3d m = null;
		Color faceColor = Color.red;
		String line;
		int vertCount = 0;
		int highVert = 0;
		int a, b, c;
		
		MtlLoader.loadMaterials(new File(Constants.RESPATH + Constants.MODELPATH + f.getName().replace(".obj", ".mtl")));
		
		line = reader.readLine();
		while (line != null) {
			if (line.startsWith("o ")) {
				if (m != null)
					modelList.add(m);
				m = new Shape3d();
				highVert = vertCount;
			}
			
			if (line.startsWith("usemtl "))
				faceColor = MtlLoader.getColor(line.substring("usemtl ".length(), line.length()));
			
			if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]) * scale;
				float y = Float.valueOf(line.split(" ")[2]) * scale;
				float z = Float.valueOf(line.split(" ")[3]) * scale;
				m.addVertex(x, y, z);
				++vertCount;
			} 
			
			if (line.startsWith("f ")) {
				a = Integer.valueOf(line.split(" ")[1]) - 1;
				b = Integer.valueOf(line.split(" ")[2]) - 1;
				c = Integer.valueOf(line.split(" ")[3]) - 1;
				m.addFace(new Face(faceColor, c - highVert, b - highVert, a - highVert));
			}
			line = reader.readLine();
		}
		modelList.add(m);
		reader.close();		
		return modelList;
	}
}
