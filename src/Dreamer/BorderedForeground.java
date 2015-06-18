package Dreamer;

import interfaces.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class BorderedForeground extends Foreground
implements Drawable {
	
	private static final long serialVersionUID = 5512338269048527570L;
	Shape outline, outlineShadow;
	final float borderMargin = 4, borderWidth = 10;
	
	BorderedForeground() {
		
		outline = new Rectangle(
				borderMargin + borderWidth, 
				borderMargin + borderWidth, 
				Constants.screenWidth - 2 * (borderMargin + borderWidth) - Constants.SHADOWOFFSET, 
				Constants.screenHeight - 2 * (borderMargin + borderWidth) - Constants.SHADOWOFFSET
				);
		
		outlineShadow = new Rectangle(
				borderMargin + borderWidth + Constants.SHADOWOFFSET, 
				borderMargin + borderWidth + Constants.SHADOWOFFSET, 
				Constants.screenWidth - 2 * (borderMargin + borderWidth) - Constants.SHADOWOFFSET, 
				Constants.screenHeight - 2 * (borderMargin + borderWidth) - Constants.SHADOWOFFSET
				);
	}

	public void draw() {
		
		Drawer.graphics.setBackground(new Color(200, 200, 200));
		Drawer.graphics.setLineWidth(borderWidth);
		Drawer.graphics.setColor(Color.gray);
		Drawer.graphics.draw(outlineShadow);
		Drawer.graphics.setColor(Color.black);
		Drawer.graphics.draw(outline);
		Drawer.graphics.setLineWidth(1);
	}

	public boolean isVisible() { return true; }		
}
