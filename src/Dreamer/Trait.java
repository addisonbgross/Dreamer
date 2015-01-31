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
	
	Speed() {
		super(1.0f);
	}
	
	Speed(float i) {
		super(i);
	}
	
	public String toString() {
		return "Speed: " + BASESPEED * intensity;
	}
	void doPassive(Enemy self) {
		self.maxSpeed = BASESPEED * intensity;
		self.acceleration = 2f;
	}
}
/**
 * Allows the NPC to follow its target
 */
class Follow extends Trait {
	Follow() {
		super(1.0f);
	}
	
	Follow(float i) {
		super(i);
	}
	
	public String toString() {
		return "Follow";
	}
	/**
	 * If target is is too far away, follow them
	 */
	void doActive(Enemy self) {
		if (self.getTarget() != null) {
			if (self.getTarget().getMinX() - self.getMinX() < -Constants.ENEMYATTACKRANGE) {
				self.xVel = Math.max(self.xVel -= self.acceleration, -self.maxSpeed);
			} else if (self.getTarget().getMinX() - self.getMinX() > Constants.ENEMYATTACKRANGE ) {
				self.xVel = Math.min(self.xVel += self.acceleration, self.maxSpeed);
			}
		}
	}
}
/**
 * NPC will jump to chase their target
 */
class Jumpy extends Trait {
	Jumpy() {
		super(1.0f);
	}
	
	Jumpy(int i) {
		super(i);
	}
	
	public String toString() {
		return "Jumpy";
	}	
	void doActive(Enemy self) {
		if (self.target != null)
			if(self.findDistanceTo(self.target) < Constants.ENEMYJUMPRANGEX)
					if(self.target.getMinY() > self.getMinY() + Constants.JUMPBUFFER && !self.checkStatus("jumping"))
						if(self.checkStatus("grounded")) {
							self.addStatus("jumping");
							self.adjustVel(0, Constants.PLAYERJUMPVEL);
							self.removeStatus("grounded");
							self.motion = null;
						}
	}
}
/**
 * NPC will attack target if it is within attack range
 */
class Violent extends Trait {
	int attackTime = Constants.ENEMYATTACKWAIT;
	Violent() {
		super(1.0f);
	}
	Violent(int i) {
		super(i);
	}
	public String toString() {
		return "Violent";
	}
	void doActive(Enemy self) {
		if (self.target != null) {
			if (self.findDistanceTo(self.target) < Constants.ENEMYATTACKRANGE) {
				--attackTime;
				if (attackTime <= 0) {
					self.addStatus("attacking");
					attackTime = Constants.ENEMYATTACKWAIT;
				}
			} else
				self.removeStatus("attacking");
		}
	}
}
class Armourer extends Trait {
	Armourer() {
		super(1.0f);
	}
	Armourer(float i) {
		super(i);
	}
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
	int stanceRange = 100, currentRange;
	Random r = new Random();
	float distance;
	Duelist() {
		super(1.0f);
	}
	Duelist(float i) {
		super(i);
	}
	public String toString() {
		return "Duelist";
	}
	/**
	 * Maintain a pseudo-random distance from the target while
	 * blocking incoming attacks
	 */
	void doActive(Enemy self) {
		// randomize duel distance
		currentRange = stanceRange + r.nextInt(150);
		if (self.target != null && !self.checkStatus("attacking") && self.isFacing(self.target)) {
			distance = Math.abs(self.target.getX() - self.getX());
			// if not within duel range
			if (distance > currentRange) {
				if (self.target.getX() > self.getX()) {
					self.xVel = Math.max(self.xVel += self.acceleration, self.maxSpeed);
					self.addStatus("right");
					self.removeStatus("left");
				} else {
					self.xVel = Math.max(self.xVel -= self.acceleration, -self.maxSpeed);
					self.addStatus("left");
					self.removeStatus("right");
				}
			} else {
				if (self.target.getX() > self.getX()) {
					self.xVel = Math.max(self.xVel -= self.acceleration, -self.maxSpeed);
					self.addStatus("right");
					self.removeStatus("left");
				} else {
					self.xVel = Math.max(self.xVel += self.acceleration, self.maxSpeed);
					self.addStatus("left");
					self.removeStatus("right");
				}
				self.addStatus("blocking");
			}
		} else
			self.removeStatus("blocking");
	}
}
