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

//TODO sort triangles by texture for proper rendering, textures
public class Triangle3d {
	static ArrayList<Triangle3d> triangleList = new ArrayList<Triangle3d>(10000);
	static TreeSet<Triangle3d> texturedTriangles = new  TreeSet<Triangle3d>(new TextureComparator());
	Vector3f[] points = new Vector3f[3];
	Vector4f[] colors = new Vector4f[3];
	Texture texture = null;
	Vector2f[] texturePoints;
	
	static void drawTriangles() {
		int i;
		if(Element.debug) {
			glBegin(GL11.GL_LINES);
			for(Triangle3d t: texturedTriangles) 
				for(i =0; i < 3; i++) {
					glColor4f(t.colors[i].x, t.colors[i].y, t.colors[i].z, t.colors[i].w);
					glVertex3f(t.points[i].x, t.points[i].y, t.points[i].z);	
					int end = (i + 1) % 3;
					glColor4f(t.colors[end].x, t.colors[end].y, t.colors[end].z, t.colors[end].w);
					glVertex3f(t.points[end].x, t.points[end].y, t.points[end].z);	
				}
			for(Triangle3d t: triangleList) 
				for(i =0; i < 3; i++) {
					glColor4f(t.colors[i].x, t.colors[i].y, t.colors[i].z, t.colors[i].w);
					glVertex3f(t.points[i].x, t.points[i].y, t.points[i].z);	
					int end = (i + 1) % 3;
					glColor4f(t.colors[end].x, t.colors[end].y, t.colors[end].z, t.colors[end].w);
					glVertex3f(t.points[end].x, t.points[end].y, t.points[end].z);	
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
			for(Triangle3d t: texturedTriangles) {
				t.texture.bind(); // or glBind(texture.getTextureID());
				glBegin(GL_TRIANGLES);
				for(i =0; i < 3; i++) {
					glColor4f(t.colors[i].x, t.colors[i].y, t.colors[i].z, t.colors[i].w);
					glTexCoord2f(t.texturePoints[i].x, t.texturePoints[i].y);
					glVertex3f(t.points[i].x, t.points[i].y, t.points[i].z);	
				}
		 		glEnd();
		 		t = null;
			}
			GL11.glDisable(GL11.GL_TEXTURE);
			
			//draw coloured non-textured triangles
			TextureImpl.bindNone();
			new Color(255, 255, 255, 255).bind();
			GL11.glDisable(GL11.GL_TEXTURE);
			glBegin(GL_TRIANGLES);
			for(Triangle3d t: triangleList) {
				for(i = 0; i < 3; i++) {
					glColor4f(t.colors[i].x, t.colors[i].y, t.colors[i].z, t.colors[i].w);
					glVertex3f(t.points[i].x, t.points[i].y, t.points[i].z);		
				}
				t = null;
			} 
	 		glEnd();
	 		
			glDisable(GL_DEPTH_TEST);
			glDisable(GL11.GL_CULL_FACE);
		}
		triangleList.clear();
		texturedTriangles.clear();
	}
}
class TextureComparator implements Comparator<Triangle3d> {
	//groups textures by texture ID integer
	public int compare(Triangle3d arg0, Triangle3d arg1) {
		if(arg1.texture.getTextureID() > arg0.texture.getTextureID())
			return 1;
		else
			return -1;
	}
}