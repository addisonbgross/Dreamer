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
	//vision is a disposable rectangle used for activating objects, mostly
	protected static Rectangle vision = new Rectangle(0, 0, 0, 0);
	protected Collidable motion = null;
	protected float xVel = 0, yVel = 0;
	private HashSet<String> status = new HashSet<String>();
	float health = Constants.STARTINGHEALTH;
	float stamina = Constants.STARTINGSTAMINA;
	private Set<Collidable> collisionSet = new HashSet<Collidable>();
	private Collidable success = null;
	protected Vector3f lastPosition = new Vector3f();
	StatCard stats;
	Body body;
	
	ArrayList<Effect> effects = new ArrayList<Effect>();
	Sweat sweat;
	JumpDust jumpDust;
	SprintDust sprintDust;
	
	Weapon weapon;
	public int weaponStage = 0;
	public boolean airborne = false;

	Actor() {}
	Actor(StatCard sc, float x, float y) {
		status.add("initialized");
		status.add("right");
		stats = sc;
		body = new Body(stats.prefix, this);
		
		// effects
		sweat = new Sweat(this);
		jumpDust = new JumpDust(this);
		sprintDust = new SprintDust(this);
		
		setCollisionShape(new Rectangle(x, y, stats.width, stats.height));
		setPosition(x, y, 0);
	}
	// Add to active Elements
	void add() {
		if(!checkStatus("dead")) {
			super.add();
			body.add();
			sweat.add();
			jumpDust.add();
			sprintDust.add();
			
			effects.add(sweat);
			effects.add(jumpDust);
			effects.add(sprintDust);
		} else
			//TODO procedure for after dying
			System.out.println(getClass() + " has died! Cannot add to lists.");
	}
	void remove() {
		super.remove();
		body.remove();
		sweat.remove();
	}
	public void update() {
		if(health <= 0)
			die();
		
		if (!checkStatus("dead")) {
			applyGravity();
			collisionSet = findCollisions(collisionSet);
			
			suggestedTrajectory 
				= new Line(getCenterBottom(), getCenterBottom().copy().add(getVelocityVector()));
			suggestedVelocity = getVelocityVector().copy();
			
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
			//for debugging the trajectory
			if(lastPosition == null)
				lastPosition.set(getX(), getY(),getZ());
			else if(
					!(Math.abs(lastPosition.x - getX()) < 0.05f)
					||
					!(Math.abs(lastPosition.y - getY()) < 0.05f)
					||
					!(Math.abs(lastPosition.z - getZ()) < 0.05f)
					) {
				//adds trajectory lines for debugging
				new PermanentLine(suggestedTrajectory).add();
			}
			lastPosition.set(getX(), getY(),getZ());
			setVelocity(suggestedVelocity);
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
			if(checkStatus("tryright")) {
				addStatus("right");
				if (xVel < currentVel)
					xVel += (xVel < 5) ? 2 * scaledJumpVel : Constants.ACTORACCELERATION * scaledJumpVel;
				else 
					setXVel(currentVel);
			}
			if(checkStatus("tryleft")) {
				addStatus("left");
				if (xVel > -currentVel)
					xVel -= (xVel > -5) ? 2 * scaledJumpVel : Constants.ACTORACCELERATION * scaledJumpVel;
				else 
					setXVel(-currentVel);
			}  
			
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
					adjustVel(0, Constants.PLAYERJUMPVEL);
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
			applyFriction(Constants.AIRFRICTION);
	}
	void takeDamage(int damage, float weaponX) {
		if (weaponX > getX()) {
			yVel = 0.5f * damage;
			xVel = 0.8f * -damage;
		} else {
			yVel = 0.5f * damage;
			xVel = 0.8f * damage;
		}
		health -= damage;		
	}
	@Override
	//Do not remove this method!  It is used to prevent Actor-Actor collisions
	boolean collide(Actor a) {
		return false;
	}
	Set<Collidable> findCollisions(Set<Collidable> foundCollisions) {
		
		vision.setBounds(
				getMinX() + xVel - (Constants.COLLISIONINTERVAL), 
				getMinY() + yVel - (Constants.COLLISIONINTERVAL), 
				getWidth() + 2 * (Constants.COLLISIONINTERVAL + Math.abs(xVel)), 
				getHeight() + 2 *(Constants.COLLISIONINTERVAL + Math.abs(yVel))
		);
		
		foundCollisions.clear();
		for(Element e: Element.getActiveWithin(vision)) {
			//very important to not compare this to itself, infinite loop
			if(Collidable.class.isAssignableFrom(e.getClass()) && e != this) {
				 foundCollisions.add((Collidable)e);
				Dreamer.numberOfCollisions++;
			}
		}
		return foundCollisions;
	}
	Vector2f getVelocityVector() {
		return new Vector2f(xVel, yVel);
	}
	void setVelocity(Vector2f v) {
		setVelocity(v.x, v.y);
	}
	//TODO switch all velocities to vectors
	public Vector2f getVel() {
		return new Vector2f(xVel, yVel);
	}
	public void setVelocity(float x, float y) {
		xVel = x;
		yVel = y;
	}
	public void setXVel(float f)
	{
		this.xVel = f;
	}
	public void setYVel(float f)
	{
		this.yVel = f;
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
	void reset(float x, float y) {
		remove();
		setVelocity(0,0);
		health = Constants.STARTINGHEALTH;
		stamina = Constants.STARTINGSTAMINA;
		clearStatus();
		addStatus("initialized");
		setPosition(x, y, 0);
		add();
	}
	// movement and physics
	void applyGravity() {	
		yVel -= Constants.GRAVITY;
	}
	void adjustVel(float xInc, float yInc){
		xVel += xInc;
		xVel = Math.min(xVel, Constants.PLAYERMAXVEL);
		yVel += yInc;
		yVel = Math.min(yVel, Constants.PLAYERJUMPVEL);
	}
	void applyFriction(double d) {
		d = Math.min(1, d);
		d = 1 - d;
		xVel *= d;
		yVel *= d;
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
		return super.toString() + " vel (" + (int)xVel + ", " + (int)yVel + ") health " + health;
	}
}
/*
 *  Enemy --------------------------------------------------------------------------------------------------
 */
class Enemy extends Actor {
	protected static Rectangle vision;
    protected int lookX = Constants.ACTORLOOKX; //  the range that the enemy can see
    protected int lookY = Constants.ACTORLOOKY; // 
    protected int patrolRange = Constants.DEFAULTPATROLRANGE;
    protected float maxSpeed = 0;
    protected float acceleration = 0;
    protected Vector2f spawnPoint = new Vector2f();
	protected ArrayList<Trait> mind = new ArrayList<Trait>();
	protected Line lineOfSight;
	Actor target = null;
	
	Enemy(StatCard sc, float x, float y, Trait... t) {
		super(sc, x, y);
		target = null;
		lineOfSight = null;
		vision = new Rectangle(0, 0, 0, 0);
		spawnPoint.x = x;
		spawnPoint.y = y;
		for (Trait tr: t) {
			mind.add(tr);
			tr.doPassive(this);
		}
	}
	
	@Override
	public void update() {	
		look();
		if (!checkStatus("damaged"))
			for (Trait t: mind)
				t.doActive(this);
		super.update(); // collisions and death checking
	}
	void look() {
        vision.setBounds(
                getMinX() - lookX / 2,
                getMinY() - lookY / 2,
                getMinX() + lookX / 2,
                getMinY() + lookY / 2
        );
        
        setTarget(null); // reset enemy vision
        
        for(Element e: Element.getActiveWithin(vision)) {
            if(Player.class.isAssignableFrom(e.getClass())) {
            	setTarget((Player)e);
                if(getTarget().checkStatus("dead")) {
                    setTarget(null);
                    lineOfSight = null;
                }
                else if (Math.abs(e.getMinX() - getMinX()) < Math.abs(getTarget().getMinX() - getMinX())) {
                    setTarget((Player)e);
                    lineOfSight = new Line(e.getX(), e.getY(), getX(), getY());
                }
            }
        }
        
        // face the target
        if (getTarget() != null)
        	if (getTarget().getMinX() > getMinX())
        		addStatus("right");
        	else 
        		addStatus("left");
    }
	void patrol() {
		if (getTarget() == null) {
			
		}
	}
	void setTarget(Actor newTarget) {
        target = newTarget;
    }
    Actor getTarget() {
        return target;
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
	void reset() {
		super.reset(Constants.STARTX * Player.list.indexOf(this), Constants.STARTY);
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
	NinjaAlt(float x, float y, Trait... t) {
		super(new StatCard("e_ninja_", 40, 70), x, y, t);
	}
}
