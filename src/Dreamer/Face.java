package Dreamer;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

public class Face implements java.io.Serializable {
	private static ArrayList<Face> drawList = new ArrayList<Face>(5000); 
	private static TreeSet<Face> texturedDrawList = new  TreeSet<Face>(new FaceTextureComparator());
		
	ArrayList<int[]> subTriangleVertexIndex;
	ArrayList<int[]> subTriangleColorIndex;
	int[] vertexIndex;
	
	Vector2f[] texturePoints;
	Color[] vertexColor; //the color of the vertex at any point in time
	Color[] faceColor; //the ideal unlit faceColor
	Texture texture;
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
	Face (Texture t, Color c, int... i) {
		//if not a quad may throw errors
		this(c, i);
		texture = t;
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
					glColor4f(
							vertexColor[colorIndex[j]].r, 
							vertexColor[colorIndex[j]].g, 
							vertexColor[colorIndex[j]].b, 
							vertexColor[colorIndex[j]].a
							);
					if(texture != null)
						glTexCoord2f(texturePoints[colorIndex[j]].x, texturePoints[colorIndex[j]].y);
					tempV3f = getVertex(triangleIndex[j], tempV3f);
					Camera.translate(tempV3f, tempV3f);
					glVertex3f(
							tempV3f.x,
							tempV3f.y,
							tempV3f.z
							);		
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
			
			drawColoredPoint(tempV3f, vertexColor[start]);
			
			end = (start + 1) % vertexIndex.length;
			index = vertexIndex[end];
			tempV3f = getVertex(index, tempV3f);
			
			drawColoredPoint(tempV3f, vertexColor[end]);
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
	
	private static Vector3f v = new Vector3f();
	private static Color c;
	
	public static void drawColoredPoint(Vector3f position, Color color) {
		v = Camera.translate(position, v);
		c = color;
		glColor4f(c.r, c.g, c.b, c.a);
		glVertex3f(v.x, v.y, v.z);	
	}
	
	public static void drawFaces() {
		if(Element.debug) {
			glBegin(GL11.GL_LINES);
			for(Face face: texturedDrawList) {
				face.drawWireFrame();
			}
			for(Face face: drawList) {
				face.drawWireFrame();
			}
	 		glEnd();
		} else {
			glEnable(GL11.GL_CULL_FACE);
			glEnable(GL_DEPTH_TEST);
	 		
	 		//TODO draw all textured triangles in clusters by texture id (already sorted);
			glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
			glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0);
			
			GL11.glEnable( GL11.GL_TEXTURE );
			for(Face f: texturedDrawList) {
				f.texture.bind();
				glBegin(GL_TRIANGLES);
				f.draw();
				glEnd();
			}
			GL11.glDisable(GL11.GL_TEXTURE);
			
			//draw coloured non-textured triangles
			TextureImpl.bindNone();
			GL11.glDisable(GL11.GL_TEXTURE);
			glBegin(GL_TRIANGLES);
			for(Face f: drawList)
				f.draw();
	 		glEnd();
	 		
			glDisable(GL_DEPTH_TEST);
			glDisable(GL11.GL_CULL_FACE);
		}
		texturedDrawList.clear();
		drawList.clear();
	}
	
	Vector3f getVertex(int i, Vector3f destination) {
		return masterShape.getTranslatedVertex(i, destination);
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
