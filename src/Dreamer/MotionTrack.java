package Dreamer;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class MotionTrack extends Collidable {
	Line track;
	Vector2f normal;
	Vector2f left, right;
	
	MotionTrack() {}
	MotionTrack(float sx, float sy, float ex, float ey) {
		track = new Line(sx, sy, ex, ey);
		setCollisionShape(track);
		normal = new Vector2f(-track.getNormal(0)[0], -track.getNormal(0)[1]);
		right = new Vector2f(
				track.getDX() / track.length(),
				track.getDY() / track.length()
				);
		left = new Vector2f(
				-track.getDX() / track.length(),
				-track.getDY() / track.length()
				);
	};
	
	@Override 
	boolean collide(Actor a) {
		if(a.getVelocityVector().dot(normal) < 0)
			if(suggestedTrajectory.intersect(track, true, suggestedPosition)) {
				
				if(a.xVel > 0) {
					suggestedVelocity.projectOntoUnit(right, suggestedVelocity);
				} else {
					suggestedVelocity.projectOntoUnit(left, suggestedVelocity); 
				}
				suggestedVelocity.scale(
						1 - Constants.AIRFRICTION - Constants.GROUNDFRICTION * Math.abs(normal.dot(a.getVelocityVector().copy().normalise()))
						);
				if(suggestedVelocity.length() < Constants.STATICFRICTION)
					suggestedVelocity.set(0, 0);
				suggestedPosition.y += 0.1f;
				suggestedTrajectory.set(suggestedTrajectory.getStart(), suggestedPosition.copy().add(suggestedVelocity));
				a.addStatus("grounded");
				a.removeStatus("jumping");
				return true;
			}
		return false;
	}
	@Override 
	void draw(Graphics g) {
		super.draw(g);
		if(Element.debug)
			Drawer.drawShape(
					new Line(
							track.getCenterX(),
							track.getCenterY(),
							track.getCenterX() + 20 * normal.x,
							track.getCenterY() + 20 * normal.y
							),
					Constants.COLLISIONCOLOUR,
					g
					);
	}
	public static void generateMotionTrack(Face f, ArrayList<Vector3f> vertices, Vector3f vector3f) {
		int sides = f.vertexIndex.length;
		Vector4f line, intersectLine;
		Vector2f firstPoint = null;
		
		for(int i = 0; i < sides; i ++) {
			int v1 = f.vertexIndex[i];
			int v2 = f.vertexIndex[(i +  1) % sides];
	
			Vector3f pointA = Vector3f.add(vertices.get(v1), vector3f, null);
			Vector3f pointB = Vector3f.add(vertices.get(v2), vector3f, null);
			//if line intersects the z-plane
			
			if((pointA.z >=  0 && pointB.z <= 0) || (pointB.z >=  0 && pointA.z <= 0) ) {
				//find intersection point
				intersectLine = new Vector4f(pointB.x - pointA.x, pointB.y - pointA.y, pointB.z - pointA.z, 1);
				float t = (intersectLine.z == 0) ? 0: -pointA.z /  intersectLine.z; // avoids NaN errors
				Float x = intersectLine.x * t + pointA.x;
				Float y = intersectLine.y * t + pointA.y;
				if(firstPoint == null)
					firstPoint = new Vector2f(x, y);
				else if(!(Math.abs(firstPoint.x - x) <  1 && Math.abs(firstPoint.y - y) <  1)){
					try {
						//this line is to extend the motiontracks enough so that the travel is seamless
						//TODO move this into the constructor
						line = new Vector4f(x - firstPoint.x, y - firstPoint.y, 0, 0);
						line.normalise();
						//if the direction of the face normal is upwards or downwards...
						//if you do some basic logic reduction on the statement below i will give you a present
						boolean swapPoints = true;
						if(firstPoint.x < x && f.normal.y > 0) {
							swapPoints = false;
						} else if(firstPoint.x > x && f.normal.y > 0) {
							swapPoints = true;
						} else if(firstPoint.y > y && f.normal.x > 0) {
							swapPoints = false;
						}
						if(swapPoints) {
							new MotionTrack(
									x + line.x,
									y + line.y,
									firstPoint.x - line.x,
									firstPoint.y - line.y
									).add();
						} else {
							new MotionTrack(
									firstPoint.x - line.x,
									firstPoint.y - line.y,
									x + line.x,
									y + line.y
									).add();
						}				
					} catch(IllegalStateException e) {
						//invalid vector
					}
				}
			}
		}
	}
}
class LadderTrack extends Collidable {
	LadderTrack(float sx, float sy, float ex, float ey) {
		//super(sx, sy, ex, ey);
		setCollisionShape(new Rectangle(sx, sy, ex - sx, ey - sy));
	}
	
	@Override 
	boolean collide(Actor a) {
		if(true)
			if(getCollisionShape().contains(suggestedTrajectory)) {
				a.addStatus("climbing");
				if(a.checkStatus("up")) {
					if(suggestedPosition.y < getMaxY() - Constants.VEL / 2) {
						suggestedVelocity.y = Constants.VEL / 2;
						suggestedVelocity.x = 0;
					}
				} else if(a.checkStatus("down")) {
					if(suggestedPosition.y > getMinY() + Constants.VEL / 2) {
						suggestedVelocity.y = -Constants.VEL / 2;
						suggestedVelocity.x = 0;
					}
				} else {
					suggestedVelocity.y = 0;
				}
				suggestedTrajectory.set(a.getCenterBottom(), a.getCenterBottom().copy().add(suggestedVelocity));
				a.addStatus("grounded");
				a.removeStatus("jumping");
				return true;
			}
			else {
				a.removeStatus("climbing");
			}
		return false;
	}
}

