package Dreamer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.TextureImpl;

class ShadowedMessage extends Element {	

	String name;
	Color color = Color.black;
	
	ShadowedMessage(String s, float x, float y) 
	{
		setPosition(x, y, 0);
		name = s;	
	}
	ShadowedMessage(String s, float x, float y, Color c) 
	{
		this(s, x, y);
		color = c;
	}

	@Override
	void draw(Graphics g) 
	{
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		g.setColor(Library.messageFontShadow);
		g.setFont(Library.messageFont);
		TextureImpl.bindNone();
		g.drawString(
				name, 
				Camera.translate(getMinX() - Library.messageFont.getWidth(name) / 2 + Constants.SHADOWOFFSET / 2, 0, 0).x, 
				Camera.translate(0, getMinY() + Library.messageFont.getHeight(name) / 2 - Constants.SHADOWOFFSET / 2, 0).y
		);
		g.setColor(Library.messageFontColor);		
		g.drawString(
				name, 
				Camera.translate(getMinX() - Library.messageFont.getWidth(name) / 2 - Constants.SHADOWOFFSET / 2, 0, 0).x, 
				Camera.translate(0, getMinY() + Library.messageFont.getHeight(name) / 2 + Constants.SHADOWOFFSET / 2, 0).y
		);
	}
}