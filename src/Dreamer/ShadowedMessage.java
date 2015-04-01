package Dreamer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

class ShadowedMessage extends Foreground {	

	private static final long serialVersionUID = -7660105807513879250L;
	String name;
	Color textColor = Color.black, shadowColor = Color.gray, highlightColor = Color.red;
	boolean highlight =  false;
	Justification justification = Justification.CENTER;
	
	ShadowedMessage(String s, float x, float y) 
	{
		setPosition(x, y, 0);
		name = s;	
		shadowColor = Theme.current.getColor(Theme.Default.DARK);
		textColor = Theme.current.getColor(Theme.Default.FONT);
	}

	@Override
	void draw() 
	{
		Camera.pushPosition();
		Camera.focus(0, 0, 2000);
		float j = (justification == Justification.LEFT)? 0: (justification == Justification.RIGHT)? 1: 0.5f; 
		// OpenGL.disableDepthTest();
		Drawer.setColor(shadowColor);
		Drawer.setFont(Library.messageFont);
		TextureImpl.bindNone();
		Drawer.drawString(
				name, 
				Camera.translate(getMinX() - j * Library.messageFont.getWidth(name) + Constants.SHADOWOFFSET / 2, 0, 0).x, 
				Camera.translate(0, getMinY() + 0.5f * Library.messageFont.getHeight(name) - Constants.SHADOWOFFSET / 2, 0).y
		);
		Drawer.setColor(highlight? highlightColor : textColor);		
		Drawer.drawString(
				name, 
				Camera.translate(getMinX() - j * Library.messageFont.getWidth(name) - Constants.SHADOWOFFSET / 2, 0, 0).x, 
				Camera.translate(0, getMinY() + 0.5f * Library.messageFont.getHeight(name) + Constants.SHADOWOFFSET / 2, 0).y
		);
		Camera.popPosition();
	}
}