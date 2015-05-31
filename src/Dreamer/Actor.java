package Dreamer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import Dreamer.enums.Status;
import Dreamer.interfaces.Updateable;
import Dreamer.interfaces.Manageable;
import static Dreamer.enums.Status.*;

public abstract class Actor extends Collidable 
implements Manageable, Updateable {
	
	private static final long serialVersionUID = -8711854287889823062L;
	private static Set<Collidable> collisionSet = new HashSet<Collidable>();
	private static Collidable success = null;
	
	Rectangle rectangle;
	private HashSet<Status> status = new HashSet<Status>();
	float health = Constants.STARTINGHEALTH;
	float stamina = Constants.STARTINGSTAMINA;
	private Vector2f spawnPoint = new Vector2f();
	
	Dynamics dynamics;
	StatCard stats;
	Body body;
	HashSet<Effect> effects = new HashSet<Effect>();
	Weapon weapon;

	Actor(StatCard sc, float x, float y) {

		status.add(INITIALIZED);
		status.add(RIGHT);
		stats = sc;
		body = new Body(stats.prefix, this);
		spawnPoint.x = x;
		spawnPoint.y = y;
		dynamics = new Dynamics();
		
		// effects	
		effects.add(new Sweat(this));
		effects.add(new JumpDust(this));
		effects.add(new SprintDust(this));
		
		setCollisionShape(new Rectangle(x, y, stats.width, stats.height));
		setPosition(x, y, 0);
	}
	
	public java.util.Collection<Manageable> getChildren() {
		
		java.util.Collection<Manageable> children = new java.util.ArrayList<>();
		children.add(body);
		children.addAll(effects);
		return children;
	}
	
	@Override // this is necessary to update position
	void setCenterBottom(float x, float y) {
	
		remove();
		super.setCenterBottom(x, y);
		add();
	}
	
	public void update() {
		
		if(health <= 0)
			die();
		
		if (!checkStatus(DEAD)) {
			dynamics.applyGravity();
			collisionSet = findCollisions(collisionSet);
			
			suggestedTrajectory 
				= new Line(
						getCenterBottom().x,
						getCenterBottom().y,
						getCenterBottom().x + dynamics.getXVel(),
						getCenterBottom().y + dynamics.getYVel()
						);
			suggestedVelocity.set(dynamics.getXVel(), dynamics.getYVel());
			
			//this recursively checks all collisions to ensure that
			//the suggestedTrajectory is within bounds of all
			//Collidable objects
			do {
				if(success != null) {
					collisionSet.remove(success);
					success = null;
				}
				for(Collidable c: collisionSet) {
					// eSystem.out.println(c.toString());
					if(c.collide(this))
						success = c;
				}
			} while (success != null);
			
			dynamics.setVelocity(suggestedVelocity.x, suggestedVelocity.y, 0);
			setCenterBottom(suggestedTrajectory.getEnd());
			getCollisionShape().setLocation(getMinX(), getMinY());			
			
			// slower velocity if blocking
			float currentVel = (checkStatus(BLOCKING)) ? Constants.VEL / 2 : Constants.VEL;
			
			// sprint sequence
			if (checkStatus(TRYSPRINT) && !checkStatus(BLOCKING)) {
				if (stamina > 0) {
					currentVel = 2.5f * Constants.VEL;
					stamina--;
					addStatus(SPRINTING);
				} else {
					if (!checkStatus(SWEATING))
						addStatus(SWEATING);
					removeStatus(SPRINTING);
				}
			} else
				removeStatus(SPRINTING);
			
			// Sideways movement!
			float scaledJumpVel = 1;
			if (checkStatus(JUMPING)) 
				scaledJumpVel = 0.4f;
			
			// left and right sequences
			float xVel = dynamics.getXVel(), yVel = dynamics.getYVel();
			if(checkStatus(TRYRIGHT)) {
				addStatus(RIGHT);
				if (xVel < currentVel)
					xVel += (xVel < 5) ? 2 * scaledJumpVel : Constants.ACTORACCELERATION * scaledJumpVel;
				else 
					xVel = currentVel;
			}
			if(checkStatus(TRYLEFT)) {
				addStatus(LEFT);
				if (xVel > -currentVel)
					xVel -= (xVel > -5) ? 2 * scaledJumpVel : Constants.ACTORACCELERATION * scaledJumpVel;
				else 
					xVel = -currentVel;
			}  
			dynamics.setVelocity(xVel, yVel, 0);
			
			// attack sequence
			if (checkStatus(TRYATTACK) && !checkStatus(ATTACKING) && weapon != null) {
				if (stamina < weapon.getWeight()) {
					if (!checkStatus(SWEATING)) {
						addStatus(SWEATING);
						body.resetBody();
					}
					removeStatus(ATTACKING);
				} else {
					addStatus(ATTACKING);
					removeStatus(SWEATING);
					stamina -= weapon.getWeight();
				} 
			}

			// jump sequence
			if (checkStatus(TRYJUMP) && !checkStatus(JUMPING)) {
				if (checkStatus(GROUNDED)) {
					addStatus(JUMPING);
					dynamics.adjustVel(0, Constants.PLAYERJUMPVEL);
				}
			} else {
				removeStatus(TRYJUMP);
			}
			
			// regenerate stamina
			if (stamina < Constants.STARTINGSTAMINA && !checkStatus(BLOCKING))
				stamina += Constants.STAMINAREGEN;
			
			// update all effect animations at correct time
			for (Effect e : effects)
				e.followActor();
		}
		
		// Reset if fallen off of level
		if(getMinY() < Constants.FALLRESET)
			reset(Constants.STARTX, Constants.STARTY);
	}
	void takeDamage(int damage, float weaponX) {
		if (weaponX > getX()) {
			dynamics.setVelocity(0.8f * -damage, 0.5f * damage, 0);
		} else {
			dynamics.setVelocity(0.8f * damage, 0.5f * damage, 0);
		}
		health -= damage;		
	}
	@Override
	//Do not remove this method!  It is used to prevent Actor-Actor collisions
	boolean collide(Actor a) {
		return false;
	}
	Set<Collidable> findCollisions(Set<Collidable> foundCollisions) {
		
		rectangle = new Rectangle(
			getMinX() + dynamics.getXVel() - (Constants.COLLISIONINTERVAL),
			getMinY() + dynamics.getYVel() - (Constants.COLLISIONINTERVAL),
			getWidth() + 2 * (Constants.COLLISIONINTERVAL + Math.abs(dynamics.getXVel())),
			getHeight() + 2 *(Constants.COLLISIONINTERVAL + Math.abs(dynamics.getYVel()))
		);
		foundCollisions.clear();
		for(Positionable p: Collidable.getActiveWithin(rectangle)) {
			//very important to not compare this to itself, infinite loop
			if(Collidable.class.isAssignableFrom(p.getClass()) && p != this) {
				 foundCollisions.add((Collidable)p);
				PerformanceMonitor.numberOfCollisions++;
			}
		}
		return foundCollisions;
	}

	// status methods
	public boolean checkStatus(Status s) {	
		return status.contains(s);
	}
	void addStatus(Status s) {
		if(s == LEFT)
			removeStatus(RIGHT);
		else if(s == RIGHT)
			removeStatus(LEFT);
		else if(s == ATTACKING)
			removeStatus(BLOCKING);
		else if(s == BLOCKING)
			removeStatus(ATTACKING);
		else if(s == JUMPING)
			removeStatus(GROUNDED);
		else if(s == GROUNDED)
			removeStatus(JUMPING);
		status.add(s);
	}
	void removeStatus(Status s) {	
		status.remove(s);
	}
	void clearStatus() {
		status.clear();
	}
	// reset and death
	void die() {
		addStatus(DEAD);
		remove();
	}
	void reset() {
		this.reset(spawnPoint.x, spawnPoint.y);
	}
	void reset(float x, float y) {
		remove();
		dynamics.setVelocity(0, 0, 0);
		health = Constants.STARTINGHEALTH;
		stamina = Constants.STARTINGSTAMINA;
		clearStatus();
		addStatus(INITIALIZED);
		setPosition(x, y, 0);
		add();
	}
	void clearMovementStatus() {
		removeStatus(TRYLEFT);
		removeStatus(TRYRIGHT);
		removeStatus(TRYSPRINT);
		removeStatus(TRYJUMP);
	}
	void switchFacing() {
		if (checkStatus(LEFT)) {
			addStatus(TRYRIGHT);
		} else {
			addStatus(TRYLEFT);
		}
	}
	public boolean isFacing(String s) {
		if (s == "left" && checkStatus(LEFT))
			return true;
		else if (s == "right" && checkStatus(RIGHT))
			return true;
		else
			return false;
	}
	public boolean isFacing(Actor a) {
		assert this != null : "Ya can't check the facing of a null object";
		if (a.checkStatus(LEFT) && checkStatus(RIGHT))
			return true;
		else if(a.checkStatus(RIGHT) && checkStatus(LEFT))
			return true;
		else
			return false;
	}
	public String toString() {
		return super.toString() + " vel (" + (int)dynamics.getXVel() + ", " + (int)dynamics.getYVel() + ") health " + health;
	}
}
/*
 *  Enemy --------------------------------------------------------------------------------------------------
 */
