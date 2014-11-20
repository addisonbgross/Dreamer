package Dreamer;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

public class Shape3d extends Element implements Lightable {
	static Random r = new Random();
	static Vector3f tempV3f =  new Vector3f();
	static Vector4f tempV4f =  new Vector4f();
	static Color tempColor;
	boolean initialized = false;
	
	Vector3f manhattanRadius = new Vector3f();
	ArrayList<Vector4f> vertices = new ArrayList<Vector4f>();
	private ArrayList<Face> faces = new ArrayList<Face>();
	
	boolean fading = false;
	Vector3f rotationAxis;
	float angle = 0;
	float angleIncrement = 0;
	float[] pow2 = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};
	
	Shape3d() {}
	Shape3d(float x, float y, float z) {
		this.setPosition(x, y, z);
	}
	
	@Override
	float getX() {return position.x;}
	@Override
	float getY() {return position.y;}
	@Override
	float getZ() {return position.z;}
	@Override
	float getWidth() {return 2 * manhattanRadius.x;}
	@Override
	float getHeight() {return 2 * manhattanRadius.y;}
	@Override
	float getDepth() {return 2 * manhattanRadius.z;}	
	@Override
	boolean isVisible() {		
		if (Camera.isPointVisible(getX(), getY(), getZ()))
			return true;
		
		if (getX() >= Camera.getCenterX() && getY() >= Camera.getCenterY()) // Cartesian I
			if (Camera.isPointVisible(getX() - getWidth() / 2, getY() - getHeight() / 2, getZ() - getDepth()))
				return true;
			else
				return false;
		
		if (getX() <= Camera.getCenterX() && getY() >= Camera.getCenterY()) // Cartesian II
			if (Camera.isPointVisible(getX() + getWidth() / 2, getY() - getHeight() / 2, getZ() - getDepth()))
				return true;
			else
				return false; 
		
		if (getX() <= Camera.getCenterX() && getY() <= Camera.getCenterY()) // Cartesian III
			if (Camera.isPointVisible(getX() + getWidth() / 2, getY() + getHeight() / 2, getZ() - getDepth()))
				return true;
			else 
				return false;
		
		if (getX() >= Camera.getCenterX() && getY() <= Camera.getCenterY()) // Cartesian IV
			if (Camera.isPointVisible(getX() - getWidth() / 2, getY() + getHeight() / 2, getZ() - getDepth()))
				return true;
			else
				return false;
		
		return false;
	}
	
	float textureStretch(int dimension) {
		for(int i = 0;  i < pow2.length; i++) {
			if(dimension <= pow2[i])
				return dimension / pow2[i]; 
		}
		return 1;
	}
	void setRotationAxis(float x, float y, float z) {
		rotationAxis = new Vector3f(x, y, z).normalise(rotationAxis); 
	}
	//adds a vertex and updates the current radius in each cardinal direction
	//vertices 
	Vector4f addVertex(float x, float y, float z) {
		Vector4f v = new Vector4f(x, y, z, 1);
		vertices.add(v);
		manhattanRadius.x = Math.max(Math.abs(x), manhattanRadius.x);
		manhattanRadius.y = Math.max(Math.abs(y), manhattanRadius.y);
		manhattanRadius.z = Math.max(Math.abs(z), manhattanRadius.z);
		return v;
	}
	public ArrayList<Vector2f> generateIntersectionPoints() {
		ArrayList<Vector2f> points = new ArrayList<Vector2f>();
		Vector4f pointA = new Vector4f(), pointB = new Vector4f(), line = new Vector4f();
		boolean[][] edgeGraph = new boolean[vertices.size()][vertices.size()];
	
		for(Face f: faces) {
			int sides = f.vertexIndex.length;
			for(int i = 0; i < sides; i++) {
				int v1 = f.vertexIndex[i];
				int v2 = f.vertexIndex[(i + 1) % sides];
				//if this edge has not been checked before
				if(!(edgeGraph[v1][v2] || edgeGraph[v2][v1])) {
					//mark edge as checked
					edgeGraph[v1][v2] = true;
					edgeGraph[v2][v1] = true;
					Vector4f.add(vertices.get(v1), getPosition4f(), pointA);
					Vector4f.add(vertices.get(v2), getPosition4f(), pointB);
					//if line intersects the z-plane
					if((pointA.z >=  0 && pointB.z <= 0) || (pointB.z >=  0 && pointA.z <= 0) ) {
						//find intersection point
						Vector4f.sub(pointB, pointA, line);
						float t = -pointA.z /  line.z; 
						Float x = line.x * t + pointA.x;
						Float y = line.y * t + pointA.y;
						points.add(new Vector2f(x, y));
					}
				}
			}
		}
		return points;
	}
	public Vector4f getTranslatedVertex(int i, Vector4f v) {
		try {
			return Vector4f.add(vertices.get(i), getPosition4f(), v);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public ArrayList<Vector2f> generateIntersectionPairs() {
		ArrayList<Vector2f> points = new ArrayList<Vector2f>();
		Vector4f pointA, pointB, line = new Vector4f();
		for(Face f: faces) {
			int sides = f.vertexIndex.length;
			for(int i = 0; i < sides; i ++) {
				int v1 = f.vertexIndex[i];
				int v2 = f.vertexIndex[(i + 1) % sides];
				pointA = Vector4f.add(vertices.get(v1), getPosition4f(), null);
				pointB = Vector4f.add(vertices.get(v2), getPosition4f(), null);
				//if line intersects the z-plane
				if((pointA.z >=  0 && pointB.z <= 0) || (pointB.z >=  0 && pointA.z <= 0) ) {
					//find intersection point
					Vector4f.sub(pointB, pointA, line);
					float t = -pointA.z /  line.z; 
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
		f.normal = Vector.crossNormalized(
				Vector4f.sub(this.vertices.get(f.vertexIndex[0]), this.vertices.get(f.vertexIndex[1]), null), 
				Vector4f.sub(this.vertices.get(f.vertexIndex[2]), this.vertices.get(f.vertexIndex[1]), null)
				);
		faces.add(f);
	}
	void addFace(Color c, int... i) {
		if(i.length <= 2)
			return;
		addFace(new Face(c, i));
	}
	final public void light(Light l) {
		Vector4f direction;
		float amount;
		//starts from black during first round of lighting,
		//then progressively adds to the colour
		int accumulate = (Light.firstLight == false)? 1: 0;
		for (Face f: faces) {
			for(int i = 0; i < f.vertexIndex.length; i++) {
				//get the current vertex 
				getTranslatedVertex(f.vertexIndex[i], tempV4f);
				//find direction of light to vertex
				direction = Vector4f.sub(l.getPosition4f(), tempV4f, null).normalise(null);
				//product of direction of light and surface normal
				float orthogonality = l.orthogonality * Vector4f.dot(direction, f.normal);
				//calculate light based on distance
				if(l.ambient)
					amount = 1;
				else
					amount = Vector.getManhattanDistance(tempV4f, l.getPosition4f());
				if(amount < l.range)
					amount =  1- amount / l.range; 
				else 
					amount = 0;
				try { 
					//if rotation axis set compute rotated normal
					if(rotationAxis != null)
						tempV4f = Vector.rotate(rotationAxis, f.normal, angle);
					f.vertexColor[i] = new Color(
							f.vertexColor[i].r * accumulate + f.faceColor[i].r * amount * (1 + orthogonality) * l.color.r,
							f.vertexColor[i].g * accumulate + f.faceColor[i].g * amount * (1 + orthogonality) * l.color.g,
							f.vertexColor[i].b * accumulate + f.faceColor[i].b * amount * (1 + orthogonality) * l.color.b,
							f.vertexColor[i].a
							);
				} catch (ArrayIndexOutOfBoundsException e) {
					//e.printStackTrace();
					//this should not happen.  but it does?
				}
			}
		}
	}
	static int numberVertices = 0;
	@Override
	//for reference, this is how the camera finds the point on the screen
	//Camera.translate(getVertex(triangleIndex[j]), tempV3f);
	void draw(Graphics g) {
		if (this.isVisible())
			for (Face f: faces)
				f.addToDrawList();
	}
	public void generateMotionTracks() {
		for(Face f: faces)
			MotionTrack.generateMotionTrack(f, vertices, getPosition4f());
	}		
	public void generateMotionTrack(int i) {
		MotionTrack.generateMotionTrack(faces.get(i), vertices, getPosition4f());
	}
	public void generateCollidable() {
		Polygon p = generateCollisionShape();
		if(p != null)
			new Collidable(generateCollisionShape()).add();
	}	
	public Polygon generateCollisionShape() {
		Polygon p = new Polygon();
		ArrayList<Vector2f> pointList = generateIntersectionPoints();
		if(pointList.size() == 0)
			return null;
		else {
			for(Vector2f v: generateIntersectionPoints())
				p.addPoint(v.x, v.y);
			return p;
		}
	}	
}
class LadderPlatform extends Shape3d {
	final float height = 10, depth = 200, space = 20;
	
	LadderPlatform(float x, float y, float z, float width, boolean flipped) {
		super(x, y, z);
		
		//TODO make work with other collidables properly
		new MotionTrack(60 + x - width / 2, y, x + width / 2, y).add();
		
		addVertex(-width / 2, -height / 2, -depth / 2);
		addVertex(-width / 2, height / 2, -depth / 2);
		addVertex(-width / 2, height / 2, 0);
		addVertex(-width / 2, -height / 2, 0);
		
		addVertex(-width / 2 + space + Constants.ladderWidth, height / 2, 0);
		addVertex(-width / 2 + space + Constants.ladderWidth, -height / 2, 0);
		
		addVertex(-width / 2 + space + Constants.ladderWidth, height / 2, depth / 2);
		addVertex(-width / 2 + space + Constants.ladderWidth, -height / 2, depth / 2);
		
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
		
		addVertex(w/2, -h/2, -d/2);
		addVertex(w/2, -h/2, d/2);
		addVertex(-w/2, -h/2, d/2);
		addVertex(-w/2, -h/2, -d/2);
		addVertex(w/2, h/2, -d/2);
		addVertex(w/2, h/2, d/2);
		addVertex(-w/2, h/2, d/2);		
		addVertex(-w/2, h/2, -d/2);
		
		addFace(c, 0, 3, 2, 1);
		addFace(c, 4, 5, 6, 7);
		addFace(c, 1, 2, 6, 5);
		addFace(c, 7, 6, 2, 3);
		addFace(c, 3, 7, 4, 0);
		addFace(c, 0, 1, 5, 4);
	}
}
class Weird extends Shape3d {
	Weird(float x, float y, float z, float size) {
		super(x, y, z);
		
		addVertex(-(2 * size), size / 2, size / 2);
		addVertex(- size, size, -size / 2);
		addVertex(0, 0, size / 2);
		addVertex(size, size, -size / 2);
		addVertex(2 * size, size / 2, size / 2);
		addVertex(3 * size, size, -size / 2);
		
		addFace(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 1, 2, 0);
		addFace(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 1, 3, 2);
		addFace(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 3, 4, 2);
		addFace(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)), 3, 5, 4);
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
		
		new Block3d(ladderColor, x - Constants.ladderWidth/2, y + size/2, z, postSize, size, postSize).add();
		new Block3d(ladderColor, x + Constants.ladderWidth/2, y + size/2, z, postSize, size, postSize).add();
		for (; numRungs > 0; numRungs--)
			new Block3d(ladderColor, x, y + rungSpace * numRungs, z, Constants.ladderWidth - postSize, rungSize, rungSize).add();
		new LadderTrack(x - Constants.ladderWidth/2 + rungSize , y, x + Constants.ladderWidth/2 - rungSize, y + size).add();
	}
}
class Pillar extends Shape3d {
	Pillar(float x, float y, float z, int sides, float radius, float height) {
		super(x, y, z);
		for(float angle = 0; angle < 2 * Math.PI; angle += (2 * Math.PI) / sides) {
			addVertex((float)(radius * Math.cos(angle)), height / 2, (float)(radius * Math.sin(angle)));
			addVertex((float)(radius * Math.cos(angle)), - height / 2, (float)(radius * Math.sin(angle)));
		}
		for(int i = 0; i < sides; i++)
			addFace(Color.white, (2 * i), (2 * i) + 1, ((2 * i) + 3) % vertices.size(), ((2 * i) + 2) % vertices.size());
	}
}
class Temple {
	final static float CEILINGHEIGHT = 300, FLOORTHICKNESS = 20, DEPTH = 300, PILLARSIZE = 20;
	final static int NUMSIDES = 9;
	Temple(float x, float y, float z, float size) {
		Shape3d s = new Block3d(Color.white, x, y + FLOORTHICKNESS / 2, z, size, FLOORTHICKNESS, DEPTH);
		s.generateCollidable();
		s.add();
		new Pillar(x - size / 2 + PILLARSIZE , y + FLOORTHICKNESS + CEILINGHEIGHT / 2, z - DEPTH / 2 + PILLARSIZE, NUMSIDES, PILLARSIZE / 2, CEILINGHEIGHT).add();
		s = new Pillar(x - size / 2 + PILLARSIZE , y + FLOORTHICKNESS + CEILINGHEIGHT / 2, z + DEPTH / 2 - PILLARSIZE, NUMSIDES, PILLARSIZE / 2 , CEILINGHEIGHT);
		s.fading = true;
		s.add();
		s = new Pillar(x + size / 2 - PILLARSIZE , y + FLOORTHICKNESS + CEILINGHEIGHT / 2, z + DEPTH / 2 - PILLARSIZE, NUMSIDES, PILLARSIZE / 2, CEILINGHEIGHT);
		s.fading = true;
		s.add();
		new Block3d(Color.white, x, y + CEILINGHEIGHT + FLOORTHICKNESS * 3 / 2, z, size, FLOORTHICKNESS, DEPTH).add();
		new Pillar(x + size / 2 - PILLARSIZE , y + FLOORTHICKNESS + CEILINGHEIGHT / 2, z - DEPTH / 2 + PILLARSIZE, NUMSIDES, PILLARSIZE / 2, CEILINGHEIGHT).add();
		new Pyramid3d(x, y + CEILINGHEIGHT  + 2 * FLOORTHICKNESS, z, size, CEILINGHEIGHT / 3, DEPTH, Color.white).add();
	}
}
class SpinningJewel extends Shape3d implements Updateable {
	ArrayList<Vector4f> modelVertices = new ArrayList<Vector4f>();

	float size;
	Color color;

	SpinningJewel(float x, float y, float z, float size, Color c) {
		super(x, y, z);
		this.size = size;
		manhattanRadius.set(size, size, size);
		
		setRotationAxis(0, 1, 0);
		color = c;
		angle = 0;
		angleIncrement = 0.01f;
		
		modelVertices.add(addVertex(-size, 0, 0));
		modelVertices.add(addVertex(0, size, 0));
		modelVertices.add(addVertex(size, 0, 0));
		modelVertices.add(addVertex(0, -size, 0));
		modelVertices.add(addVertex(0, 0, size));
		modelVertices.add(addVertex(0, 0, -size));
		
		addFace(c, 0, 1, 4);
		addFace(c, 1, 2, 4);
		addFace(c, 2, 3, 4);
		addFace(c, 3, 0, 4);
		addFace(c, 1, 0, 5);
		addFace(c, 0, 3, 5);
		addFace(c, 3, 2, 5);
		addFace(c, 2, 1, 5);
	}

	public void update() {
		for(int i = 0; i < vertices.size(); i++)
			vertices.set(i, Vector.rotate(rotationAxis, modelVertices.get(i), angle));
		angle += angleIncrement;
	}
}
class ActionJewel extends SpinningJewel {
	int size = 20;
	Color color = new Color(60, 60, 230);
	Level level;
	Collidable transporter;
	ActionJewel(float x, float y, float z, Action action) {
		super(x, y, z, 20, new Color(60, 60, 230));
		transporter = new Collidable(new Rectangle(getX() - size / 2, getY() - size / 2, size, size)) {
			boolean collide(Actor a) {
				if(a.getCollisionShape().intersects(getCollisionShape())) {
					//TODO resolve concurrentException
					//action.perform(a);
					//action.perform();
					return true;
				}	
				return false;
			}
		};
		transporter.add();
		add();
	}
}
class LargeIsland extends Shape3d {
	
	LargeIsland(float x, float y, float z) {
		super(x, y, z);
	
		addVertex(-1600, 0, 100);
		addVertex(-1850,0, 150);
		addVertex(-2000, 0, 0);
		addVertex(-1800, 0, -200);
		addVertex(-1900, 20, -250);

		addVertex(-1200, 50, -200);
		addVertex(-1100, 25, 300);
		
		addVertex(-1000, 150, -300);
		addVertex(-900, 100, 300);

		addVertex(-800, 50, -300);
		addVertex(-800, 50, 300);
		
		addVertex(-650, 100, -250);
		addVertex(-700, 75, 250);
		
		addVertex(-400, 150, -250);
		addVertex(-450, 75, 200);
		
		addVertex(-200, 100, -250);
		addVertex(-200, 100, 200);
		
		addVertex(200, 100, -300);
		addVertex(200, 100, 200);
		
		addVertex(-200, 50, 250);
		addVertex(150, 50, 300);
		
		addVertex(350, 125, -350);
		addVertex(400, 100, 250);
		
		addVertex(500, 175, -300);
		addVertex(550, 150, 200);
		
		addVertex(800, 350, -250);
		addVertex(750, 300, 150);
		
		addVertex(950, 550, -150);
		addVertex(950, 500, 150);
		
		new Temple(getX(), 100 + getY(), getZ(), 200);
		
		addFace(Color.green, 0, 1, 2, 4);
		addFace(Color.green, 0, 4, 5, 6);
		addFace(Color.green, 6, 5, 7, 8);
		addFace(Color.green, 8, 7, 9, 10);
		addFace(Color.green, 10, 9, 11, 12);
		addFace(Color.green, 12, 11, 13, 14);
		addFace(Color.green, 14, 13, 15, 16);
		addFace(Color.green, 16, 15, 17, 18);
		addFace(Color.green, 20, 19, 16, 18);
		addFace(Color.green, 14, 16, 19);
		addFace(Color.green, 18, 17, 21, 22);
		addFace(Color.green, 22, 21, 23, 24);
		addFace(Color.green, 24, 23, 25, 26);
		addFace(Color.green, 26, 25, 27);
		addFace(Color.green, 26, 27, 28);

		generateMotionTracks();
	}
}
class Island extends Shape3d {
	Color topColor = new Color(31, 127, 15);
	Color bottomColor = new Color(127, 63, 31);
	
	Island(float x, float y, float z, float radius) {
		super(x, y, z);
		int numPoints = 8;
		for(float theta = 0; theta < (2 * Math.PI); theta += (2 * Math.PI) / numPoints)
			addVertex(radius * (float)Math.cos(theta), 0, radius * (float)Math.sin(theta));
		addVertex(0, -radius, 0);
		addFace(topColor, 0, 1, 2, 3, 4, 5, 6, 7); //top
		for(int i = 0; i < numPoints; i++)
			addFace(bottomColor, 9, (i + 1) % numPoints, i);
		generateMotionTrack(0);
	}
}
class GreyRoom extends Shape3d {
	int size = 500;
	Color c = Color.gray;
	GreyRoom() {
		setPosition(0, 0, 0);
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
class Lamp extends Shape3d implements Updateable {
	Actor actor;
	Color glass = new Color(1.0f, 1.0f, 1.0f, 1f);
	Color fire = Color.yellow;
	LampLight light;
	
	Lamp(float x, float y, float z, float range){
		this(x, y, z);
		light.flickerRange = range;
	}
	Lamp(float x, float y, float z) {
		super(x, y, z); 
		light = new LampLight(this);
		//Glass lamp casing
		addVertex(-5, 0, 5);
		addVertex(-5, 0, 0);
		addVertex(5, 0, 0);
		addVertex(5, 0, 5);
		
		addVertex(-5, 10, 5);
		addVertex(-5, 10, 0);
		addVertex(5, 10, 0);
		addVertex(5, 10, 5);
		
		addFace(glass, 0, 1, 2, 3); //bottom
		addFace(glass, 4, 5, 6, 7); //top
		//addFace(glass, 0, 4, 7, 3); //front
		addFace(glass, 0, 1, 5, 4); //left side
		addFace(glass, 3, 7, 6, 2); //right side
		addFace(glass, 1, 5, 6, 2); //back, for some reason
		
		//Fire inside that lamp
		addVertex(-2, 0, 2);
		addVertex(-2, 0, 0);
		addVertex(2, 0, 0);
		addVertex(2, 0, 2);
		
		addVertex(-2, 4, 2);
		addVertex(-2, 4, 0);
		addVertex(2, 4, 0);
		addVertex(2, 4, 2);
		
		addFace(fire, 8, 9, 10, 11); //bottom
		addFace(fire, 12, 13, 14, 15); //top
		addFace(fire, 8, 12, 15, 11); //front
		addFace(fire, 8, 9, 13, 12); //left side
		addFace(fire, 11, 15, 14, 10); //right side
		addFace(fire, 1, 5, 6, 2); //back, for some reason	
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
	public void update() {
		if(actor != null)
			setPosition(actor.getX() + actor.body.beltPoint.x, actor.getMinY() + actor.body.beltPoint.y, 2);
		light.update();
	}
}
class Ziggurat {
	float layerSize = 50;
	Ziggurat(float x, float y, float z, float size) {
		Block3d b;
		int numLayer = (int)(size / layerSize) / 5;
		for(int i = 0; i < numLayer; ++i) {
			b = new Block3d(new Color(0.2f, 0.8f, 0.3f), x, y + layerSize * i, z, size / numLayer * (numLayer - i), layerSize, size  / numLayer * (numLayer - i));
			b.generateCollidable();
			b.add();
		}
	}
}












