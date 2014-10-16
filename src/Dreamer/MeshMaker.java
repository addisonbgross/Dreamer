package Dreamer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

public class MeshMaker {	
	static int SPACE = 200, YSPACE = 200, width, height, xMeshes, yMeshes;
	static Image elevationMap, colorMap;
	static Color c = new Color(0, 0, 0);
	static boolean xFlipped;
	
	static void makeMesh(Image heightMap, Image colorMap, boolean xFlipped, int initX, int initY, int initZ) {
		if(heightMap.getWidth() != colorMap.getWidth() || heightMap.getHeight() != colorMap.getHeight()) {
			System.err.println("MeshMaker.java: colorMap size does not equal heightMap size!");
		}
		//Shape3d mesh= new Shape3d(0, -300, 100);
		Shape3d mesh = new Shape3d(initX, initY, initZ);
		for (int x=0; x < (width = heightMap.getWidth()); ++x)
			for (int z=0; z < (height = heightMap.getHeight()); ++z) {
				c = heightMap.getColor(x, z);
				
				int xPos;
				if(xFlipped)
					xPos = (width /2 - x) * SPACE;
				else
					xPos = (x - width /2) * SPACE;
				
				// TODO Make this vertex assignment nicer, its so magic, such magic
				mesh.addVertex(xPos, (xPos / (float) 10000  + c.r + c.g + c.b - ((float)z / 8)) * YSPACE, (z - height/2) * SPACE);
			}
		for (int x=0; x < width - 1; ++x)
			for (int z=0; z < height - 1; ++z) {
				int bottomLeft = x*height + z, 
					topLeft = x*height + z + 1, 
					topRight = (x + 1)*height + z + 1,
					bottomRight = (x + 1)*height + z;
				
				Face f = new Face();
				if(xFlipped) {
					f.setVertices(bottomLeft, topLeft, topRight, bottomRight);
					f.setColors(colorMap.getColor(x, z), colorMap.getColor(x, z+1), colorMap.getColor(x+1, z+1), colorMap.getColor(x+1, z));
				} else {
					f.setVertices(bottomLeft, bottomRight, topRight, topLeft);
					f.setColors(colorMap.getColor(x, z), colorMap.getColor(x+1, z), colorMap.getColor(x+1, z+1), colorMap.getColor(x, z+1));
				}
				
				f.triangulate();
				mesh.addFace(f);
			}
		 mesh.generateMotionTracks();
		 
		 Map.meshList.add(mesh); // This is just a master list for addressing individual meshes
		 mesh.add();
	}
}
