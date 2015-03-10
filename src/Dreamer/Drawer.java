package Dreamer;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

public class Drawer {
	
	static void drawShape(Shape s, Color c, Graphics g, boolean filled) {
		
		if(Line.class.equals(s.getClass())) {
			Line l = (Line)s;
			l = new Line(	
					Camera.translate(l.getX1(), l.getY1(), 0).x,
					Camera.translate(l.getX1(), l.getY1(), 0).y,
					Camera.translate(l.getX2(), l.getY2(), 0).x,
					Camera.translate(l.getX2(), l.getY2(), 0).y
					);
			g.setColor(c);
			g.draw(l);
			
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
			g.setColor(c);
			
			if(filled)
				g.fill(p);
			else
				g.draw(p);
		}
		
		// TODO: unbreak my heart, say you love me forever...
		/*
		// draw enemy vision rectangle
		if (Enemy.class.isAssignableFrom(this.getClass())) {				
			Polygon p = new Polygon();
			Rectangle vision = ((Enemy)this).getVision();			
			int i = vision.getPointCount() - 1;
			while(i >= 0) {
				p.addPoint(
						Camera.translate(vision.getPoint(i)[0], vision.getPoint(i)[1], position.z).x,
						Camera.translate(vision.getPoint(i)[0], vision.getPoint(i)[1], position.z).y
						);
				i--;
			}
			
			if (((Enemy)this).getTarget() == null)
				g.setColor(c);			
			else
				g.setColor(new Color(1f, 0, 0, 1f));
			g.draw(p);
		}
		*/
	}
	
	static void drawShape(Shape s, Color c, Graphics g) {
		drawShape(s, c, g, true);
	}
	
	static void drawCursor(String s, float x, float y, float z, Graphics g) {
		Vector3f v = Camera.translate(x, y, z);
		g.setColor(Library.defaultFontColor);
		g.setFont(Library.defaultFont);
		g.drawString(s, v.x, v.y);
		g.drawLine(v.x - Constants.MARKERSIZE, v.y, v.x + Constants.MARKERSIZE,
				v.y);
		g.drawLine(v.x, v.y - Constants.MARKERSIZE, v.x, v.y
				+ Constants.MARKERSIZE);
	}
}
