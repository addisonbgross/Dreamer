package Dreamer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class MousePointer extends Positionable implements Updateable {
	boolean leftClickAction = false, rightClickAction = false;
	float lastX, lastY, lastXVel, lastYVel;
	Action onMove = new Action(),
			onRightClick = new Action(),
			onLeftClick = new Action(),
			onRightClickRelease = new Action(),
			onLeftClickRelease = new Action()
			;
	Shape3d focus;
	
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
		setPosition(Camera.translateMouse(Mouse.getX(), Mouse.getY(), getZ()));
		lastXVel = Mouse.getDX();
		System.out.println("Mouse: " + Mouse.getX() + ", " + Mouse.getY());
		if(Mouse.getX() > Constants.screenWidth - 5)
			Camera.command("right");
		else if(Mouse.getX() < 5)
			Camera.command("left");
		else if(Mouse.getY() > Constants.screenHeight - 5)
			Camera.command("up");
		else if(Mouse.getY() < 5)
			Camera.command("down");
		else 
			Camera.command("stop");
		
		onMove.perform();
		
		//draw a block defined by the mouse left click dragging region
		if(Mouse.isButtonDown(0)) {
			
			if(!leftClickAction) {
				lastX = getX();
				lastY = getY();
				leftClickAction = true;
				onLeftClick.perform();
			}	
		} else if(leftClickAction) {
			
			leftClickAction = false;
			onLeftClickRelease.perform();
		}
		
		if(Mouse.isButtonDown(1)) {
			if(!rightClickAction) {
				lastX = getX();
				lastY = getY();
				rightClickAction = true;
				onRightClick.perform();
			}	
		} else if(rightClickAction) {
			rightClickAction = false;
			onRightClickRelease.perform();
		}
		
		add();
	}

	@Override
	void draw(Graphics g) {
		Drawer.drawCursor("MousePointer (" + getX() + " " + getY() + " " + getZ() + ")", getX(), getY(), getZ(), g);
		/*
		if(Element.debug)
			drawCursor("MousePointer", getX(), getY(), getZ(), g);
			*/
	}
}
