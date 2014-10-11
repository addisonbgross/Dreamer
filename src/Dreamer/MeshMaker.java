package Dreamer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class MeshMaker {	
	static int SPACE = 100, YSPACE = 200, width, height, xMeshes, yMeshes;
	static Image elevationMap, colorMap;
	static Color c = new Color(0, 0, 0);
	static boolean xFlipped;
	static void makeMesh(Image eMap, Image cMap) {
		 makeMesh(eMap, cMap, false);
	}
	static void makeMesh(Image eMap, Image cMap, boolean xFlip) {
		/*
		if(heightMap.getWidth() != colorMap.getWidth() || heightMap.getHeight() != colorMap.getHeight()) {
			System.err.println("MeshMaker.java: colorMap size does not equal heightMap size!");
		}
		*/
		elevationMap = eMap;
		colorMap = cMap;
		xFlipped = xFlip;
		width = elevationMap.getWidth();
		height = elevationMap.getHeight();		
		xMeshes = width / 4 - 1;
		yMeshes = height / 4 - 1;
		for(int i = 0; i < xMeshes; ++i)
			for(int j = 0; j < yMeshes; ++j) {
				subMesh(i, j);
			}
	}
	public static void subMesh(int xM, int yM) {
		Shape3d mesh= new Shape3d(0, 0, 0);
		for (int x=0; x < 4; ++x)
			for (int z=0; z < 4; ++z) {
				c = elevationMap.getColor(x, z);
				int xPos;
				if(xFlipped)
					xPos = -(width * SPACE / 2) + (x + 4 * xM) * SPACE;
				else
					xPos = -(width * SPACE / 2) + (x + 4 * xM) * SPACE;
				int yPos = (int)(c.r + c.g + c.b) * SPACE - 500; 
				int zPos = -(4 * yM + z) * SPACE + 200; 
				mesh.addVertex(xPos, yPos, zPos);
				System.out.println(xPos + " " + zPos);
			}
		for (int x=0; x < 3; ++x)
			for (int z=0; z < 3; ++z) {
				int bottomLeft = (x * 4) + z, 
					topLeft = (x + 1) * 4 + z,
					topRight = (x + 1 ) * 4 + z + 1,
					bottomRight = (x * 4) + z + 1;
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
		 mesh.add();
	}
}