class Enemy extends Actor {
	
	private static final long serialVersionUID = 429443147595796339L;
	private Rectangle vision;
    private int lookX = Constants.ACTORLOOKX; //  the range that the enemy can see
    private int lookY = Constants.ACTORLOOKY; // 
    private float maxSpeed = 0;
    private float acceleration = 0;
    protected ArrayList<Trait> brain;
	private Actor target = null;
	
	Enemy(StatCard sc, float x, float y, ArrayList<Trait> t) {
		super(sc, x, y);
		target = null;
		vision = new Rectangle(getMinX(), getMinY(), 0, 0);
		brain = t;
		for (Trait tr: brain) {
			tr.doPassive(this);
		}
	}
	
	@Override
	public void update() {	
		look();
		
		// think clearly
		if (!checkStatus(DAMAGED)) { 
			for (Trait t: brain) {
				t.doActive(this);
			}
		// turn to face the usurper
		} else {	
			if (target == null) {
				switchFacing();
			}
		}

		clearMovementStatus();
		super.update(); // collisions and death checking
	}
	void look() {
		
        vision.setBounds(
                getMinX() - lookX,
                getMinY() - lookY,
                lookX,
                lookY
        );
        
        // vision in direction of enemy facing
        if (checkStatus(RIGHT))
        	vision.setLocation((getMinX() + vision.getWidth() / 3) - lookX / 2, getY());
        else
        	vision.setLocation((getMinX() - vision.getWidth() / 3) - lookX / 2, getY());
        
        // reset enemy vision
        target = null;

        // [if any Player's within vision : set to target ? null]
        // TODO: make this not terrible
        for(Player p : Player.list) {
    	
    		if (
    			p.getMinX() >= vision.getCenterX() - vision.getWidth() / 2 && 
    			p.getMinX() <= vision.getCenterX() + vision.getWidth() / 2 &&
    			p.getMinY() <= vision.getCenterY() + vision.getHeight() / 2 &&
    			p.getMinY() <= vision.getCenterY() + vision.getHeight() / 2
    		) {     			
	            	target = p.checkStatus(DEAD)? null : p;
    		} 
        }
        
        // face the target
        if (target != null)
        	
        	if (target.getMinX() > getMinX())
        		addStatus(RIGHT);
        	else 
        		addStatus(LEFT);
	}
	
