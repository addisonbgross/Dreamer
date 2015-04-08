package Dreamer;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

public class Shape3d extends Positionable implements Lightable {
	
	private static final long serialVersionUID = -2545062660573860101L;

	static Random r = new Random();

	protected Vector3f manhattanRadius = new Vector3f();
	ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	protected ArrayList<Face> faces = new ArrayList<Face>();
	protected ArrayList<Transformer> transformers = new ArrayList<Transformer>();

	// boolean fading = false; // implement in future?

	float[] pow2 = { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048 };

	Shape3d() {
	}

	Shape3d(float x, float y, float z) {
		this.setPosition(x, y, z);
	}

	@Override
	float getX() {
		return position.x;
	}

	@Override
	float getY() {
		return position.y;
	}

	@Override
	float getZ() {
		return position.z;
	}

	@Override
	float getWidth() {
		return 2 * manhattanRadius.x;
	}

	@Override
	float getHeight() {
		return 2 * manhattanRadius.y;
	}

	@Override
	float getDepth() {
		return 2 * manhattanRadius.z;
	}

	@Override
	boolean isVisible() {
		boolean huehuehue = true;

		if (!huehuehue) {
			if (Camera.isPointVisible(getX(), getY(), getZ()))
				return true;
	
			if (getX() >= Camera.getCenterX() && getY() >= Camera.getCenterY()) // Cartesian
																				// I
				if (Camera.isPointVisible(getX() - getWidth() / 2, getY()
						- getHeight() / 2, getZ() - getDepth()))
					return true;
				else
					return false;
	
			if (getX() <= Camera.getCenterX() && getY() >= Camera.getCenterY()) // Cartesian
																				// II
				if (Camera.isPointVisible(getX() + getWidth() / 2, getY()
						- getHeight() / 2, getZ() - getDepth()))
					return true;
				else
					return false;
	
			if (getX() <= Camera.getCenterX() && getY() <= Camera.getCenterY()) // Cartesian
																				// III
				if (Camera.isPointVisible(getX() + getWidth() / 2, getY()
						+ getHeight() / 2, getZ() - getDepth()))
					return true;
				else
					return false;
	
			if (getX() >= Camera.getCenterX() && getY() <= Camera.getCenterY()) // Cartesian
																				// IV
				if (Camera.isPointVisible(getX() - getWidth() / 2, getY()
						+ getHeight() / 2, getZ() - getDepth()))
					return true;
				else
					return false;

			return false;
		} else {
			return true;
		}
	}

	Shape3d scale(float f) {

		for (Vector3f v : this.vertices) {
			v.scale(f);
		}
		
		manhattanRadius.scale(f);
		
		return this;
	}

	Shape3d scale(float x, float y, float z) {

		for (Vector3f v : this.vertices) {
			v.x *= x;
			v.y *= y;
			v.z *= z;
		}
		
		manhattanRadius.x *= x;
		manhattanRadius.y *= y;
		manhattanRadius.z *= z;
		
		return this;
	}

	Shape3d setColor(Color c) {

		for (Face f : faces) {
			f.setColor(c);
		}
		return this;
	}

	Shape3d randomize(float f) {

		for (Vector3f v : this.vertices) {
			v.x *= 1 + f * (0.5f - r.nextFloat());
			v.y *= 1 + f * (0.5f - r.nextFloat());
			v.z *= 1 + f * (0.5f - r.nextFloat());
		}
		for (Face fa : faces) {
			fa.calculateNormal();
		}
		return this;
	}

	float textureStretch(int dimension) {
		for (int i = 0; i < pow2.length; i++) {
			if (dimension <= pow2[i])
				return dimension / pow2[i];
		}
		return 1;
	}

	// adds a vertex and updates the current radius in each cardinal direction
	// vertices
	Vector3f addVertex(float x, float y, float z) {
		Vector3f v = new Vector3f(x, y, z);
		vertices.add(v);
		manhattanRadius = updateBounds(v, manhattanRadius);
		return v;
	}
	Vector3f updateBounds(Vector3f v, Vector3f bounds) {
		bounds.x = Math.max(Math.abs(v.x), bounds.x);
		bounds.y = Math.max(Math.abs(v.y), bounds.y);
		bounds.z = Math.max(Math.abs(v.z), bounds.z);
		return bounds;
	}
	
