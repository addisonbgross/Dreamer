	package Dreamer;

import interfaces.Updateable;

import org.lwjgl.util.vector.Vector3f;

public abstract class Transformer implements Updateable, java.io.Serializable {
	
	private static final long serialVersionUID = 6765091439474383443L;
	public abstract Vector3f transformVertex(Vector3f v, Vector3f destination);
	public abstract Vector3f transformNormal(Vector3f v, Vector3f destination);
	
	public static  Vector3f rotate(Vector3f v, Vector3f rotationAxis, float angle, Vector3f destination) {
		return destination.set(Vector.rotate(rotationAxis, v, angle));
	}
	public static Vector3f translate(Vector3f v, Vector3f translation, Vector3f destination) {
		return Vector3f.add(v, translation, destination);
	}
	public static Vector3f untranslate(Vector3f v, Vector3f translation, Vector3f destination) {
		return Vector3f.sub(v, translation, destination);
	}
}

class Pulsar extends Transformer {
	
	private static final long serialVersionUID = -5465661317879986334L;
	float angle, increment, pulseAmount;
	
	Pulsar(float amount, float speed) {
		pulseAmount = amount;
		increment = 0.1f * speed;
	}
	
	public void update() {
		angle += increment;
		angle = angle % (2 * 3.1415692f);
	}
	public Vector3f transformVertex(Vector3f v, Vector3f destination) {
		float scale = pulseAmount * (float)Math.sin(angle) + 1;
		destination.set(scale * v.x, scale * v.y, scale * v.z);
		return destination;
	}
	public Vector3f transformNormal(Vector3f v, Vector3f destination){
		return v;
	}
}

class Rotator extends Transformer {
	
	private static final long serialVersionUID = 1929699675823063966L;
	Vector3f rotationAxis, rotationPoint;
	float angle, increment;
	
	Rotator(float x, float y, float z, float speed) {
		setRotationAxis(x, y, z);
		setRotationPoint(0, 0, 0);
		increment = 0.1f * speed;
	}
	
	void setRotationPoint(float x, float y, float z) {
		rotationPoint = new Vector3f(x, y, z);
	}
	void setRotationAxis(float x, float y, float z) {
		rotationAxis = new Vector3f(x, y, z).normalise(rotationAxis);
	}
	public void update() {
		angle += increment;
		angle = angle % (2 * 3.1415692f);
	}
	@Override
	public Vector3f transformVertex(Vector3f v, Vector3f destination) {
		untranslate(v, rotationPoint, destination);
		rotate(v, rotationAxis, angle, destination);
		return translate(destination, rotationPoint, destination);	
	}
	@Override
	public Vector3f transformNormal(Vector3f v, Vector3f destination) {
		return rotate(v, rotationAxis, angle, destination);
	}
	@Override
	public String toString() {
		return "Rotating at " + increment + " rad/update, current angle " + angle;
	}
}