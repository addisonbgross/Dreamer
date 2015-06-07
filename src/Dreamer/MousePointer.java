package Dreamer;

import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

import Dreamer.interfaces.*;

public class MousePointer extends Positionable 
implements Updateable, Drawable {
	
	private static final long serialVersionUID = 4399127807182868906L;
	
	boolean leftClickPressed = false;
	boolean rightClickPressed = false;
	float lastClickedX, lastClickedY, lastXVel, lastYVel;
	Performable onMove = ()-> {}, 
			onRightClick = ()-> {}, 
			onLeftClick = ()-> {}, 
			onRightClickRelease = ()-> {}, 
			onLeftClickRelease = ()-> {};
	Shape3d focus;
	Rectangle selectionRectangle = null;
	
	MousePointer() {
		
		try {
			Mouse.create();
			Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {

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
		
		if(Mouse.isButtonDown(0)) {
			
			if(!leftClickPressed) {
				lastClickedX = getX();
				lastClickedY = getY();
				leftClickPressed = true;
				onLeftClick.perform();
			}	
		} else if(leftClickPressed) {
			
			leftClickPressed = false;
			onLeftClickRelease.perform();
		}
		
		if(Mouse.isButtonDown(1)) {
			
			if(!rightClickPressed) {
				lastClickedX = getX();
				lastClickedY = getY();
				rightClickPressed = true;
				onRightClick.perform();
			}	
		} else if(rightClickPressed) {
			
			rightClickPressed = false;
			onRightClickRelease.perform();
		}
	}

	public boolean isVisible() { return true; }
	
	public void draw() {
		
		OpenGL.disableDepthTest();
	
		Drawer.drawCursor("MousePointer (" + getX() + " " + getY() + " " + getZ() + ")", getX(), getY(), getZ());
		
		if(selectionRectangle != null) {
			Drawer.drawShape(selectionRectangle, Color.white, false);
		}
	}

	public void startSelection() {
		selectionRectangle = new Rectangle(getX(), getY(), getX(), getY());
	}
	
	public void updateSelection() {
		
		if(selectionRectangle != null) {
			
			float 
				width = getX() - lastClickedX, 
				height = getY() - lastClickedY;
			
			selectionRectangle.setSize(width, height);
		}
	}
	
	public Set<Positionable> getSelection() {
		Set<Positionable> set = new java.util.HashSet<>();
		set.addAll(Collider.getActiveWithin(selectionRectangle));
		return set;
	}
	
	public void resetSelection() {
		selectionRectangle = null;
	}
}
