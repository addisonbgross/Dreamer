package Dreamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.Color;

public class MtlLoader {
	static private HashMap<String, Color> colorMap;
	
	public static void loadMaterials(File f) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line, currentMaterial = "Filler";
		colorMap = new HashMap<String, Color>(); 
		
		line = reader.readLine();
		while (line != null) {
			if (line.startsWith("newmtl "))
				currentMaterial = line.substring("newmtl ".length(), line.length());

			if (line.startsWith("Kd ")) {
				float r = Float.valueOf(line.split(" ")[1]);
				float g = Float.valueOf(line.split(" ")[2]);
				float b = Float.valueOf(line.split(" ")[3]);
				colorMap.put(currentMaterial, new Color(r, g, b));
			} 
			line = reader.readLine();
		}
		reader.close();		
	}
	public static Color getColor(String name) {
		return colorMap.get(name);
	}
}
