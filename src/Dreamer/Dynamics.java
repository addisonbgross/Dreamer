package Dreamer;

import org.lwjgl.util.vector.Vector3f;

public class Dynamics implements java.io.Serializable {
	
	private static final long serialVersionUID = -5242307807752292157L;
	private Vector3f velocity = new Vector3f();
	
	Vector3f getVelocity() {return velocity;}
	
	float getXVel() {return velocity.x;}
	public float getYVel() {return velocity.y;}
	float getZVel() {return velocity.z;}
	
	public Dynamics setXVel(float f) {velocity.x = f; return this;}
	public Dynamics setYVel(float f) {velocity.y = f; return this;}
	public Dynamics setZVel(float f) {velocity.z = f; return this;}
	
	Dynamics setVelocity(float x, float y, float z) {
		velocity.set(x, y, z);
		return this;
	}
	Dynamics setVelocity(Vector3f v) {
		setVelocity(v.x, v.y, v.z);
		return this;
	}
	// movement and physics
	Dynamics applyGravity() {	
		velocity.y -= Constants.GRAVITY;
		return this;
	}
	Dynamics adjustVel(float xInc, float yInc){
		velocity.x += xInc;
		velocity.x = Math.min(velocity.x, Constants.PLAYERMAXVEL);
		velocity.y += yInc;
		velocity.y = Math.min(velocity.y, Constants.PLAYERJUMPVEL);
		return this;
	}
	Dynamics applyFriction(double d) {
		d = Math.min(1, d);
		d = 1 - d;
		velocity.x *= d;
		velocity.y *= d;
		return this;
	}
}
