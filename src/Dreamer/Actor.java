package Dreamer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

abstract class Actor extends Collidable implements Updateable {
	
	private static Set<Collidable> collisionSet = new HashSet<Collidable>();
	private static Collidable success = null;
	
	//rangeFinder is a disposable rectangle used for activating objects, mostly
	protected static Rectangle rangeFinder = new Rectangle(0, 0, 0, 0);
	private HashSet<String> status = new HashSet<String>();
	float health = Constants.STARTINGHEALTH;
	float stamina = Constants.STARTINGSTAMINA;
	private Vector2f spawnPoint = new Vector2f();
	
	Dynamics dynamics;
	StatCard stats;
	Body body;
	HashSet<Effect> effects = new HashSet<Effect>();
	Weapon weapon;
	public boolean airborne = false;

	Actor(StatCard sc, float x, float y) {
		status.add("initialized");
		status.add("right");
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
	// Add to active Elements
	void add() {
		if(!checkStatus("dead")) {
			super.add();
			body.add();
			for(Effect e: effects)
				e.add();
		} else
			//TODO procedure for after dying
			System.out.println(getClass() + " has died! Cannot add to lists.");
	}
	void remove() {
		super.remove();
		body.remove();
		for(Effect e: effects)
			e.remove();
	}
	@Override // this is necessary to update position
	void setCenterBottom(float x, float y) {
		super.remove();
		super.setCenterBottom(x, y);
		super.add();
	}
	public void update() {
		if(health <= 0)
			die();
		
		if (!checkStatus("dead")) {
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
					if(c.collide(this))
						success = c;
				}
			} while (success != null);
			
			dynamics.setVelocity(suggestedVelocity.x, suggestedVelocity.y, 0);
			setCenterBottom(suggestedTrajectory.getEnd());
			getCollisionShape().setLocation(getMinX(), getMinY());			
			
			// slower velocity if blocking
			float currentVel = (checkStatus("blocking")) ? Constants.VEL / 2 : Constants.VEL;
			
			// sprint sequence
			if (checkStatus("trysprint") && !checkStatus("blocking")) {
				if (stamina > 0) {
					currentVel = 2.5f * Constants.VEL;
					stamina--;
					addStatus("sprinting");
				} else {
					if (!checkStatus("sweating"))
						addStatus("sweating");
					removeStatus("sprinting");
				}
			} else
				removeStatus("sprinting");
			
			// Sideways movement!
			float scaledJumpVel = 1;
			if (checkStatus("jumping")) 
				scaledJumpVel = 0.4f;
			
			// left and right sequences
			float xVel = dynamics.getXVel(), yVel = dynamics.getYVel();
			if(checkStatus("tryright")) {
				addStatus("right");
				if (xVel < currentVel)
					xVel += (xVel < 5) ? 2 * scaledJumpVel : Constants.ACTORACCELERATION * scaledJumpVel;
				else 
					xVel = currentVel;
			}
			if(checkStatus("tryleft")) {
				addStatus("left");
				if (xVel > -currentVel)
					xVel -= (xVel > -5) ? 2 * scaledJumpVel : Constants.ACTORACCELERATION * scaledJumpVel;
				else 
					xVel = -currentVel;
			}  
			dynamics.setVelocity(xVel, yVel, 0);
			
			// attack sequence
			if (checkStatus("tryattack") && !checkStatus("attacking") && weapon != null) {
				if (stamina < weapon.getWeight()) {
					if (!checkStatus("sweating")) {
						addStatus("sweating");
						body.resetBody();
					}
					removeStatus("attacking");
				} else {
					addStatus("attacking");
					removeStatus("sweating");
					stamina -= weapon.getWeight();
				} 
			}
			
			// jump sequence
			if (checkStatus("tryjump") && !airborne) {
				if (checkStatus("grounded")) {
					airborne = true;
					addStatus("jumping");
					dynamics.adjustVel(0, Constants.PLAYERJUMPVEL);
				}
			} else if (!checkStatus("tryjump") && checkStatus("grounded"))
				airborne = false;
			else
				removeStatus("tryjump");
			
			// regenerate stamina
			if (stamina < Constants.STARTINGSTAMINA && !checkStatus("blocking"))
				stamina += Constants.STAMINAREGEN;
			
			// update all effect animations at correct time
			for (Effect e : effects)
				e.followActor();
		}
		
		// Reset if fallen off of level
		if(getMinY() < Constants.FALLRESET)
			reset(Constants.STARTX, Constants.STARTY);
		
		//apply friction from air
		if(checkStatus("jumping"))
			dynamics.applyFriction(Constants.AIRFRICTION);
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
		
		rangeFinder.setBounds(
				getMinX() + dynamics.getXVel() - (Constants.COLLISIONINTERVAL), 
				getMinY() + dynamics.getYVel() - (Constants.COLLISIONINTERVAL), 
				getWidth() + 2 * (Constants.COLLISIONINTERVAL + Math.abs(dynamics.getXVel())), 
				getHeight() + 2 *(Constants.COLLISIONINTERVAL + Math.abs(dynamics.getYVel()))
		);
		
