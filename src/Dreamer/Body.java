package Dreamer;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Graphics;

public class Body extends Element implements Updateable { 	
	Animation2 legs, body, head;
	Actor actor;
	int direction;
	Random r = new Random();
	int blinkCounter;
	int LEFT = 1, RIGHT = -1;
	int xOffset, yOffset;
	int LEGSPEED = 150, BODYSPEED = 40, HEADSPEED = 150; 
	int dmgCounter;
	public int weaponStage = 0;
	
	//for adjusting the body part animations' positions
	Vector2f legsAdjust = new Vector2f(0, 0);
	Vector2f bodyAdjust = new Vector2f(1, -5);
	Vector2f headAdjust = new Vector2f(1, 30);
	Vector2f beltPoint  = new Vector2f(-10, 7);
	
	/**
	 * Create new Body and assign to Actor
	 * @param s	 name of texture sprite sheet set
	 * @param a	 Actor to attack Body to
	 */
	Body(String s, Actor a) {
		this(s + "legs", s + "body", s + "head", a);
	}
	/**
	 * Create new Body and assign to Actor
	 * @param legs	name of texture sprite sheet for legs
	 * @param body	name of texture sprite sheet for body
	 * @param head	name of texture sprite sheet for head
	 * @param a		Actor to attach Body to
	 */
	Body(String legs, String body, String head, Actor a) {
		actor = a;
		this.legs = new Animation2(legs, 6, 4, LEGSPEED);
		this.body = new Animation2(body, 6, 5, BODYSPEED);
		this.head = new Animation2(head, 6, 4, HEADSPEED);
		direction = LEFT;
		setParts();
	}
	/**
	 * Adjust Body position in relation to its Actor
	 */
	void setParts() {
		legs.setPosition(actor.getX() + legsAdjust.x, actor.getY() + legsAdjust.y, actor.getZ() + 0.01f);
		body.setPosition(actor.getX() + bodyAdjust.x, actor.getY() + bodyAdjust.y, actor.getZ());
		head.setPosition(actor.getX() + headAdjust.x, actor.getY() + headAdjust.y, actor.getZ() - 0.01f);
	}
	/**
	 * Face the animations comprising the Body
	 * @param dir	direction of facing
	 */
	void turnBody(int dir) {
		direction = dir;
		legs.setDirection(dir);
		body.setDirection(dir);
		head.setDirection(dir);
	}
	/**
	 * Body reacts to the current statuses of its Actor
	 * This will adjust the animations' current frame and speed
	 */
	void reactToStatus() {
		// commonly used variables	
		boolean attacking = actor.checkStatus("attacking");
		boolean jumping = actor.checkStatus("jumping");
		boolean climbing = actor.checkStatus("climbing");
		boolean blocking = actor.checkStatus("blocking");
		
		if (!blocking) {
			if (actor.checkStatus("right"))
				turnBody(RIGHT);	
			else 
				turnBody(LEFT);
		} 
		
		if (actor.checkStatus("damaged")) {
			carryWeapon();
			head.selectRow(2);
			legs.stop();
			body.stop();
			head.stop();
			
			++dmgCounter;
			if (dmgCounter > 35) {
				actor.removeStatus("damaged");
				dmgCounter = 0;
			}
		} else {			
			//Legs
			if (jumping) {
				legs.selectRow(2);
				legs.setSpeed(LEGSPEED);
				legs.start();
			} else if (Math.abs(actor.xVel) > 1 && !climbing) {
				legs.selectRow(1);
				legs.setSpeed(Math.abs(actor.xVel + 1) / (Constants.VEL / 40));
				legs.start();
			} else if ((actor.checkStatus("up") || actor.checkStatus("down")) && climbing) {
				legs.selectRow(3);
				legs.setSpeed(Math.abs(actor.yVel + 1) / (Constants.VEL / 200));
				legs.start();
			} else if (climbing) {
				legs.selectRow(3);
				legs.setSpeed(Math.abs(actor.yVel + 1) / (Constants.VEL / 200));
				legs.stop();
			} else {
				legs.selectRow(0);
				legs.reset();
			}				
			
			//body
			if (blocking) {
				carryWeapon(3);
				body.stop();
				body.reset();
				body.selectRow(4);
			} else if (attacking) {
				carryWeapon(body.currentIndex);
				body.setLooping(false);
				body.selectRow(2);
				body.start();
			} else if (jumping) {
				carryWeapon();
				body.setLooping(true);
				body.selectRow(1);
				body.stop();
			} else if (Math.abs(actor.xVel) > 1) {    
				carryWeapon();
				body.setLooping(true);
				body.setSpeed(BODYSPEED);
				body.selectRow(1);
				body.start();			
			} else if ((actor.checkStatus("up") || actor.checkStatus("down")) && climbing) {
				carryWeapon();
				body.setLooping(true);
				body.selectRow(3);
				body.setSpeed(Math.abs(actor.yVel + 1) / (Constants.VEL / 200));
				body.start();
			} else if (climbing) {
				carryWeapon();
				body.setLooping(true);
				body.selectRow(3);
				body.setSpeed(Math.abs(actor.yVel + 1) / (Constants.VEL / 200));
				body.stop();
			} else {
				carryWeapon();
				body.setLooping(true);
				body.selectRow(0);
				body.reset();
			}
			
			//head
			if (attacking || blocking) {
				head.selectRow(1);
				head.start();
			} else if ((actor.checkStatus("up") || actor.checkStatus("down")) && climbing) {
				head.selectRow(3);
				head.setSpeed(Math.abs(actor.yVel + 1) / (Constants.VEL / 200));
				head.start();
			} else {
				if (blinkCounter > 0){
					blinkCounter--;
				} else if(r.nextInt(200) < 3) {
					head.selectRow(1);
					blinkCounter = r.nextInt(10) + 5;
				} else 
					head.selectRow(0);
			}
		}
	}
	/**
	 * Carry the Actor's Weapon in its standard pose
	 */
	void carryWeapon() {
		carryWeapon(0);
	}
	/**
	 * Carry the Actor's Weapon in the current frame
	 * of its attack sequence
	 * @param i	 current index in attack sequence
	 */
	void carryWeapon(int i) {
		weaponStage = i;
	}
	/**
	 * Update this object per game iteration
	 */
	public void update() {
		reactToStatus();
		setParts();
	}
	/**
	 * Add this to current active Elements
	 */
	@Override
	void add() {
		super.add();
		this.legs.add();
		this.body.add();
		this.head.add();
	}
	/**
	 * Remove this from current active Elements
	 */
	@Override
	void remove() {
		super.add();
		this.legs.remove();
		this.body.remove();
		this.head.remove();
	}
	@Override
	void draw(Graphics g) {
		//stupid
	}
}

