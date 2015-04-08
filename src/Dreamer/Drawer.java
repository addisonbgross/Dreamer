package Dreamer;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class Drawer {
	
	static Graphics graphics = new Graphics();
	
	static void drawLine(Color c, float x1, float y1, float z1, float x2, float y2, float z2) {
		Vector3f start = new Vector3f(), end = new Vector3f();
		start = Camera.translate(x1, y1, z1, start);
		end = Camera.translate(x2, y2, z2, end);
		graphics.setColor(c);
		graphics.drawLine(start.x, start.y, end.x, end.y);
	}
	
	static void drawShape(Shape s, Color c, boolean filled) {
		
		if(Line.class.equals(s.getClass())) {
			
		} else {
			Polygon p = new Polygon();
			int i = s.getPointCount() - 1;
			while(i >= 0) {
				p.addPoint(
						Camera.translate(s.getPoint(i)[0], s.getPoint(i)[1], 0).x,
						Camera.translate(s.getPoint(i)[0], s.getPoint(i)[1], 0).y
						);
				i--;
			}
			graphics.setColor(c);
			
			if(filled)
				graphics.fill(p);
			else
				graphics.draw(p);
		}
	}
	
	static void drawShape(Shape s, Color c) {
		drawShape(s, c, true);
	}
	
	static void drawCursor(String s, float x, float y, float z) {
		Vector3f v = Camera.translate(x, y, z);
		graphics.setColor(Library.defaultFontColor);
		graphics.setFont(Library.defaultFont);
		graphics.drawString(s, v.x, v.y);
		graphics.drawLine(v.x - Constants.MARKERSIZE, v.y, v.x + Constants.MARKERSIZE,
				v.y);
		graphics.drawLine(v.x, v.y - Constants.MARKERSIZE, v.x, v.y
				+ Constants.MARKERSIZE);
	}

	public static void setColor(Color c) {
		graphics.setColor(c);
	}

	public static void drawString(String s, float x, float y) {
		graphics.drawString(s, x, y);
	}

	public static void setWorldClip(int i, int j, int screenWidth, int screenHeight) {
		graphics.setWorldClip(new Rectangle(i, j, screenWidth, screenHeight));
	}

	public static void drawEllipse(float x, float y, int i, int j) {
		Ellipse e = new Ellipse(x, y, i, j);
		graphics.draw(e);
	}

	public static void drawImage(Image image, float x, float y) {
		graphics.drawImage(image, x, y);
	}

	public static void setFont(TrueTypeFont ttf) {
		graphics.setFont(ttf);
	}

	public static void setBackground(Color c) {
		graphics.setBackground(c);
	}

	public static void setLineWidth(float w) {
		graphics.setLineWidth(w);
	}
}
