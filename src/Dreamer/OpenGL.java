package Dreamer;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

public class OpenGL {

	public static void init() {
	
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		// clear z buffer to 1
		glClearDepth(1);
		
		// enable alpha blending
		glEnable(GL_BLEND);
		// enables depth buffering to draw faces in the appropriate order
		glDepthFunc(GL_LESS);
		glCullFace(GL_FRONT);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glViewport(0, 0, Constants.screenWidth, Constants.screenHeight);
		glMatrixMode(GL_MODELVIEW);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		//NOTE this was the culprit for upside-down drawing
		glOrtho(0, Constants.screenWidth, Constants.screenHeight, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}
	
	public static void clearBuffers() {
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public static void disableDepthTest() { GL11.glDisable(GL11.GL_DEPTH_TEST); }
	
	public static void color4f(Color c) { glColor4f(c.r, c.g, c.b, c.a); }
	
	public static void vertex3f(Vector3f v) { glVertex3f(v.x, v.y, v.z); }
	
	public static void texCoord2f(Vector2f v) { glTexCoord2f(v.x, v.y); }
	
	public static void end() { GL11.glEnd(); }
	
	public static void beginLines() { GL11.glBegin(GL11.GL_LINES); }

	public static void draw(TreeSet<Face> texturedDrawList, ArrayList<Face> drawList) {
		
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
		for(Face f: drawList) {
			f.draw();
		}
 		glEnd();
 		
		glDisable(GL_DEPTH_TEST);
		glDisable(GL11.GL_CULL_FACE);
	}
}
