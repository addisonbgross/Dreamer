package Dreamer;

import interfaces.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import enums.*;

class Text extends Foreground 
implements Drawable {	

	private static final long serialVersionUID = -7660105807513879250L;
	String name;
	Color textColor = Color.white, shadowColor = Color.gray, highlightColor = Color.red;
	boolean highlight =  false, shadowed = false;
	Justification justification = Justification.CENTER;
	FontType font = FontType.EIGHT_BIT;
	
	Text(String s, float x, float y) {
		
		setPosition(x, y, 0);
		name = s;
	}
	
	public boolean isVisible() { return true; }

	public void drawString() {
		
		int width = Library.getFont(font).getWidth(name);
		int height = Library.getFont(font).getHeight(name);
		
		float j = (justification == Justification.LEFT)? 0 : (justification == Justification.RIGHT)? 1 : 0.5f; 
		
		Drawer.drawString(
				name, 
				Camera.translate(getMinX() - j * width + Constants.SHADOWOFFSET / 2, 0, 0).x, 
				Camera.translate(0, getMinY() + 0.5f * height - Constants.SHADOWOFFSET / 2, 0).y
		);
	}
	
	public void draw() {
		
		Camera.pushPosition();
		
		Camera.reset();

		Drawer.setFont(Library.getFont(font));
		TextureImpl.bindNone();
		
		// OpenGL.disableDepthTest();
		
		if(shadowed) {
			Drawer.setColor(shadowColor);
			drawString();
		}
		
		Drawer.setColor(highlight? highlightColor : textColor);		
		drawString();
		
		Camera.popPosition();
	}
}