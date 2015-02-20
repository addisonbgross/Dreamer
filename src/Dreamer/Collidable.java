package Dreamer;

import java.util.Comparator;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

class Collidable extends Element {	
	static boolean collision = false;
	static Shape lookahead;
	static float xVelTemp, yVelTemp, xIncTemp, yIncTemp;
	static int watchDog;
	static Vector2f suggestedPosition = new Vector2f();
	static Vector2f suggestedVelocity = new Vector2f();
	static Line suggestedTrajectory = null;
	
	private Shape collisionShape;
	
	Collidable() {}
	Collidable(Shape s) 
	{
		setCollisionShape(s);
	}

	///////////// HORRID MESS
	@Override
	void setCenterBottom(float x, float y) {
		removeCollision();
		super.setCenterBottom(x, y);
		addCollision();
	}
	void addCollision() {
		add();
	}
	void removeCollision() {
		remove();
	}
	@Override
	void draw(Graphics g) {
		if(Element.debug && collisionShape != null) {	
			try {
				drawShape(collisionShape, Color.yellow, g, false);
			} catch(java.lang.IndexOutOfBoundsException e) {
				e.printStackTrace();
				print();
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
				a.removeStatus("jumping");
			}
			a.addStatus("grounded");
			xVelTemp *= 1 - Constants.TERRAINFRICTION;
			yVelTemp *= 1 - Constants.AIRFRICTION;
		}
		
		lookahead.setLocation(a.getMinX() - 1, a.getMinY() + 1);
		if(collisionShape.intersects(lookahead))
		{
			a.addStatus("boundedLeft");
			if(xVelTemp < 0) 
				xVelTemp = 0;
			xIncTemp += 1;
		} else
			a.removeStatus("boundedLeft");
		
		lookahead.setLocation(a.getMinX() + 1, a.getMinY() + 1);
		if(collisionShape.intersects(lookahead))
		{
			a.addStatus("boundedRight");
			if(xVelTemp > 0) 
				xVelTemp = 0;
			xIncTemp -= 1;
		} else
			a.removeStatus("boundedRight");
		
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
		suggestedTrajectory.set(a.getCenterBottom(), a.getCenterBottom().copy().add(suggestedVelocity));
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
	public Shape getCollisionShape() {
		return collisionShape;
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






















