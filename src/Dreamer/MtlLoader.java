package Dreamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.Color;

public class MtlLoader {
	static private HashMap<String, Color> colorMap; // all materials composing Obj model (colour and transparency)
	
	/**
	 * Scan Mtl file for materials and build colourMap
	 * 
	 * @param f Mtl file being read
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void loadMaterials(File f) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line, currentMaterial = "Filler";
		float opacity = 1.0f;	// current transparency level
		float r = 0, g = 0, b = 0;	// current rbg colour
		colorMap = new HashMap<String, Color>(); 
		
		line = reader.readLine();
		while (line != null) {
			if (line.startsWith("newmtl "))
				currentMaterial = line.substring("newmtl ".length(), line.length());
			
			if (line.startsWith("Kd ")) {
				r = Float.valueOf(line.split(" ")[1]);
				g = Float.valueOf(line.split(" ")[2]);
				b = Float.valueOf(line.split(" ")[3]);
			} 
			
			// alpha transparency
			if (line.startsWith("d ")) {
				opacity = Float.valueOf(line.split(" ")[1]);
				colorMap.put(currentMaterial, new Color(r, g, b, opacity));
			}
			
			line = reader.readLine();
		}
		reader.close();		
	}
	
	/**
	 * 
	 * @param name title of material to be referrenced in ObjLoader
	 * @return Slick2d Color
	 */
	public static Color getColor(String name) {
		return colorMap.get(name);
	}
}
