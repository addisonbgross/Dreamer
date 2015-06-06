package Dreamer;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import Dreamer.enums.Status;
import Dreamer.interfaces.Updateable;

public class MotionTrack extends Collider {
	
	private static final long serialVersionUID = -4136972279948096275L;
	Line track;
	Vector3f normal;
	static Vector3f temp = new Vector3f();
	Vector2f left, right;
	boolean highlighted = false;
	
	MotionTrack() {}
	
	MotionTrack(float sx, float sy, float ex, float ey) {
	
		temp.set(ex - sx, ey - sy, 0);
		
		try {
			temp.normalise();
		} catch(java.lang.IllegalStateException ise) {
			ise.printStackTrace();
		}
		
		track = new Line(sx - temp.x, sy - temp.y, ex + temp.x, ey + temp.y);
		setCollisionShape(track);
		normal = new Vector3f(-track.getNormal(0)[0], -track.getNormal(0)[1], 0);
		
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
		
		temp.set(a.dynamics.getXVel(), a.dynamics.getYVel());
		
		if(Vector3f.dot(temp, normal) < 0)
			if(suggestedTrajectory.intersect(track, true, suggestedPosition)) {
				
				if(temp.x > 0) {
					suggestedVelocity.projectOntoUnit(right, suggestedVelocity);
				} else {
					suggestedVelocity.projectOntoUnit(left, suggestedVelocity); 
				}
				suggestedVelocity.scale(
						1 
						- Constants.AIRFRICTION 
						- Constants.GROUNDFRICTION 
						* Math.abs(Vector3f.dot(normal, a.dynamics.getVelocity().normalise(null)))
						);
				if(suggestedVelocity.length() < Constants.STATICFRICTION)
					suggestedVelocity.set(0, 0);
				suggestedPosition.y += 0.1f;
				suggestedTrajectory.set(suggestedTrajectory.getStart(), suggestedPosition.copy().add(suggestedVelocity));
				a.addStatus(Status.GROUNDED);
				a.removeStatus(Status.JUMPING);
				return true;
			}
		return false;
	}
	
	public void draw() {
	
		if(isVisible() && getCollisionShape() != null) {
		
			Drawer.drawLine(
				highlighted? Constants.HIGHLIGHTCOLOR : Constants.COLLISIONCOLOUR,
				track.getCenterX(),
				track.getCenterY(),
				0,
				track.getCenterX() + 20 * normal.x,
				track.getCenterY() + 20 * normal.y,
				0
			);	
			
			Drawer.drawLine(
				highlighted? Constants.HIGHLIGHTCOLOR : Constants.COLLISIONCOLOUR,
				track.getX1(),
				track.getY1(),
				0,
				track.getX2(),
				track.getY2(),
				0
			);	
		}
	}
	
	public boolean isVisible() {
		
		return (Manager.trackview || Manager.debug) ?
			Camera.isPointVisible(getX(), getY(), getZ()) : false; 
	}
	
	public static void generateMotionTrack(Face f, ArrayList<Vector3f> vertices, Vector3f vector3f) {
		int sides = f.vertexIndex.length;
		Vector3f intersectLine;
		Vector2f firstPoint = null;
		
		for(int i = 0; i < sides; i ++) {
			int v1 = f.vertexIndex[i];
			int v2 = f.vertexIndex[(i +  1) % sides];
	
			Vector3f pointA = Vector3f.add(vertices.get(v1), vector3f, null);
			Vector3f pointB = Vector3f.add(vertices.get(v2), vector3f, null);
			//if line intersects the z-plane
			
			if((pointA.z >=  0 && pointB.z <= 0) || (pointB.z >=  0 && pointA.z <= 0) ) {
				//find intersection point
				intersectLine = new Vector3f(pointB.x - pointA.x, pointB.y - pointA.y, pointB.z - pointA.z);
				float t = (intersectLine.z == 0) ? 0: -pointA.z /  intersectLine.z; // avoids NaN errors
				Float x = intersectLine.x * t + pointA.x;
				Float y = intersectLine.y * t + pointA.y;
				if(firstPoint == null)
					firstPoint = new Vector2f(x, y);
				else if(!(Math.abs(firstPoint.x - x) <  1 && Math.abs(firstPoint.y - y) <  1)){
					try {
						temp = new Vector3f(x - firstPoint.x, y - firstPoint.y, 0);

						if(Vector3f.cross(temp, f.normal, null).z > 0) {
							new MotionTrack(firstPoint.x, firstPoint.y, x, y).add();
						} else {
							new MotionTrack(x, y, firstPoint.x, firstPoint.y).add();
						} 		
					} catch(IllegalStateException e) {
						//invalid vector
					}
				}
			}
		}
	}
}
class LadderTrack extends Collider {
	
	private static final long serialVersionUID = 4209415364083440118L;

	LadderTrack(float sx, float sy, float ex, float ey) {
		//super(sx, sy, ex, ey);
		setCollisionShape(new Rectangle(sx, sy, ex - sx, ey - sy));
	}
	
	@Override 
	boolean collide(Actor a) {
		if(true)
			if(getCollisionShape().contains(suggestedTrajectory)) {
				a.addStatus(Status.CLIMBING);
				if(a.checkStatus(Status.UP)) {
					if(suggestedPosition.y < getMaxY() - Constants.VEL / 2) {
						suggestedVelocity.y = Constants.VEL / 2;
						suggestedVelocity.x = 0;
					}
				} else if(a.checkStatus(Status.DOWN)) {
					if(suggestedPosition.y > getMinY() + Constants.VEL / 2) {
						suggestedVelocity.y = -Constants.VEL / 2;
						suggestedVelocity.x = 0;
					}
				} else {
					suggestedVelocity.y = 0;
				}
				Vector2f temp = new Vector2f(
						a.getCenterBottom().x,
						a.getCenterBottom().y
						);
				suggestedTrajectory.set(temp, temp.add(suggestedVelocity));
				a.addStatus(Status.GROUNDED);
				a.removeStatus(Status.JUMPING);
				return true;
			}
			else {
				a.removeStatus(Status.CLIMBING);
			}
		return false;
	}
}

class MovingMotionTrack extends MotionTrack implements Updateable {

	private static final long serialVersionUID = -8954373676646170720L;
	float angle, angleincrement = 0.1f, dX;
	
	MovingMotionTrack(float sx, float sy, float ex, float ey) {
		super(sx, sy, ex, ey);
	}
	
	@Override
	public void update() {
		angle += angleincrement;
		angle = angle % (2 * 3.141592f);
		dX = 200 * (float)Math.sin(angle);
		remove();
		track.setLocation(track.getX() + dX, track.getY());
		setCollisionShape(track);
		add();
	}
}