	void setTarget(Actor newTarget) { target = newTarget; }
    
	Actor getTarget() { return target; }
	
    Rectangle getVision() { return vision; }
    
    void setAcceleration(float newAcc) { acceleration = newAcc; }
    
    float getAcceleration() { return acceleration; }
    
    void setMaxSpeed(float speed) { maxSpeed = speed; }
    
    float getMaxSpeed() { return maxSpeed; }
}
/*
 * Player --------------------------------------------------------------------------------------------------
 */
class Player extends Actor {
	
	private static final long serialVersionUID = 6260650017867646859L;
	static LinkedList<Player> list = new LinkedList<Player>();
	
	Player(StatCard sc, float x, float y) { 
		
		super(sc, x, y);
		list.add(this);
	}
	
	static Player getFirst() { return list.element(); }
	
	static boolean atLeastOneLives() {
		
		for(Player p: list)
			if(!p.checkStatus(DEAD))
				return true;
		return false;
	}
	static boolean allAlive() {
		
		for(Player p: list)
			if(p.checkStatus(DEAD))
				return false;
		return true;
	}
}
/*
 * Characters --------------------------------------------------------------------------------------------------
 */
class Ninja extends Player {
	private static final long serialVersionUID = 5602718421775517193L;

	Ninja(float x, float y) {
		super(new StatCard("e_ninja_", 40, 70), x, y);
	}
}
class NinjaAlt extends Enemy {
	private static final long serialVersionUID = 3832411639799205188L;

	NinjaAlt(float x, float y, ArrayList<Trait> t) {
		super(new StatCard("e_ninja_", 40, 70), x, y, t);
	}
}
class GrassSoldier extends Enemy {
	private static final long serialVersionUID = -5182787543477320247L;

	GrassSoldier(float x, float y, ArrayList<Trait> t) {
		super(new StatCard("e_grasssoldier_", 40, 70), x, y, t);
	}
}
class Skeleton extends Enemy {
	private static final long serialVersionUID = 6662913320740461181L;

	Skeleton(float x, float y, ArrayList<Trait> t) {
		super(new StatCard("e_skeleton_", 40, 70), x, y, t);
	}
}
