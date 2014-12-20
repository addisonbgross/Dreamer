package Dreamer;

import org.newdawn.slick.Graphics;

public class Sweat extends Element implements Effect, Updateable {
	Animation2 animation;
	
	Sweat(float x, float y, float z) {
		animation = new Animation2("sweat", 8, 1, 155, 20, 30);
		animation.setPosition(x, y, z);
		animation.add();
		animation.start();
	}

	@Override
    public void start() {
	    animation.start();
    }
	@Override
    public void stop() {
	    animation.stop();
    }
	@Override
	public void remove() {
		super.remove();
		animation.remove();
	}
	@Override
    public void update() {
	    if (animation.currentIndex == animation.framesWide() - 1)
	    	remove();
    }
	@Override
    void draw(Graphics g) {
	    // bollocks
    }

}
