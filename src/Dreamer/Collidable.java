package Dreamer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

class Collidable extends Positionable {	
	
	private static final long serialVersionUID = -5571044266659765974L;
	static boolean collision = false;
	static Shape lookahead;
	static float xVelTemp, yVelTemp, xIncTemp, yIncTemp;
	static int watchDog;
	static Vector2f suggestedPosition = new Vector2f();
	static Vector2f suggestedVelocity = new Vector2f();
	static Line suggestedTrajectory = null;
	static Vector3f temp = new Vector3f();
	
	private Shape collisionShape;
	static Set<Element> ySet = new HashSet<Element>();
	static Set<Element> xSet = new HashSet<Element>();
	protected static ElementMap<Float, HashSet<Element>> yRange = new ElementMap<Float, HashSet<Element>>();
	protected static ElementMap<Float, HashSet<Element>> xRange = new ElementMap<Float, HashSet<Element>>();
	
	Collidable() {}
	Collidable(Shape s) { setCollisionShape(s); }

	@Override
	void add() {
		super.add();
		xRange.add(getMinX(), this);
		yRange.add(getMinY(), this);
		for (float offset = getWidth(); offset >= 0; offset -= Constants.COLLISIONINTERVAL)
			xRange.add(getMinX() + offset, this);
		for (float offset = getHeight(); offset >= 0; offset -= Constants.COLLISIONINTERVAL)
			yRange.add(getMinY() + offset, this);
	}
	
	@Override
	void remove() {
		super.remove();
		xRange.remove(getMinX(), this);
		yRange.remove(getMinY(), this);
		for (float offset = getWidth(); offset >= 0; offset -= Constants.COLLISIONINTERVAL)
			xRange.remove(getMinX() + offset, this);
		for (float offset = getHeight(); offset >= 0; offset -= Constants.COLLISIONINTERVAL)
			yRange.remove(getMinY() + offset, this);
	}
	
	@Override
	void draw() {
		
		if(Element.debug && collisionShape != null) {	
			try {
				Drawer.drawShape(collisionShape, Color.yellow, false);
			} catch(java.lang.IndexOutOfBoundsException e) {
				e.printStackTrace();
				print();
			}	
			
			// draw Enemy vision
			if (this instanceof Enemy) {
				try {
					Drawer.drawShape(((Enemy)this).getVision(), 
							(((Enemy)this).getTarget() == null) ? Color.white : Color.red, false);
				} catch(java.lang.IndexOutOfBoundsException e) {
					e.printStackTrace();
					print();
				}	
			}
		}
	}	

	boolean collide(Actor a) {
	
		watchDog = 0;
		//TODO this whole function is rickety
		//maybe the actor should pass it's values and get a point and a boolean back or something
		xVelTemp = suggestedTrajectory.getDX();
		yVelTemp = suggestedTrajectory.getDY();
		xIncTemp = -xVelTemp / Constants.ADJUSTSTEPS;
		yIncTemp = -yVelTemp / Constants.ADJUSTSTEPS;
		lookahead = a.getCollisionShape();
		
		//check if the figure is on the ground
		lookahead.setLocation(a.getMinX(), a.getMinY() - 1);
		//adjust status and stuff if it is
		if(collisionShape.intersects(lookahead))
		{
			if(yVelTemp < 1) {
				yVelTemp = 0;
				a.removeStatus(Status.JUMPING);
			}
			a.addStatus(Status.GROUNDED);
			xVelTemp *= 1 - Constants.TERRAINFRICTION;
			yVelTemp *= 1 - Constants.AIRFRICTION;
		}
		
		lookahead.setLocation(a.getMinX() - 1, a.getMinY() + 1);
		if(collisionShape.intersects(lookahead))
		{
			if(xVelTemp < 0) 
				xVelTemp = 0;
			xIncTemp += 1;
		} else
		
		lookahead.setLocation(a.getMinX() + 1, a.getMinY() + 1);
		if(collisionShape.intersects(lookahead))
		{
			if(xVelTemp > 0) 
				xVelTemp = 0;
			xIncTemp -= 1;
		} else
		
		lookahead.setLocation(a.getMinX() + xVelTemp,a.getMinY() + yVelTemp);
		
		while(collisionShape.intersects(lookahead) && (watchDog < Constants.MAXTEST)) 
		{
			xVelTemp += xIncTemp;
			yVelTemp += yIncTemp;
			watchDog++;
			collision = true;
			lookahead.setLocation(a.getMinX() + xVelTemp, a.getMinY() + yVelTemp);
		}
		suggestedVelocity.set(xVelTemp, yVelTemp);
		temp = new Vector3f(
				a.getCenterBottom().x + suggestedVelocity.x,
				a.getCenterBottom().y + suggestedVelocity.y,
				0
				);
				
		suggestedPosition.set(temp.x, temp.y);
		suggestedTrajectory.set(
				a.getCenterBottom().x,
				a.getCenterBottom().y,
				suggestedPosition.x,
				suggestedPosition.y
				);
		//suggestedVelocity.scale(1 - Constants.TERRAINFRICTION);
		//restore lookahead(it is the Actor's collision box after all)
		lookahead.setLocation(a.getMinX(), a.getMinY());
		return collision;
	}
	
	public Shape setCollisionShape(Shape s) {
	
		if(s != null) {
			setPosition(s.getMinX(), s.getMinY(), getZ());
			setWidth(s.getWidth());
			setHeight(s.getHeight());
		}
		collisionShape = s;
		return s;
	}
	
	public Shape getCollisionShape() { return collisionShape; }

	/**
	 * getActiveWithin: returns the active set bound within a rectangle of Shape
	 * width and height
	 * 
	 * @param <T>
	 * 
	 * @param s
	 *            Shape x and y extremes
	 * @return Set of elements within a given bounds
	 */
	static Set<Element> getActiveWithin(Shape s) {
		
		Set<Map.Entry<Float, HashSet<Element>>> temp = new HashSet<Map.Entry<Float, HashSet<Element>>>();
		Set<Element> tempActive = new HashSet<Element>();
	
		// take all set of elements in the camera scene x range
		temp.addAll(xRange.subMap(s.getMinX(), true, s.getMaxX(), true)
				.entrySet());
	
		// add each set together in xSet
		for (Map.Entry<Float, HashSet<Element>> entry : temp) {
			xSet.addAll(entry.getValue());
		}
	
		temp.clear();
	
		// take all the elements in scene y range
		temp.addAll(yRange.subMap(s.getMinY(), true, s.getMaxY(), true)
				.entrySet());
	
		// add them together in ySet
		for (Map.Entry<Float, HashSet<Element>> entry : temp) {
			ySet.addAll(entry.getValue());
		}
	
		// if an element is in both x and y sets then draw it, make it active,
		// this has the effect of rendering and activating only the elements
		// that are within the camera scene boundaries
		for (Element o : xSet) {
			if (ySet.contains(o)) {
				tempActive.add(o);
			}
		}
		
		xSet.clear();
		ySet.clear();
		
		return tempActive;
	}
	
	public static void clear() {

		xRange.clear();
		yRange.clear();
		xSet.clear();
		ySet.clear();
	}
}

class CollisionComparator implements Comparator<Collidable> {

	Actor actor;
	CollisionComparator(Actor a) {actor = a;}
	
	public int compare(Collidable a, Collidable b) {
		if(a.findDistanceTo(actor) < b.findDistanceTo(actor))
			return 1;
		else if(a.findDistanceTo(actor) > b.findDistanceTo(actor))
			return -1;
		return 0;
	}
}






















