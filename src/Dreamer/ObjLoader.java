package Dreamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class ObjLoader {		
	/**
	 * Scan Obj file and construct faces from vertices and materials. Uses MtlLoader to add colour and
	 * transparency to faces being built
	 * 
	 * @param f		Obj file being read
	 * @param scale relative scale of spaces between vertices
	 * @return ArrayList of Shape3d objects composing model
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ArrayList<Shape3d> loadModel(File f, int scale) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		ArrayList<Shape3d> modelList = new ArrayList<Shape3d>();
		Shape3d m = null;
		Color faceColor = Color.red;
		String line;
		int vertCount = 0; // total vertices in Obj
		int highVert = 0;  // highest vertex in Obj
		int a, b, c;  // sides of Face
		
		// build HashMap of all materials associated with Model
		MtlLoader.loadMaterials(new File(Constants.RESPATH + Constants.MODELPATH + f.getName().replace(".obj", ".mtl")));
		
		line = reader.readLine();
		while (line != null) {
			float x = 0, 
				  y = 0, 
				  z = 0;
			
			// start of new object within Obj
			if (line.startsWith("o ")) {
				if (m != null)
					modelList.add(m);
				m = new Shape3d();
				
				highVert = vertCount;
			}
			
			// apply material to current face (from MtlLoader)
			if (line.startsWith("usemtl "))
				faceColor = MtlLoader.getColor(line.substring("usemtl ".length(), line.length()));
			
			if (line.startsWith("v ")) {
				x = Float.valueOf(line.split(" ")[1]) * scale;
				y = Float.valueOf(line.split(" ")[2]) * scale;
				z = Float.valueOf(line.split(" ")[3]) * scale;
				m.addVertex(x, y, z);
				++vertCount;
			} 			
			
			// construct new face from vertices
			if (line.startsWith("f ")) {
				a = Integer.valueOf(line.split(" ")[1]) - 1;
				b = Integer.valueOf(line.split(" ")[2]) - 1;
				c = Integer.valueOf(line.split(" ")[3]) - 1;
				Face face = new Face(faceColor, c - highVert, b - highVert, a - highVert);
				m.addFace(face);
			}
			line = reader.readLine();
		}
		modelList.add(m);
		reader.close();		
		return modelList;
	}

	/**
	 * Scan through Obj file to detect any objects named "Lamp"
	 * 
	 * @param f     Obj file being read
	 * @param scale relative scale of spaces between vertices
	 * @param xPos  x origin of model
	 * @param yPos  y origin of model
	 * @param zPos  z origin of model
	 * @return ArrayList of all points where Lamp objects should be placed
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	static public ArrayList<Vector3f> loadLights(File f, int scale, int xPos, int yPos, int zPos) throws FileNotFoundException, IOException {
		ArrayList<Vector3f> lightPoints = new ArrayList<Vector3f>();
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line;
		boolean lampNext = false;  // indicates that next line in Obj file will be the coords of a new light
	
		line = reader.readLine();
		while (line != null) {
			float x = 0,
				  y = 0,
				  z = 0;
			
			if (line.startsWith("v ") && lampNext) {
				x = Float.valueOf(line.split(" ")[1]) * scale;
				y = Float.valueOf(line.split(" ")[2]) * scale;
				z = Float.valueOf(line.split(" ")[3]) * scale;
			}
			
			if (line.contains("Lamp")) {
				lampNext = true;
			} else if (lampNext) {
				lightPoints.add(new Vector3f(x + xPos, y + yPos, z + zPos));
				lampNext = false;
			}
			line = reader.readLine();
		}
		
		reader.close();
		return lightPoints;
	}
}