	Vector3f findCenter() {
		// TODO: this!
		Vector3f center = new Vector3f();
		
		for(Vector3f v: vertices) {
			center = Vector3f.add(center, v, center);
		}
		
		center.scale(1 / vertices.size());
		
		return center;
	}

	public ArrayList<Vector2f> generateIntersectionPoints() {
		ArrayList<Vector2f> points = new ArrayList<Vector2f>();
		Vector3f pointA = new Vector3f(), pointB = new Vector3f(), line = new Vector3f();
		boolean[][] edgeGraph = new boolean[vertices.size()][vertices.size()];

		for (Face f : faces) {
			int sides = f.vertexIndex.length;
			for (int i = 0; i < sides; i++) {
				int v1 = f.vertexIndex[i];
				int v2 = f.vertexIndex[(i + 1) % sides];
				// if this edge has not been checked before
				if (!(edgeGraph[v1][v2] || edgeGraph[v2][v1])) {
					// mark edge as checked
					edgeGraph[v1][v2] = true;
					edgeGraph[v2][v1] = true;
					Vector3f.add(vertices.get(v1), getPosition3f(), pointA);
					Vector3f.add(vertices.get(v2), getPosition3f(), pointB);
					// if line intersects the z-plane
					if ((pointA.z >= 0 && pointB.z <= 0)
							|| (pointB.z >= 0 && pointA.z <= 0)) {
						// find intersection point
						Vector3f.sub(pointB, pointA, line);
						float t = -pointA.z / line.z;
						Float x = line.x * t + pointA.x;
						Float y = line.y * t + pointA.y;
						points.add(new Vector2f(x, y));
					}
				}
			}
		}
		return points;
	}

