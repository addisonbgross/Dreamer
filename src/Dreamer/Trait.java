package Dreamer;

import java.util.Random;

/**
 * Traits form the intelligence of non-player characters. Each
 * Trait can have an active and passive trait. Passive Traits
 * will apply an effect to the recipient's stats, such as changing
 * their health, speed, stamina, etc... Active Traits will be
 * activated each game update, and they will dictate the attitude
 * and actions of the NPC
 */
abstract class Trait {
	protected float intensity; // 0.0 -> 1.0
	Trait() { intensity = 1; }
	Trait(float i) {
		if (intensity > 0 && intensity <= 1.0)
			intensity = i;
		else
			intensity = 0.5f;
	}
	void doActive(Enemy self){};
	void doPassive(Enemy self){};
}

/**
 * Base walking speed
 */
class Speed extends Trait {	
	int BASESPEED = 10;
	
	Speed() { super(1.0f); }
	Speed(float i) { super(i); }
	
	public String toString() { return "Speed: " + BASESPEED * intensity; }

	void doPassive(Enemy self) {
		self.setMaxSpeed(BASESPEED * intensity);
		self.setAcceleration(2f);
	}
}
/**
 * Allows the NPC to follow its target
 */
class Follow extends Trait {
	Random r;
	int followDistance;
	
	Follow() { this(1.0f); }
	Follow(float i) {
		super(i);
		r = new Random();
		followDistance = 10 + (2 * r.nextInt(5));  // fairly arbitrary
	}
	
	public String toString() { return "Follow"; }
	
	/**
	 * If target is is too far away, follow them
	 */
	void doActive(Enemy self) {
		float xVel = self.dynamics.getXVel();
		
		if (self.getTarget() != null) {		
			if (self.getTarget().getMinX() - self.getMinX() < -Constants.ENEMYATTACKRANGE - followDistance) {
				xVel = Math.max(xVel -= self.getAcceleration(), -self.getMaxSpeed());
			} else if (self.getTarget().getMinX() - self.getMinX() > Constants.ENEMYATTACKRANGE + followDistance) {
				xVel = Math.min(xVel += self.getAcceleration(), self.getMaxSpeed());
			}
		} else {
			xVel = 0;
		}
		self.dynamics.setXVel(xVel);
	}
}
/**
 * NPC will jump to chase their target
 */
class Jumpy extends Trait {
	Jumpy() { super(1.0f); }
	Jumpy(int i) { super(i); }
	
	public String toString() { return "Jumpy"; }	
	
	void doActive(Enemy self) {
		if (self.getTarget() != null) {
			if(self.findDistanceTo(self.getTarget()) < Constants.ENEMYJUMPRANGEX) {
				if(self.getTarget().getMinY() > self.getMinY() + Constants.JUMPBUFFER && !self.checkStatus("jumping")) {
					if(self.checkStatus("grounded")) {
						self.addStatus("tryjump");
					}
				}
			}
		}
	}
}
/**
 * NPC will attack target if it is within attack range
 */
class Violent extends Trait {
	Violent() { super(1.0f); }
	Violent(int i) { super(i); }

	public String toString() { return "Violent"; }

	void doActive(Enemy self) {
		if (self.getTarget() != null) {
			if (self.findDistanceTo(self.getTarget()) < Constants.ENEMYATTACKRANGE) {
				self.addStatus("tryattack");
			} else {
				self.removeStatus("tryattack");
			}
		}
	}
}
class Armourer extends Trait {
	Armourer() { super(1.0f); }
	Armourer(float i) { super(i); }

	void doActive(Enemy self) {
		if (self.getTarget() != null) {
			// TODO
			// Chase down gear if one finds themselves unarmed
			// and with a target
		}
	}
}
/**
 * NPC will engage in sword play with their target
 */
class Duelist extends Trait {
	int stanceRange = 20, duelRange;
	Random r = new Random();
	float distanceToTarget;
	
	Duelist() { super(1.0f); }
	Duelist(float i) { super(i); }
	
	public String toString() { return "Duelist"; }
	
	/**
	 * Maintain a pseudo-random distance from the target while
	 * blocking incoming attacks
	 */
	void doActive(Enemy self) {
		// randomize duel distance
		duelRange = stanceRange + r.nextInt(300);

		if (self.getTarget() != null && !self.checkStatus("attacking") && self.isFacing(self.getTarget())) {
			distanceToTarget = Math.abs(self.getTarget().getX() - self.getX());

			float xVel = self.dynamics.getXVel();

			// if target within duel range
			if (distanceToTarget < duelRange) {
				if (self.getTarget().getX() > self.getX()) {
					xVel = Math.max(xVel -= self.getAcceleration(), -self.getMaxSpeed());
					self.addStatus("right");
					self.removeStatus("left");
				} else {
					xVel = Math.max(xVel += self.getAcceleration(), self.getMaxSpeed());
					self.addStatus("left");
					self.removeStatus("right");
				}
				self.addStatus("blocking");
			}
			
			self.dynamics.setXVel(xVel);
		} else
			self.removeStatus("blocking");
	}
}
