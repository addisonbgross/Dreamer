package Dreamer;


public class SprintDust extends Effect {
	
	private static final long serialVersionUID = 3851748505780547971L;
	boolean takeOff;
	
	SprintDust(Actor a) {
		actor = a;
		animation = new Animation2("sprintDust", 9, 1, 50, 60, 40);
		xOffset = 15;
		yOffset = -18;
		zOffset = 5;
		takeOff = false;
	}
	@Override
    public void update() {
		if (animation.currentIndex == animation.framesWide() - 1) {
			actor.removeStatus(Status.SPRINTING);
			animation.reset();
			animation.stop();
		} else if (actor.checkStatus(Status.SPRINTING) && actor.checkStatus(Status.GROUNDED) && !takeOff) {
			animation.start();
			takeOff = true;			
		} else if (!actor.checkStatus(Status.SPRINTING) && actor.stamina > Constants.STARTINGSTAMINA / 10)
			takeOff = false;
	}
	void followActor() {
		rePosition();
	}
}
