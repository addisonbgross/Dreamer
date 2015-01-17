package Dreamer;

public abstract class Effect extends Element implements Updateable {
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
	void followActor() {
		if (actor.isFacing("left")) {
			animation.direction = LEFT;
			
			animation.setPosition(actor.getX() + xOffset, actor.getY() + yOffset, actor.getZ() + zOffset);
		} else {
			animation.direction = RIGHT;
			animation.setPosition(actor.getX() - xOffset, actor.getY() + yOffset, actor.getZ() + zOffset);
		}
	}
}
