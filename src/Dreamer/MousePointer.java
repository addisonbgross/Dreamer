package Dreamer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class MousePointer extends Element implements Updateable {
	boolean leftClickAction = false, rightClickAction = false;
	float lastX, lastY;
	
	MousePointer() {
		try {
			Mouse.create();
			Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		remove();
		setPosition(Camera.translateMouse(Mouse.getX(), Mouse.getY()));
		//draw a block defined by the mouse left click dragging region
		if(Mouse.isButtonDown(0)) {
			if(!leftClickAction) {
				lastX = getX();
				lastY = getY();
				leftClickAction = true;
			}	
		} else if(leftClickAction) {
			leftClickAction = false;
			new Block3d(
					Color.gray,
					getX(), 
					getY(),
					getZ(),
					2 * Math.abs(getX() - lastX), 
					2 * Math.abs(getY() - lastY),
					100
					).add();
		}
		if(Mouse.isButtonDown(1)) {
			if(!rightClickAction) {
				lastX = getX();
				lastY = getY();
				rightClickAction = true;
			}	
		} else if(rightClickAction) {
			rightClickAction = false;
		}
		add();
	}
	@Override
	void draw(Graphics g) {
		if(Element.debug)
			drawCursor("MouseLight", getX(), getY(), getZ(), g);
	}
}
