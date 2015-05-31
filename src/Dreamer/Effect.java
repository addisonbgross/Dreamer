package Dreamer;

import Dreamer.interfaces.*;

public abstract class Effect
implements Updateable, Manageable, Drawable {
	
	private static final long serialVersionUID = 5289812447446272287L;
	Actor actor;
	Animation2 animation;
	int xOffset, yOffset, zOffset;
	int LEFT = 1, RIGHT = -1;

	public java.util.Collection<Manageable> getChildren() {
		java.util.Collection<Manageable> children = new java.util.ArrayList<>();
		children.add(animation);
		return children;
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
	
	public void draw() {
		
		if(Manager.debug) {
			Drawer.drawCursor(toString(), animation.position.x, animation.position.y, 0); 
		}
	}
	
	public boolean isVisible() { return true; }
	
	abstract void followActor(); // leave function blank if Effect doesn't follow
}
