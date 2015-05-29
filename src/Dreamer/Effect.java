package Dreamer;

import Dreamer.interfaces.Updateable;

public abstract class Effect extends Element implements Updateable {
	
	private static final long serialVersionUID = 5289812447446272287L;
	Actor actor;
	Animation2 animation;
	int xOffset, yOffset, zOffset;
	int LEFT = 1, RIGHT = -1;

	public void add() {
		super.add();
		animation.add();
	}
	public void remove() {
		super.remove();
		animation.remove();
	}
	public void rePosition() {
		if (actor.isFacing("left")) {
			animation.direction = LEFT;
			animation.setPosition(actor.getX() + xOffset, actor.getY() + yOffset, actor.getZ() + zOffset);
		} else {
			animation.direction = RIGHT;
			animation.setPosition(actor.getX() - xOffset, actor.getY() + yOffset, actor.getZ() + zOffset);
		}
	}
	@Override
	void draw() {
		if(Manager.debug) {
			Drawer.drawCursor(toString(), animation.position.x, animation.position.y, 0); 
		}
	}
	abstract void followActor(); // leave function blank if Effect doesn't follow
}
