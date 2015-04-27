package Dreamer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class Face implements java.io.Serializable {
	
	private static final long serialVersionUID = -7030358503083299426L;
	private static ArrayList<Face> drawList = new ArrayList<Face>(5000); 
	private static TreeSet<Face> texturedDrawList = new  TreeSet<Face>(new FaceTextureComparator());
		
	ArrayList<int[]> subTriangleVertexIndex;
	ArrayList<int[]> subTriangleColorIndex;
	int[] vertexIndex;
	
	Vector2f[] texturePoints;
	Color[] vertexColor; //the color of the vertex at any point in time
	Color[] faceColor; //the ideal unlit faceColor
	String textureName = "";
	transient Texture texture;
	Vector3f normal;
	Shape3d masterShape; //the shape this Face belongs to
	
	Vector4f tempV4f = new Vector4f();
	Vector3f tempV3f = new Vector3f();
	Color tempColor;
	
	Face() {}
	Face (Color c, int... i) {
		setVertices(i);
		//base color of the face
		setColor(c);
		triangulate();
	}
	Face (String s, Color c, int... i) {
		//if not a quad may throw errors
		this(c, i);
		textureName = s;
		if(!textureName.equals(""))
			texture = Library.getTexture(textureName);
		setTexturePoints(0, 0, 1, 1);
	}
	
	public void addToDrawList() {
		if(texture == null)
			drawList.add(this);
		else
			texturedDrawList.add(this);
	}
	
	static int[] triangleIndex;
	static int[] colorIndex;
	
	public void draw() {
		for(int i = 0; i < subTriangleVertexIndex.size(); i++) {
			triangleIndex = subTriangleVertexIndex.get(i);
			colorIndex =  subTriangleColorIndex.get(i);
				for(int j = 0; j < 3; j++) {
					//fading stuff could go here?
					OpenGL.color4f(vertexColor[colorIndex[j]]);
					if(texture != null)
						OpenGL.texCoord2f(texturePoints[colorIndex[j]]);
					tempV3f = getVertex(triangleIndex[j], tempV3f);
					Camera.translate(tempV3f, tempV3f);
					OpenGL.vertex3f(tempV3f);		
				}
		} 
	}
	
	public void iterateTriangles() {
		for(int i = 0; i < subTriangleVertexIndex.size(); i++) {
			triangleIndex = subTriangleVertexIndex.get(i);
			colorIndex =  subTriangleColorIndex.get(i);
				for(int j = 0; j < 3; j++) {
					
				}
		} 	
	}
	
	public void calculateNormal() {
		normal = Vector.crossNormalized(
				Vector3f.sub(masterShape.vertices.get(vertexIndex[0]),
						masterShape.vertices.get(vertexIndex[1]), null),
				Vector3f.sub(masterShape.vertices.get(vertexIndex[2]),
						masterShape.vertices.get(vertexIndex[1]), null));
	}
	
	private int index, start, end;
	
	public void drawWireFrame() {
		
		for(start = 0; start < vertexIndex.length;  start++) {
			
			index = vertexIndex[start];
			tempV3f = getVertex(index, tempV3f);
			
			Camera.translate(tempV3f, tempV3f);
			OpenGL.color4f(vertexColor[start]);
			OpenGL.vertex3f(tempV3f);
			
			end = (start + 1) % vertexIndex.length;
			index = vertexIndex[end];
			tempV3f = getVertex(index, tempV3f);
			
			Camera.translate(tempV3f, tempV3f);
			OpenGL.color4f(vertexColor[end]);
			OpenGL.vertex3f(tempV3f);
		}
	}
	
	//only works for convex polygons
	public void triangulate() {
		subTriangleVertexIndex = new ArrayList<int[]>();
		subTriangleColorIndex = new ArrayList<int[]>();
		
		for(int i = 0; i < (vertexIndex.length - 2); i++) {
			int[] triangleVertexIndex = {vertexIndex[0], vertexIndex[i + 1], vertexIndex[i + 2]};
			int[] triangleColorIndex = {0, i + 1, i + 2};
			subTriangleVertexIndex.add(triangleVertexIndex);
			subTriangleColorIndex.add(triangleColorIndex);
		}
	}
	
	public void setVertices(int... v) {
		vertexIndex = new int[v.length];
		int iter = 0;
		for(int i: v) {
			vertexIndex[iter] = i;
			iter++;
		}
	}
	
	public void setColor(Color desired) {
		vertexColor = new Color[vertexIndex.length];
		faceColor = new Color[vertexIndex.length];
		for(int i = 0; i < vertexIndex.length; i++) {
			vertexColor[i] = new Color(desired.r, desired.g, desired.b, desired.a);
			faceColor[i] =new Color(desired.r, desired.g, desired.b, desired.a);
		}
	}
	
	public void setColors(Color... desired) {
		vertexColor = new Color[vertexIndex.length];
		faceColor = new Color[vertexIndex.length];
		for(int i = 0; i < vertexIndex.length; i++) {
			vertexColor[i] = new Color(desired[i].r, desired[i].g, desired[i].b, desired[i].a);
			faceColor[i] = new Color(desired[i].r, desired[i].g, desired[i].b, desired[i].a);
		}
	}
	
	void setTexturePoints(float u1, float v1, float u2, float v2) {
		texturePoints = new Vector2f[]{
			new Vector2f(u1, v2),
			new Vector2f(u1, v1),
			new Vector2f(u2, v1),
			new Vector2f(u2, v2)
		};
	}
	
	public static void drawFaces() {
		if(Element.debug) {
			
			OpenGL.beginLines();
			for(Face face: texturedDrawList) {
				face.drawWireFrame();
			}
			for(Face face: drawList) {
				face.drawWireFrame();
			}
	 		OpenGL.end();
		
		} else {
			
			OpenGL.draw(texturedDrawList, drawList);
		}
		texturedDrawList.clear();
		drawList.clear();
	}
	
	Vector3f getVertex(int i, Vector3f destination) {
		return masterShape.getTranslatedVertex(i, destination);
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "Face #" + masterShape.faces.indexOf(this) + "\nnormal " + normal.toString();
		for(int i: vertexIndex) {
			s += "\npoint " + i + " " + masterShape.vertices.get(i).toString();
		}
		return s;
	}
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
		in.defaultReadObject();
		if(textureName != "") {
			texture = Library.getTexture(textureName);
			setTexturePoints(0, 0, 1, 1);
		}
	}
}

class FaceTextureComparator implements Comparator<Face> {
	//groups textures by texture ID integer
	public int compare(Face arg0, Face arg1) {
		if(arg1.texture.getTextureID() > arg0.texture.getTextureID())
			return 1;
		else
			return -1;
	}
}
