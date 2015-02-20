package Dreamer;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;

public class Theme {

	static enum Default {LIGHT, DARK, FONT}; 
	static Theme current = new Theme();
	
	private Map<Default, Color> colorMap = new HashMap<Default, Color>(10);
	
	static Theme mono = new Theme()
		.addColor(Default.LIGHT, 200, 200, 200)
		.addColor(Default.DARK, 25, 25, 25)
		.addColor(Default.FONT, 225, 225, 225)
		;
	
	static Theme ice = new Theme()
		.addColor(Default.LIGHT, 70, 180, 255)
		.addColor(Default.DARK, 30, 75, 150)
		;
	
	static Theme fire = new Theme()
		.addColor(Default.LIGHT, 255, 180, 40)
		.addColor(Default.DARK, 150, 75, 30)
		;
	
	static Theme transparentFire = new Theme()
		.addColor(Default.LIGHT, 255, 180, 40)
		.addColor(Default.DARK, 150, 75, 30)
		.setTransparency(0.5f)
		;
	
	Color getColor(Default d) {
		Color c;
		return (c = colorMap.get(d)) == null ? new Color(50, 50, 50): c;
	}
	
	private Theme addColor(Default d, int r, int g, int b) {
		colorMap.put(d, new Color(r, g, b));
		return this;
	}
	
	private Theme setTransparency(float f) { // 0f -1f
		for(Default d: colorMap.keySet())
			colorMap.get(d).a = f;
		return this;
	}
}
