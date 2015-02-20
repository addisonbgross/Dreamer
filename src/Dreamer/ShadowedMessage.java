package Dreamer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.TextureImpl;

class ShadowedMessage extends Foreground {	

	String name;
	Color textColor = Color.black, shadowColor = Color.gray, highlightColor = Color.red;
	boolean highlight =  false;
	
	ShadowedMessage(String s, float x, float y) 
	{
		setPosition(x, y, 0);
		name = s;	
		shadowColor = Theme.current.getColor(Theme.Default.DARK);
		textColor = Theme.current.getColor(Theme.Default.FONT);
	}

	@Override
	void draw(Graphics g) 
	{
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		g.setColor(shadowColor);
		g.setFont(Library.messageFont);
		TextureImpl.bindNone();
		g.drawString(
				name, 
				Camera.translate(getMinX() - Library.messageFont.getWidth(name) / 2 + Constants.SHADOWOFFSET / 2, 0, 0).x, 
				Camera.translate(0, getMinY() + Library.messageFont.getHeight(name) / 2 - Constants.SHADOWOFFSET / 2, 0).y
		);
		g.setColor(highlight? highlightColor : textColor);		
		g.drawString(
				name, 
				Camera.translate(getMinX() - Library.messageFont.getWidth(name) / 2 - Constants.SHADOWOFFSET / 2, 0, 0).x, 
				Camera.translate(0, getMinY() + Library.messageFont.getHeight(name) / 2 + Constants.SHADOWOFFSET / 2, 0).y
		);
	}
}