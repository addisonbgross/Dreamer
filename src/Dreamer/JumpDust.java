package Dreamer;

import Dreamer.enums.Status;


public class JumpDust extends Effect {
	
	private static final long serialVersionUID = 4389369971340447586L;
	boolean takeOff;
	
	JumpDust(Actor a) {
		actor = a;
		animation = new Animation2("jumpDust", 9, 1, 50, 120, 80);
		xOffset = 0;
		yOffset = -7;
		zOffset = 5;
		takeOff = false;
	}
	
	@Override
    public void update() {
		if (animation.currentIndex == animation.framesWide() - 1) {
			animation.reset();
			animation.stop();
		} else if (actor.checkStatus(Status.JUMPING) && !takeOff) {
			rePosition();
			animation.start();
			takeOff = true;			
		} else if (!actor.checkStatus(Status.JUMPING))
			takeOff = false;
	}
	void followActor() { /* stays where the Actor left the ground */ }
}
