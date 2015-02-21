	package Dreamer;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public abstract class Transformer implements Updateable {
	
	public abstract Vector3f transformVertex(Vector3f v, Vector3f destination);
	public abstract Vector3f transformNormal(Vector3f v, Vector3f destination);
	
	public Vector3f rotate(Vector3f v, Vector3f rotationAxis, float angle, Vector3f destination) {
		return destination.set(Vector.rotate(rotationAxis, v, angle));
	}
	public Vector3f translate(Vector3f v, Vector3f translation, Vector3f destination) {
		return Vector3f.add(v, translation, destination);
	}
	public Vector3f untranslate(Vector3f v, Vector3f translation, Vector3f destination) {
		return Vector3f.sub(v, translation, destination);
	}
}

class Pulsar extends Transformer {
	
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
	public Vector3f transformVertex(Vector3f v, Vector3f destination) {
		untranslate(v, rotationPoint, destination);
		rotate(v, rotationAxis, angle, destination);
		return translate(v, rotationPoint, destination);
	}
	public Vector3f transformNormal(Vector3f v, Vector3f destination) {
		return rotate(v, rotationAxis, angle, destination);
	}
}

class Wiggler extends Rotator {
	
	Wiggler(float x, float y, float z, float speed) {
		super(x, y, z, speed);
	}
	
	@Override
	public Vector3f transformVertex(Vector3f v, Vector3f destination) {
		untranslate(v, rotationPoint, destination);
		rotate(v, rotationAxis, (float)(0.5f * Math.sin(angle)), destination);
		return translate(v, rotationPoint, destination);
	}
	@Override
	public Vector3f transformNormal(Vector3f v, Vector3f destination) {
		return rotate(v, rotationAxis, (float)(0.5f * Math.sin(angle)), destination);
	}
}

class Test extends DynamicShape3d {

	Wiggler wig;
	Test link = null;
	Vector3f linkPoint;
	
	Test(Color c, float x, float y, float z, float w, float h, float d) {
		super(x, y, z);
		wig = new Wiggler(0, 0, 1, 2f);
		wig.setRotationPoint(0, -h/2, 0);
		transformers.add(wig);
		linkPoint = new Vector3f(0, getHeight() / 2, 0);
		
		addVertex(w / 2, -h / 2, -d / 2);
		addVertex(w / 2, -h / 2, d / 2);
		addVertex(-w / 2, -h / 2, d / 2);
		addVertex(-w / 2, -h / 2, -d / 2);
		addVertex(w / 2, h / 2, -d / 2);
		addVertex(w / 2, h / 2, d / 2);
		addVertex(-w / 2, h / 2, d / 2);
		addVertex(-w / 2, h / 2, -d / 2);

		addFace(c, 0, 3, 2, 1);
		addFace(c, 4, 5, 6, 7);
		addFace(c, 1, 2, 6, 5);
		addFace(c, 7, 6, 2, 3);
		addFace(c, 3, 7, 4, 0);
		addFace(c, 0, 1, 5, 4);
	}
	Test(float x, float y, float z, float w, float h, float d) {
		this(Theme.current.getColor(Theme.Default.LIGHT), x, y, z, w, h, d);
	}
	
	@Override
	public void update() {
		super.update();
		linkPoint.set(0, getHeight() / 2, 0);
		linkPoint = wig.transformVertex(linkPoint, linkPoint);
		linkPoint = wig.translate(linkPoint, getPosition3f(), linkPoint);
		if(link != null) {
			setPosition(link.linkPoint.x, link.linkPoint.y + getHeight() / 2, link.linkPoint.z);
		}
	}
}