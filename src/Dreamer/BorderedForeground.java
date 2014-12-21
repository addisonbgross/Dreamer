package Dreamer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class BorderedForeground extends Foreground{
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

	void draw(Graphics g) {
		g.setBackground(new Color(200, 200, 200));
		g.setLineWidth(borderWidth);
		g.setColor(Color.gray);
		g.draw(outlineShadow);
		g.setColor(Color.black);
		g.draw(outline);
		g.setLineWidth(1);
	}	
}