		foundCollisions.clear();
		for(Element e: Element.getActiveWithin(rangeFinder)) {
			//very important to not compare this to itself, infinite loop
			if(Collidable.class.isAssignableFrom(e.getClass()) && e != this) {
				 foundCollisions.add((Collidable)e);
				Dreamer.numberOfCollisions++;
			}
		}
		return foundCollisions;
	}

	// status methods
	public boolean checkStatus(String s) {	
		return status.contains(s);
	}
	void addStatus(String s) {
		if(s == "left")
			removeStatus("right");
		else if(s == "right")
			removeStatus("left");
		else if(s == "attacking")
			removeStatus("blocking");
		else if(s == "blocking")
			removeStatus("attacking");
		else if(s == "jumping")
			removeStatus("grounded");
		else if(s == "grounded")
			removeStatus("jumping");
		status.add(s);
	}
	void removeStatus(String s) {	
		status.remove(s);
	}
	void clearStatus() {
		status.clear();
	}
	String getStatus() {	
		String out = "statuses: ";
		for(String s: status) 
			out = out.concat(" " + s);
		return out;
	}
	void printStatus() {	
		for(String s: status) 
			System.out.print(" " + s);
		System.out.println();
	}
	// reset and death
	void die() {
		addStatus("dead");
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
		addStatus("initialized");
		setPosition(x, y, 0);
		add();
	}
	public boolean isFacing(String s) {
		if (s == "left" && checkStatus("left"))
			return true;
		else if (s == "right" && checkStatus("right"))
			return true;
		else
			return false;
	}
	public boolean isFacing(Actor a) {
		assert this != null : "Ya can't check the facing of a null object";
		if (a.checkStatus("left") && checkStatus("right"))
			return true;
		else if(a.checkStatus("right") && checkStatus("left"))
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
	private Rectangle vision;
    private int lookX = Constants.ACTORLOOKX; //  the range that the enemy can see
    private int lookY = Constants.ACTORLOOKY; // 
    private int patrolRange = Constants.DEFAULTPATROLRANGE;
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
		if (!checkStatus("damaged"))
			for (Trait t: brain)
				t.doActive(this);
		super.update(); // collisions and death checking
	}
	void look() {
        vision.setBounds(
                getMinX() - lookX,
                getMinY() - lookY,
                 lookX,
                 lookY
        );
        
        // vision in direction of enemy facingte
        if (checkStatus("right"))
        	vision.setLocation((getMinX() + vision.getWidth() / 3) - lookX / 2, getY());
        else
        	vision.setLocation((getMinX() - vision.getWidth() / 3) - lookX / 2, getY());
        
        // reset enemy vision
        target = null;

        // if any Player's within vision: set to target ? null
        // TODO: make this not terrible
        for(Element e: Element.activeSet) {
        	if (e instanceof Player) {
        		Player p = (Player)e;
        		if (p.getMinX() >= vision.getCenterX() - vision.getWidth() / 2 && 
        			p.getMinX() <= vision.getCenterX() + vision.getWidth() / 2 &&
        			p.getMinY() <= vision.getCenterY() + vision.getHeight() / 2 &&
        			p.getMinY() <= vision.getCenterY() + vision.getHeight() / 2) {
		            	target = (Player)e;
		                if(target.checkStatus("dead"))
		                    target = null;
        		} 
            }
        }
        
        // face the target
        if (target != null)
        	if (target.getMinX() > getMinX())
        		addStatus("right");
        	else 
        		addStatus("left");
	}
	void setTarget(Actor newTarget) {
        target = newTarget;
    }
    Actor getTarget() {
        return target;
    }
    Rectangle getVision() {
    	return vision; 
    }
    void setAcceleration(float newAcc) {
    	acceleration = newAcc;
    }
    float getAcceleration() {
    	return acceleration;
    }
    void setMaxSpeed(float speed) {
    	maxSpeed = speed;
    }
    float getMaxSpeed() {
    	return maxSpeed;
    }
}
/*
 * Player --------------------------------------------------------------------------------------------------
 */
class Player extends Actor {
	
	static LinkedList<Player> list = new LinkedList<Player>();
	
	Player(StatCard sc, float x, float y) { 
		super(sc, x, y);
	}

	void addToGame() {
		list.add(this);
	}
	void removeFromGame() {
		Element.updateDeathSet.add(this);
		super.remove();
		this.remove();
		list.remove(this);
	}
	
	static Player getFirst() {
		return list.element();
	}
	static boolean atLeastOneLives() {
		for(Player p: list)
			if(!p.checkStatus("dead"))
				return true;
		return false;
	}
	static boolean allAlive() {
		for(Player p: list)
			if(p.checkStatus("dead"))
				return false;
		return true;
	}
}
/*
 * Characters --------------------------------------------------------------------------------------------------
 */
class Ninja extends Player {
	Ninja(float x, float y) {
		super(new StatCard("e_ninja_", 40, 70), x, y);
	}
}
class NinjaAlt extends Enemy {
	NinjaAlt(float x, float y, ArrayList<Trait> t) {
		super(new StatCard("e_ninja_", 40, 70), x, y, t);
	}
}