	public Vector3f getTranslatedVertex(int i, Vector3f destination) {
		try {
			destination.set(vertices.get(i));
			transformVertex(destination, destination);
			return Vector3f.add(destination, getPosition3f(), destination);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Vector3f transformVertex(Vector3f v, Vector3f destination) {
		for (Transformer tx : transformers) {
			destination.set(tx.transformVertex(v, destination));
		}
		return destination;
	}

	public Vector3f getTranslatedNormal(Face f, Vector3f destination) {
		destination.set(f.normal);
		for (Transformer tx : transformers)
			tx.transformNormal(destination, destination);
		return destination;
	}

	public ArrayList<Vector2f> generateIntersectionPairs() {
		ArrayList<Vector2f> points = new ArrayList<Vector2f>();
		Vector3f pointA, pointB, line = new Vector3f();
		for (Face f : faces) {
			int sides = f.vertexIndex.length;
			for (int i = 0; i < sides; i++) {
				int v1 = f.vertexIndex[i];
				int v2 = f.vertexIndex[(i + 1) % sides];
				pointA = Vector3f.add(vertices.get(v1), getPosition3f(), null);
				pointB = Vector3f.add(vertices.get(v2), getPosition3f(), null);
				// if line intersects the z-plane
				if ((pointA.z >= 0 && pointB.z <= 0)
						|| (pointB.z >= 0 && pointA.z <= 0)) {
					// find intersection point
					Vector3f.sub(pointB, pointA, line);
					float t = -pointA.z / line.z;
					Float x = line.x * t + pointA.x;
					Float y = line.y * t + pointA.y;
					points.add(new Vector2f(x, y));
				}
			}
		}
		return points;
	}

	void addFace(Face f) {
		f.masterShape = this;
		f.calculateNormal();
		faces.add(f);
	}

	void addFace(Color c, int... i) {
		if (i.length <= 2)
			return;
		addFace(new Face(c, i));
	}

	final public void light(Light l) {
		for (Face f : faces) {
			l.light(f);
		}
	}

	@Override
	// for reference, this is how the camera finds the point on the screen
	// Camera.translate(getVertex(triangleIndex[j]), tempV3f);
	void draw() {
		if (isVisible())
			for (Face f : faces)
				f.addToDrawList();
	}

	public void generateMotionTracks() {
		for (Face f : faces)
			MotionTrack.generateMotionTrack(f, vertices, getPosition3f());
	}

	public void generateMotionTrack(int i) {
		MotionTrack
				.generateMotionTrack(faces.get(i), vertices, getPosition3f());
	}

	public void generateCollidable() {
		Polygon p = generateCollisionShape();
		if (p != null)
			new Collidable(generateCollisionShape()).add();
	}

	public Polygon generateCollisionShape() {
		Polygon p = new Polygon();
		ArrayList<Vector2f> pointList = generateIntersectionPoints();
		if (pointList.size() == 0)
			return null;
		else {
			for (Vector2f v : generateIntersectionPoints())
				p.addPoint(v.x, v.y);
			return p;
		}
	}

	Shape3d rotate(float x, float y, float z, float theta) {
		Vector3f axis = new Vector3f(x, y, z);

		for (Vector3f v : vertices) {
			Transformer.rotate(v, axis, theta, v);
		}
		for (Face f : faces) {
			f.calculateNormal();
		}
		return this;
	}
	
	DynamicShape3d makeDynamic() {
		return new DynamicShape3d(this);
	}
	
	Shape3d getCopy() {
		Shape3d s = new Shape3d(); {
			
			s.manhattanRadius.set(manhattanRadius);
			s.position.set(position);
			for (Vector3f v: vertices) {
				s.addVertex(v.x, v.y, v.z);
			}
			for (Face f : faces)
				s.addFace(f);
		}
		return s;
	}
}

class DynamicShape3d extends Shape3d implements Updateable {

	private static final long serialVersionUID = -8826293081438535027L;

	DynamicShape3d(float x, float y, float z) {
		super(x, y, z);
	}

	DynamicShape3d(Shape3d s) {
		this.manhattanRadius.set(s.manhattanRadius);
		this.position.set(s.position);
		for (Vector3f v : s.vertices)
			this.addVertex(v.x, v.y, v.z);
		for (Face f : s.faces)
			this.addFace(f);
	}

	Shape3d makeStatic() {
		Shape3d s = new Shape3d(); {
			
			s.manhattanRadius.set(manhattanRadius);
			s.position.set(position);
			for (Vector3f v: vertices) {
				Vector3f t = new Vector3f(v);
				t = transformVertex(v, t);
				s.addVertex(t.x, t.y, t.z);
			}
			for (Face f : faces)
				s.addFace(f);
		}
		return s;
	}

	public void update() {
		for (Transformer tx : transformers)
			tx.update();
	}

	DynamicShape3d addTransformer(Transformer t) {
		transformers.add(t);
		return this;
	}
	
	DynamicShape3d clearTransformers() {
		transformers.clear();
		return this;
	}
}

class SpinningJewel extends DynamicShape3d implements Updateable {

	SpinningJewel(float x, float y, float z, float size) {
		super(x, y, z);
		transformers.add(new Rotator(0, 1, 0, 100 / size));
		transformers.add(new Pulsar(0.05f, 1));
		manhattanRadius.set(size, size, size);

		Color c = Theme.current.getColor(Theme.Default.LIGHT);

		addVertex(-size, 0, 0);
		addVertex(0, size, 0);
		addVertex(size, 0, 0);
		addVertex(0, -size, 0);
		addVertex(0, 0, size);
		addVertex(0, 0, -size);

		addFace(c, 0, 1, 4);
		addFace(c, 1, 2, 4);
		addFace(c, 2, 3, 4);
		addFace(c, 3, 0, 4);
		addFace(c, 1, 0, 5);
		addFace(c, 0, 3, 5);
		addFace(c, 3, 2, 5);
		addFace(c, 2, 1, 5);
	}
}

class LadderPlatform extends Shape3d {

	
	
	final float height = 10, depth = 200, space = 20;

	LadderPlatform(float x, float y, float z, float width, boolean flipped) {
		super(x, y, z);

		// TODO make work with other collidables properly
		new MotionTrack(60 + x - width / 2, y, x + width / 2, y).add();

		addVertex(-width / 2, -height / 2, -depth / 2);
		addVertex(-width / 2, height / 2, -depth / 2);
		addVertex(-width / 2, height / 2, 0);
		addVertex(-width / 2, -height / 2, 0);

		addVertex(-width / 2 + space + Constants.ladderWidth, height / 2, 0);
		addVertex(-width / 2 + space + Constants.ladderWidth, -height / 2, 0);

		addVertex(-width / 2 + space + Constants.ladderWidth, height / 2,
				depth / 2);
		addVertex(-width / 2 + space + Constants.ladderWidth, -height / 2,
				depth / 2);

		addVertex(width / 2, height / 2, depth / 2);
		addVertex(width / 2, -height / 2, depth / 2);

		addVertex(width / 2, height / 2, -depth / 2);
		addVertex(width / 2, -height / 2, -depth / 2);

		addFace(Color.red, 0, 1, 2, 3);
		addFace(Color.red, 3, 2, 4, 5);
		addFace(Color.red, 5, 4, 6, 7);
		addFace(Color.red, 7, 6, 8, 9);
		addFace(Color.red, 9, 8, 10, 11);
		addFace(Color.red, 11, 10, 1, 0);
		addFace(Color.red, 6, 4, 10, 8);
		addFace(Color.red, 4, 2, 1, 10);
		addFace(Color.red, 11, 0, 3, 5);
		addFace(Color.red, 11, 5, 7, 9);
	}
}

class Block3d extends Shape3d {
	Block3d(Color c, float x, float y, float z, float w, float h, float d) {
		super(x, y, z);

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
		addFace(c, 0, 4, 7, 3);
		addFace(c, 0, 1, 5, 4);
	}

	Block3d(float x, float y, float z, float w, float h, float d) {
		this(Theme.current.getColor(Theme.Default.LIGHT), x, y, z, w, h, d);
	}
}

class Weird extends Shape3d {
	Weird(float x, float y, float z, float size) {
		super(x, y, z);

		addVertex(-(2 * size), size / 2, size / 2);
		addVertex(-size, size, -size / 2);
		addVertex(0, 0, size / 2);
		addVertex(size, size, -size / 2);
		addVertex(2 * size, size / 2, size / 2);
		addVertex(3 * size, size, -size / 2);

		addFace(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 1,
				2, 0);
		addFace(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 1,
				3, 2);
		addFace(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 3,
				4, 2);
		addFace(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 3,
				5, 4);
	}
}

class Ladder extends Shape3d {
	static final Color ladderColor = Color.blue;

	Ladder(float x, float y, float z, float size) {
		super(x, y, z);
		int postSize = 7;
		int rungSpace = 30;
		int rungSize = 5;
		int numRungs = (int) (size / rungSpace);

		new Block3d(ladderColor, x - Constants.ladderWidth / 2, y + size / 2,
				z, postSize, size, postSize).add();
		new Block3d(ladderColor, x + Constants.ladderWidth / 2, y + size / 2,
				z, postSize, size, postSize).add();
		for (; numRungs > 0; numRungs--)
			new Block3d(ladderColor, x, y + rungSpace * numRungs, z,
					Constants.ladderWidth - postSize, rungSize, rungSize).add();
		new LadderTrack(x - Constants.ladderWidth / 2 + rungSize, y, x
				+ Constants.ladderWidth / 2 - rungSize, y + size).add();
	}
}

class Pillar extends Shape3d {
	Pillar(float x, float y, float z, int sides, float radius, float height) {
		super(x, y, z);
		for (float angle = 0; angle < 2 * Math.PI; angle += (2 * Math.PI)
				/ sides) {
			addVertex((float) (radius * Math.cos(angle)), height / 2,
					(float) (radius * Math.sin(angle)));
			addVertex((float) (radius * Math.cos(angle)), -height / 2,
					(float) (radius * Math.sin(angle)));
		}
		for (int i = 0; i < sides; i++)
			addFace(Color.white, (2 * i), (2 * i) + 1,
					((2 * i) + 3) % vertices.size(),
					((2 * i) + 2) % vertices.size());
	}
}

class Temple {
	final static float CEILINGHEIGHT = 300, FLOORTHICKNESS = 20, DEPTH = 300,
			PILLARSIZE = 20;
	final static int NUMSIDES = 9;

	Temple(float x, float y, float z, float size) {
		Shape3d s = new Block3d(Color.white, x, y + FLOORTHICKNESS / 2, z,
				size, FLOORTHICKNESS, DEPTH);
		s.generateCollidable();
		s.add();
		new Pillar(x - size / 2 + PILLARSIZE, y + FLOORTHICKNESS
				+ CEILINGHEIGHT / 2, z - DEPTH / 2 + PILLARSIZE, NUMSIDES,
				PILLARSIZE / 2, CEILINGHEIGHT).add();
		s = new Pillar(x - size / 2 + PILLARSIZE, y + FLOORTHICKNESS
				+ CEILINGHEIGHT / 2, z + DEPTH / 2 - PILLARSIZE, NUMSIDES,
				PILLARSIZE / 2, CEILINGHEIGHT);
		// s.fading = true;
		s.add();
		s = new Pillar(x + size / 2 - PILLARSIZE, y + FLOORTHICKNESS
				+ CEILINGHEIGHT / 2, z + DEPTH / 2 - PILLARSIZE, NUMSIDES,
				PILLARSIZE / 2, CEILINGHEIGHT);
		// s.fading = true;
		s.add();
		new Block3d(Color.white, x, y + CEILINGHEIGHT + FLOORTHICKNESS * 3 / 2,
				z, size, FLOORTHICKNESS, DEPTH).add();
		new Pillar(x + size / 2 - PILLARSIZE, y + FLOORTHICKNESS
				+ CEILINGHEIGHT / 2, z - DEPTH / 2 + PILLARSIZE, NUMSIDES,
				PILLARSIZE / 2, CEILINGHEIGHT).add();
		new Pyramid3d(x, y + CEILINGHEIGHT + 2 * FLOORTHICKNESS, z, size,
				CEILINGHEIGHT / 3, DEPTH).add();
	}
}

class ActionJewel extends SpinningJewel {
	
	int size = 20;
	Level level;
	Collidable transporter;

	ActionJewel(float x, float y, float z, Action action) {
		super(x, y, z, 20);
		transporter = new Collidable(new Rectangle(getX() - size / 2, getY()
				- size / 2, size, size)) {
			boolean collide(Actor a) {
				if (a.getCollisionShape().intersects(getCollisionShape())) {
					action.perform(a);
					action.perform();
					return true;
				}
				return false;
			}
		};
		transporter.add();
		add();
	}
}

class Island extends DynamicShape3d {

	Island(float x, float y, float z) {
		super(x, y, z);
		create(100);
	}
	
	Island(float x, float y, float z, float radius) {
		super(x, y, z);
		create(radius);
		generateTopTrack();
		transformers.add(new Rotator(0, 1, 0, 5 / radius));
		transformers.add(new Pulsar(0.05f, 0.4f));
	}

	void create(float radius) {
		int numPoints = 8;
		for (float theta = 0; theta < (2 * Math.PI); theta += (2 * Math.PI)
				/ numPoints)
			addVertex(radius * (float) Math.cos(theta), 0, radius
					* (float) Math.sin(theta));
		addVertex(0, -radius, 0);
		addFace(Theme.current.getColor(Theme.Default.LIGHT), 0, 1, 2, 3, 4, 5,
				6, 7); // top
		for (int i = 0; i < numPoints; i++)
			addFace(Theme.current.getColor(Theme.Default.DARK), 9, (i + 1)
					% numPoints, i);
	}
	void generateTopTrack() {
		generateMotionTrack(0);
	}
}

class GreyRoom extends Shape3d {
	int size = 500;
	Color c = Color.gray;

	GreyRoom(int x, int y, int z) {
		setPosition(x, y, z);
		addVertex(-size, 0, -size);
		addVertex(-size, size, -size);
		addVertex(size, size, -size);
		addVertex(size, 0, -size);

		addVertex(-size, 0, size);
		addVertex(-size, size, size);
		addVertex(size, size, size);
		addVertex(size, 0, size);

		addVertex(-2 * size, -size, size);
		addVertex(-2 * size, 2 * size, size);
		addVertex(2 * size, 2 * size, size);
		addVertex(2 * size, -size, size);

		addFace(c, 0, 1, 2, 3);
		addFace(c, 4, 5, 1, 0);
		addFace(c, 4, 0, 3, 7);
		addFace(c, 2, 6, 7, 3);
		addFace(c, 1, 5, 6, 2);

		addFace(c, 5, 9, 10, 6);
		addFace(c, 6, 10, 11, 7);
		addFace(c, 7, 11, 8, 4);
		addFace(c, 4, 8, 9, 5);

		generateMotionTracks();
	}
}

class Flare extends Shape3d implements Updateable {
	FlareLight light;
	
	Flare(float x, float y, float z) {
		super(x, y, z);
		light = new FlareLight(x, y, z);
	}
	
	@Override
	void add() {
		super.add();
		light.add();
	}

	@Override
	void remove() {
		super.remove();
		light.remove();
	}

	@Override
	boolean isVisible() {
		if (Camera.isPointVisible(getX(), getY(), getZ()))
			return true;
		else if (Camera.isPointVisible(getX() + Constants.LIGHTDISTANCE, getY()
				+ Constants.LIGHTDISTANCE, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() + Constants.LIGHTDISTANCE, getY()
				- Constants.LIGHTDISTANCE, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() - Constants.LIGHTDISTANCE, getY()
				+ Constants.LIGHTDISTANCE, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() - Constants.LIGHTDISTANCE, getY()
				- Constants.LIGHTDISTANCE, getZ()))
			return true;
		return false;
	}

	public void update() {
		light.update();
	}
}

class Lamp extends Shape3d implements Updateable {
	Actor actor;
	Color glass = new Color(1.0f, 1.0f, 1.0f, 1f);
	Color fire = Color.yellow;
	LampLight light;

	Lamp(float x, float y, float z, float range) {
		this(x, y, z);
		light.flickerRange = range;
	}

	Lamp(float x, float y, float z) {
		super(x, y, z);
		light = new LampLight(this);
		// Glass lamp casing
		addVertex(-5, 0, 5);
		addVertex(-5, 0, 0);
		addVertex(5, 0, 0);
		addVertex(5, 0, 5);

		addVertex(-5, 10, 5);
		addVertex(-5, 10, 0);
		addVertex(5, 10, 0);
		addVertex(5, 10, 5);

		addFace(glass, 0, 1, 2, 3); // bottom
		addFace(glass, 4, 5, 6, 7); // top
		// addFace(glass, 0, 4, 7, 3); //front
		addFace(glass, 0, 1, 5, 4); // left side
		addFace(glass, 3, 7, 6, 2); // right side
		addFace(glass, 1, 5, 6, 2); // back, for some reason

		// Fire inside that lamp
		addVertex(-2, 0, 2);
		addVertex(-2, 0, 0);
		addVertex(2, 0, 0);
		addVertex(2, 0, 2);

		addVertex(-2, 4, 2);
		addVertex(-2, 4, 0);
		addVertex(2, 4, 0);
		addVertex(2, 4, 2);

		addFace(fire, 8, 9, 10, 11); // bottom
		addFace(fire, 12, 13, 14, 15); // top
		addFace(fire, 8, 12, 15, 11); // front
		addFace(fire, 8, 9, 13, 12); // left side
		addFace(fire, 11, 15, 14, 10); // right side
		addFace(fire, 1, 5, 6, 2); // back, for some reason
	}

	Lamp(Actor a) {
		this(a.getX(), a.getY(), a.getZ());
		actor = a;
	}

	@Override
	void add() {
		super.add();
		light.add();
	}

	@Override
	void remove() {
		super.remove();
		light.remove();
	}

	@Override
	boolean isVisible() {
		if (Camera.isPointVisible(getX(), getY(), getZ()))
			return true;
		else if (Camera.isPointVisible(getX() + Constants.LIGHTDISTANCE, getY()
				+ Constants.LIGHTDISTANCE, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() + Constants.LIGHTDISTANCE, getY()
				- Constants.LIGHTDISTANCE, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() - Constants.LIGHTDISTANCE, getY()
				+ Constants.LIGHTDISTANCE, getZ()))
			return true;
		else if (Camera.isPointVisible(getX() - Constants.LIGHTDISTANCE, getY()
				- Constants.LIGHTDISTANCE, getZ()))
			return true;
		return false;
	}

	public void update() {
		if (actor != null)
			setPosition(actor.getX() + actor.body.beltPoint.x, actor.getMinY()
					+ actor.body.beltPoint.y, 2);
		light.update();
	}
}

class Ziggurat {
	float layerSize = 50;

	Ziggurat(float x, float y, float z, float size) {
		Block3d b;
		int numLayer = (int) (size / layerSize) / 5;
		for (int i = 0; i < numLayer; ++i) {
			b = new Block3d(new Color(0.2f, 0.8f, 0.3f), x, y + layerSize * i,
					z, size / numLayer * (numLayer - i), layerSize, size
							/ numLayer * (numLayer - i));
			b.generateCollidable();
			b.add();
		}
	}
};
