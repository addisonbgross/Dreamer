package Dreamer;

import org.newdawn.slick.Graphics;

public class Sweat extends Element implements Effect, Updateable {
	Animation2 animation;
	int LEFT = 1, RIGHT = -1;
	Actor a;
	
	Sweat(Actor actor) {
		a = actor;
		animation = new Animation2("sweat", 8, 1, 155, 20, 30);
		add();
	}

	@Override
	public void add() {
		super.add();
		animation.add();
		animation.start();
	}
	@Override
	public void remove() {
		super.remove();
		animation.remove();
	}
	@Override
    public void update() {
		if (a.isFacing("left")) {
			animation.direction = LEFT;
			animation.setPosition(a.body.getHeadPosition().x + 20, a.body.getHeadPosition().y, a.body.getHeadPosition().z);
		} else {
			animation.direction = RIGHT;
			animation.setPosition(a.body.getHeadPosition().x - 20, a.body.getHeadPosition().y, a.body.getHeadPosition().z);
		}
			
		if (animation.currentIndex == animation.framesWide() - 1) {
			a.sweating = false;
	    	remove();
		}
	}
	@Override
    void draw(Graphics g) {
	    // bollocks
    }

}
