package Dreamer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;

public class MousePointer extends Positionable implements Updateable {
	
	private static final long serialVersionUID = 4399127807182868906L;
	
	boolean leftClickAction = false, rightClickAction = false;
	float lastX, lastY, lastXVel, lastYVel;
	Performable onMove = ()-> {}, 
			onRightClick = ()-> {}, 
			onLeftClick = ()-> {}, 
			onRightClickRelease = ()-> {}, 
			onLeftClickRelease = ()-> {};
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
		lastYVel = Mouse.getDY();

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
	void draw() {
		Drawer.drawCursor("MousePointer (" + getX() + " " + getY() + " " + getZ() + ")", getX(), getY(), getZ());
		/*
		if(Element.debug)
			drawCursor("MousePointer", getX(), getY(), getZ(), g);
			*/
	}
}
