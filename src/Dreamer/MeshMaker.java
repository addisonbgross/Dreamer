package Dreamer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

public class MeshMaker {	
	public static int XSPACE = 300, YSPACE = 500, width, height, xMeshes, yMeshes;
	static Image elevationMap, colorMap;
	static Color c;
	static Shape3d mesh;
	static boolean xFlipped = false;
	
	static void makeMesh(Image heightMap, Image colorMap, boolean xFlipped, int initX, int initY, int initZ) {
		if(heightMap.getWidth() != colorMap.getWidth() || heightMap.getHeight() != colorMap.getHeight()) {
			System.err.println("MeshMaker.java: colorMap size does not equal heightMap size!");
		}
			
		width = heightMap.getWidth();
		height = heightMap.getHeight();
		
		mesh = new Shape3d(initX, initY, initZ);
		c = new Color(0, 0, 0);
		for (int x = 0; x < width; ++x)
			for (int z = 0; z < height; ++z) {
				c = heightMap.getColor(x, z);
				
				// horizontally flip mesh if required
				int xPos;
				if(xFlipped)
					xPos = (width / 2 - x) * XSPACE;
				else
					xPos = (x - width / 2) * XSPACE;
				
				// add vertex with height relative to colour of pixel [black, white] = [0, MaxHeight]
				mesh.addVertex(xPos, (c.r + c.g + c.b) * YSPACE, (z - height / 2) * XSPACE);
			}
		for (int x = 0; x < width - 1; ++x)
			for (int z = 0; z < height - 1; ++z) {
				int bottomLeft = x * height + z, 
					topLeft = x * height + z + 1, 
					topRight = (x + 1) * height + z + 1,
					bottomRight = (x + 1) * height + z;
				
				Face f = new Face();
				if(xFlipped) {
					f.setVertices(bottomLeft, topLeft, topRight, bottomRight);
					f.setColors(colorMap.getColor(x, z), colorMap.getColor(x, z + 1), colorMap.getColor(x + 1, z + 1), colorMap.getColor(x + 1, z));
				} else {
					f.setVertices(bottomLeft, bottomRight, topRight, topLeft);
					f.setColors(colorMap.getColor(x, z), colorMap.getColor(x + 1, z), colorMap.getColor(x + 1, z + 1), colorMap.getColor(x, z + 1));
				}
				
				f.triangulate();
				mesh.addFace(f);
			}
		
		 mesh.generateMotionTracks();
		 Map.meshList.add(mesh); // This is just a master list for addressing individual meshes
		 mesh.add();
	}
}
