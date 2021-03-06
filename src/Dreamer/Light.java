package Dreamer;

import interfaces.*;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Polygon;

abstract public class Light extends Positionable {
	
	private static final long serialVersionUID = 15616709052981486L;	
	static public ArrayList<Light> lightList = new ArrayList<Light>();
	static boolean firstLight = true;
	static Random r = new Random();
	Color color = new Color(127, 127, 127);
	float range = Constants.LIGHTDISTANCE;
	float orthogonality = 0;
	boolean ambient = false;
	float distance;
	
	// for calculations in light(Face f)
	static Vector3f direction  = new Vector3f(), tempV3f = new Vector3f(), tempV3f2  = new Vector3f();
	static float lightAmount, faceOrthogonality;
	static int accumulate;
	
	@Override
	public void add() {
		
		super.add();
		lightList.add(this);
	}
	
	@Override
	public void remove() {
		
		super.remove();
		lightList.remove(this);
	}
	
	void flicker() { range = r.nextInt(200) + 800; }
	
	static void clearAll() { lightList.clear(); }
	
	static void light (Drawable e) {
		
		if(e instanceof Lightable) {
			firstLight = true;
			for(Light l: lightList) {
				if(l.isVisible()) {
					((Lightable) e).light(l);
					firstLight = false;
				}
			}
		}
	}
	
	boolean isVisible() {
	
		return ambient? true : Camera.isPointVisible(getX(), getY(), getZ()); 
	}
	
	void light(Face f) {
		// starts from black during first round of lighting,
		// then progressively adds to the color
		accumulate = (Light.firstLight == false) ? 1 : 0;
		
		tempV3f = f.masterShape.getTranslatedNormal(f, tempV3f);
		
		if(tempV3f.z > -0.3f) { // culls faces pointing away?
			for (int i = 0; i < f.vertexIndex.length; i++) {
				// get the current vertex
				tempV3f2 = f.masterShape.getTranslatedVertex(f.vertexIndex[i], tempV3f2);
				
				// find direction of light to vertex
				direction = Vector3f.sub(getPosition3f(), tempV3f2, null)
						.normalise(null);
				// product of direction of light and surface normal
				faceOrthogonality = orthogonality * Vector3f.dot(direction, tempV3f);
				// calculate light based on distance
				if (ambient)
					lightAmount = 1;
				else
					lightAmount = Vector.getManhattanDistance(tempV3f2, getPosition3f());
				if (lightAmount < range)
					lightAmount = 1 - lightAmount / range;
				else
					lightAmount = 0;
				try {
					f.vertexColor[i] = new Color(f.vertexColor[i].r
							* accumulate + f.faceColor[i].r * lightAmount
							* (1 + faceOrthogonality) * color.r,
							f.vertexColor[i].g * accumulate + f.faceColor[i].g
									* lightAmount * (1 + faceOrthogonality) * color.g,
							f.vertexColor[i].b * accumulate + f.faceColor[i].b
									* lightAmount * (1 + faceOrthogonality) * color.b,
							f.vertexColor[i].a);
				} catch (ArrayIndexOutOfBoundsException e) {
					// e.printStackTrace();
					// this should not happen. but it does?
				}
			}
		}
	}
}

final class MouseLight extends Light implements Updateable {
	
	private static final long serialVersionUID = 7882373801114220812L;
	
	{
		color = new Color(127, 191, 255);
		range = 1000;
		orthogonality = 1f;
	}
	
	public MouseLight() { trackMouse(); }
	
	public void update() { trackMouse(); }
	
	public void trackMouse() { 
		
		setPosition(Camera.translateMouse(Mouse.getX(), Mouse.getY(), 0)); 
	}
}

final class LampLight extends Light implements Updateable {

	private static final long serialVersionUID = 3188322324141898573L;
	Shape3d s;
	float flickerRange;
	
	{
		orthogonality = 1f;
		color = new Color(255,255, 255);
		flickerRange = 600;
	}
	
	public LampLight(Shape3d shape) {
		setPosition(shape.getPosition3f());
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
	
	private static final long serialVersionUID = -50582640841286336L;
	{
		orthogonality = 0.9f;
		color = new Color(240, 240, 240);	// Original brightness (185, 175, 170);
		ambient = true;
	}
	SunLight(float x, float y, float z) {
		setPosition(x, y, z);
	}
}

final class SunsetLight extends Light {
	
	private static final long serialVersionUID = -8061974024074695263L;
	{
		orthogonality = 0.8f;
		color = new Color(200, 100, 100);
		ambient = true;
	}
	SunsetLight(float x, float y, float z) {
		setPosition(x, y, z);
	}
}

final class FlareLight extends Light {
	
	private static final long serialVersionUID = -4870682254855682693L;
	float flickerRange;
	{
		orthogonality = 0.8f;
		color = new Color(255, 200, 200);
		ambient = true;
	}
	FlareLight(float x, float y, float z) {
		setPosition(x, y, z);
	}
	
	public void update() {
		range = flickerRange + 1000;
		range = r.nextInt((int)range / 5) + range; 
	}
}

class Sunset extends Sun {
	
	private static final long serialVersionUID = -3652185159824163938L;
	Sunset() {
		super();
		light = new SunsetLight(-2000, 100, 100);
		
		outerSunColor = new Color(255, 100, 63, 63);
		middleSunColor = new Color(255, 100, 31, 127);
		innerSunColor = new Color(255, 100, 0, 191);
	}
}

class Sun extends Background {
	
	private static final long serialVersionUID = 8144659238106126455L;
	Polygon outer = new Polygon();
	Polygon middle = new Polygon();
	Polygon inner = new Polygon();
	float innerAngularMotion, middleAngularMotion, outerAngularMotion;
	float outerRadius = 80, middleRadius = 65, innerRadius = 50;
	int numOuterSpikes = 48, numMiddleSpikes = 36, numInnerSpikes = 24;
	int spiky = 10;
	
	Color outerSunColor = new Color(255, 255, 63, 63);
	Color middleSunColor = new Color(255, 255, 31, 127);
	Color innerSunColor = new Color(255, 255, 0, 191);
	
	Light light = new SunLight(-8000, 2000, 2000);

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
	public void add() {
		
		super.add();
		light.add();
	}
	
	public void update() { Manager.activeDrawingSet.add(this); }
	
	public void remove() {
		
		super.remove();
		light.remove();
	}
	
	@Override
	public void draw() {
		// TODO make the sun 3d
	}
}