package Dreamer;

import enums.Status;


public class Sweat extends Effect {	

	private static final long serialVersionUID = 2081696229398023849L;

	Sweat(Actor a) {
		actor = a;
		animation = new Animation2("sweat", 9, 1, 100, 20, 30);
		xOffset = 20;
		yOffset = 30;
		zOffset = 5;
	}
	@Override
    public void update() {
		if (actor.checkStatus(Status.SWEATING)) {
			if (!animation.running)
				animation.start();
				
			if (animation.currentIndex == animation.framesWide() - 1) {
				actor.removeStatus(Status.SWEATING);
				animation.reset();
				animation.stop();
			}
		}
	}
	void followActor() {
		rePosition();
	}
}
