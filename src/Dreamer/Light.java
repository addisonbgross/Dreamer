package Dreamer;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;

abstract public class Light extends Element {
	static public ArrayList<Light> lightList = new ArrayList<Light>();
	static boolean firstLight = true;
	static Random r = new Random();
	Color color = new Color(127, 127, 127);
	float range = Constants.LIGHTDISTANCE;
	float orthogonality = 0;
	boolean ambient = false;
	
	@Override
	void add() {
		super.add();
		lightList.add(this);
	}
	@Override
	void remove() {
		super.remove();
		lightList.remove(this);
	}
	@Override
	void draw(Graphics g) {
		if(Element.debug) {
			Vector3f v = Camera.translate(getX(), getY(), getZ());
			Ellipse e = new Ellipse(
					v.x, 
					v.y, 
					5,
					5
					);
			g.draw(e);
		}
	}
	// This is used redundantly in the Lamp (Shape3d) class. A way to generalize this is needed
	@Override
	boolean isVisible() {
		float lightSpan = Constants.LIGHTDISTANCE * 1.4f;
		if(ambient)
			return true;
		else if(Camera.isPointVisible(getX(), getY(), getZ()))
			return true;
		else if (Camera.isPointVisible(getX() + lightSpan, getY() + lightSpan, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() + lightSpan, getY() - lightSpan, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() - lightSpan, getY() + lightSpan, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() - lightSpan, getY() - lightSpan, getZ()))
			return true;
		return false;
	}
	void flicker() {
		range = r.nextInt(200) + 800;
	}
	static void clearAll() {
		lightList.clear();
	}
	static void lightActive() {
		firstLight = true;
		for(Light l: lightList) {
			if(l.isVisible()) {
				for(Element e: activeSet)
					if(e instanceof Lightable) {
						((Lightable) e).light(l);
					}
				firstLight = false;
			}
		}
	}
}
final class MouseLight extends Light implements Updateable {
	{
		color = new Color(127, 191, 255);
		range = 1000;
		orthogonality = 1f;
	}
	public MouseLight() {
		trackMouse();
	}
	public void update() {
		trackMouse();
	}
	public void trackMouse() {
		setPosition(Camera.translateMouse(Mouse.getX(), Mouse.getY()));
	}
}
final class LampLight extends Light implements Updateable {
	Shape3d s;
	float flickerRange;
	{
		orthogonality = 1f;
		color = new Color(255,255, 255);
		flickerRange = 600;
	}
	public LampLight(Shape3d shape) {
		setPosition(shape.getPosition4f());
		s = shape;
	}
	public void update() {
		setPosition(s.position.x, s.position.y, s.position.z);
		range = flickerRange + 1000;
		range = r.nextInt((int)range / 5) + range; 
		//flicker();
	}
}
final class SunLight extends Light {
	{
		orthogonality = 0.9f;
		color = new Color(240, 240, 240);	// Original brightness (185, 175, 170);
		ambient = true;
	}
	SunLight(float x, float y, float z) {
		setPosition(x, y, z);
	}
}
final class Sun extends Background {
	Polygon outer = new Polygon();
	Polygon middle = new Polygon();
	Polygon inner = new Polygon();
	float innerAngularMotion, middleAngularMotion, outerAngularMotion;
	float outerRadius = 80, middleRadius = 65, innerRadius = 50;
	int numOuterSpikes = 48, numMiddleSpikes = 36, numInnerSpikes = 24;
	int spiky = 10;
	SunLight light = new SunLight(-8000, 2000, 2000);

	Sun() {
		float angle = 0;
		while(angle < 2 * Math.PI) {
			spiky = -1 * spiky;
			outer.addPoint(
					(outerRadius + spiky)* (float)Math.cos(angle), 
					(outerRadius + spiky)* (float)Math.sin(angle)
					);
			angle += Math.PI / numOuterSpikes;
		}
		angle = 0;
		while(angle < 2 * Math.PI) {
			spiky = -1 * spiky;
			middle.addPoint(
					(middleRadius + spiky)* (float)Math.cos(angle), 
					(middleRadius + spiky)* (float)Math.sin(angle)
					);
			angle += Math.PI / numMiddleSpikes;
		}
		angle = 0;
		while(angle < 2 * Math.PI) {
			spiky = -1 * spiky;
			inner.addPoint(
					(innerRadius + spiky)* (float)Math.cos(angle), 
					(innerRadius + spiky)* (float)Math.sin(angle)
					);
			angle += Math.PI / numInnerSpikes;
		}
	}
	@Override
	void add() {
		super.add();
		light.add();
	}
	public void update() {
		activeSet.add(this);
	}
	public void remove() {
		super.remove();
		light.remove();
	}
	void draw(Graphics g) {
		//PUFF THE MAGIC DRAGON LIVED BY THE 
		GL11.glEnable(org.lwjgl.opengl.GL11.GL_BLEND);
		outer.setLocation(200 - Camera.getCenterX() / 100, 200 - Camera.getCenterY() / 100);
		inner.setLocation(200 - Camera.getCenterX() / 100, 200 - Camera.getCenterY() / 100);
		middle.setLocation(200 - Camera.getCenterX() / 100, 200 - Camera.getCenterY() / 100);
		g.setColor(new Color(255, 255, 63, 63));
		g.fill(outer.transform(Transform.createRotateTransform(outerAngularMotion, outer.getCenterX(), outer.getCenterY())));
		g.setColor(new Color(255, 255, 31, 127));
		g.fill(middle.transform(Transform.createRotateTransform(middleAngularMotion, middle.getCenterX(), middle.getCenterY())));
		g.setColor(new Color(255, 255, 0, 191));
		g.fill(inner.transform(Transform.createRotateTransform(innerAngularMotion, inner.getCenterX(), inner.getCenterY())));
		innerAngularMotion += 0.005;
		middleAngularMotion += 0.003;
		outerAngularMotion += 0.001;
	}
}