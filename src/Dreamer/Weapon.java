package Dreamer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

abstract class Weapon extends Shape3d implements Updateable {
	Face f;
	Actor actor;
	String name = "";
	int damage, carryHeight, width, height, cuttingEdge;
	int direction, LEFT = 1, RIGHT = -1;
	WeaponCollision weaponCollision;
	Line weaponLine; 
	protected Vector2f blockPosition;
	protected Vector2f weaponPoint = new Vector2f();
	protected float weaponAngle = 0, blockAngle = 20;
	int[] attackAngles;
	int[] attackOffsetX;
	int[] attackOffsetY;


	Weapon(float x, float y, float z) {
		
	}
	Weapon(Actor a) {
		weaponCollision = new WeaponCollision(this); 
		attach(a);
		//linkPositionTo(a);
		addVertex(0, 0, 0);
		addVertex(0, 100, 0);
		addVertex(100, 100, 0);
		addVertex(100, 0, 0);
	}
	@Override
	boolean isVisible() {
		return true;
	}
	@Override
	void draw(Graphics g) {
		super.draw(g);
		if(Element.debug)
			if(weaponLine != null)
				drawShape(weaponLine, Color.black, g);
	}	
	void makeFace() {
		if(!name.equals("")) {
			f = new Face(Library.getTexture(name), new Color(1, 1, 1, 1.0f), 0, 1, 2, 3);
			width = Library.getImage(name).getWidth(); //.getTextureWidth();
			height = Library.getImage(name).getHeight();
			f.setTexturePoints(0, 0,  textureStretch(width), textureStretch(height));
			addFace(f);
		}
	}
	
	void attack() {
		Actor temp;
		updateCollision();
		Shape s = weaponCollision.getCollisionShape();
		for(Element e: Element.getActiveWithin(s)) {
			//very important to not compare this to itself, infinite loop
			if(Actor.class.isAssignableFrom(e.getClass()) && e != actor) {
				temp = (Actor)e;
				if (temp.getCollisionShape().intersects(s) && 
					temp.checkStatus("blocking")) {
						temp.xVel += (temp.checkStatus("left"))?2:-2;
						actor.xVel += (temp.checkStatus("left"))?-7:7;
				} else if(temp.getCollisionShape().intersects(s) && !temp.checkStatus("damaged")) {
					temp.takeDamage(this.damage, s.getCenterX());
					temp.yVel = 30;
					temp.addStatus("damaged");
					temp.xVel += (temp.checkStatus("left"))?8:-8;
				}	
			}
		}
		weaponCollision.remove();
	}
	void updateCollision() {
		weaponCollision.remove();
		weaponCollision.setCollisionShape(new Line(
				weaponPoint.x, 
				weaponPoint.y, 
				weaponPoint.x + cuttingEdge * (float)Math.sin((Math.PI * weaponAngle) / 180), 
				weaponPoint.y + cuttingEdge * (float)Math.cos((Math.PI * weaponAngle) / 180)
				)
		);
		weaponCollision.add();
	}
	void attach(Actor a) {
		if (a.currentWeapon  != this) {
			actor = a;
			if(a.currentWeapon == null)
				a.currentWeapon = this;
			else {
				//TODO below call is causing some kind of infinite recursion?
				//a.weapon.updateCollision();
				a.currentWeapon.detach();
				a.currentWeapon = this;
			}
			updateCollision();
		}
	}
	void detach() {
		actor.currentWeapon.updateCollision();
		actor.currentWeapon = null;
		actor = null;
	}
	public void update() {
		if(actor != null) {
			if(actor.checkStatus("dead")) {
				updateCollision();
				detach();
				return;
			}
			
			// Set weapon on front or back of Actor depending on ladder
			setZ((actor.checkStatus("climbing") || actor.checkStatus("blocking")?2f:-2f));

			manhattanRadius.set(100, 100, 0);
			
			if (actor.checkStatus("attacking"))
				attack();
			// Offset this from actor's center
			int xOffset, yOffset, xBlockOffset = 0, yBlockOffset = 0;	
			int rotation = actor.body.weaponStage;
			
			// Set weapon in direction of actor
			int direction;
			if (actor.checkStatus("blocking")) {
				direction = actor.body.direction;
				xBlockOffset = (int)blockPosition.x;
				yBlockOffset = (int)blockPosition.y;
				weaponAngle = -direction * blockAngle;
			} else {
				direction = (actor.checkStatus("left"))?LEFT:RIGHT;
				weaponAngle = -direction * attackAngles[rotation];
			}
			xOffset = attackOffsetX[rotation] + xBlockOffset;
			yOffset = carryHeight + yBlockOffset;	
			yOffset += attackOffsetY[rotation];
			weaponPoint.set(actor.getX() - direction * xOffset, actor.getY() + yOffset);
			
			float cos = (float)Math.cos((Math.PI * weaponAngle) / 180);
			float sin = (float)Math.sin((Math.PI * weaponAngle) / 180);

			vertices.get(0).set(
					weaponPoint.x - cos * width / 2, 
					weaponPoint.y + sin * width / 2,
					-0.1f
					); 
			vertices.get(1).set(
					weaponPoint.x + sin * height - cos * width / 2,
					weaponPoint.y + cos * height + sin * width / 2,
					-0.1f
					); 
			vertices.get(2).set(
					weaponPoint.x + sin * height + cos * width / 2,
					weaponPoint.y + cos * height - sin * width / 2,
					-0.1f
					); 
			vertices.get(3).set(
					weaponPoint.x + cos * width / 2, 
					weaponPoint.y - sin * width / 2,
					-0.1f
					); 
		}
	}
}
class Katana extends Weapon {
	Katana(Actor a) {
		super(a);
		name = "katana";
		damage = 10;
		carryHeight = 50;
		cuttingEdge = 100;
		
		blockPosition = new Vector2f(-2, -28);
		attackAngles = new int[]{250, 275, 310, 325, 60, 95};
		attackOffsetX = new int[]{48, 15, 22, 24, 22, 15};
		attackOffsetY = new int[]{-31, -38, -38, -39, -47, -57};
		makeFace();
	}
}
class Battleaxe extends Weapon {	
	Battleaxe(Actor a) {
		super(a);
		name = "battleaxe";
		damage = 15;
		carryHeight = 3;
		makeFace();
	}
}
class Naginata extends Weapon {
	Vector2f block = new Vector2f(-2, -28);
	Naginata(Actor a) {
		super(a);
		name = "naginata";
		damage = 30;
		carryHeight = 55;
		cuttingEdge = 160;
		
		blockPosition = new Vector2f(15, -15);
		attackAngles = new int[]{-25, 0, 10, 30, 60, 90};
		attackOffsetX = new int[]{20, 20, 15, -10, -30, -20};
		attackOffsetY = new int[] {-90, -85, -85, -97, -85, -65};
		makeFace();
	}
}
class WeaponCollision extends Collidable {
	Weapon weapon;
	WeaponCollision(Weapon w) {
		weapon = w;
	}
	@Override
	boolean collide(Actor a) {
		if(weapon.actor == null)
			if(a.getCollisionShape().intersects(getCollisionShape()) && a.checkStatus("acting")) {
				weapon.attach(a);
				a.removeStatus("acting");
				return true;
			}
		return false;
	}
}
