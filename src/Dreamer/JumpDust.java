package Dreamer;


public class JumpDust extends Effect {
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
		} else if (actor.checkStatus("jumping") && !takeOff) {
			rePosition();
			animation.start();
			takeOff = true;			
		} else if (!actor.checkStatus("jumping"))
			takeOff = false;
	}
	void followActor() { /* stays where the Actor left the ground */ }
}
