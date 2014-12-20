package Dreamer;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;

public class Theme {

	public static Theme current = new Theme();
	
	Map<String, Color> colorMap = new HashMap<String, Color>(10);
	
	Color getColor(String s) {
		Color c;
		return ((c = colorMap.get(s.toLowerCase())) == null) ? new Color(50, 50, 50): c;
	}
	
	void addColor(String s, int r, int g, int b) {
		colorMap.put(s, new Color(r, g, b));
	}

	public void setTransparency(int i) { // 0 - 256
		setTransparency((float) i / 256);
	}
	
	public void setTransparency(float f) { // 0f -1f
		for(String s: colorMap.keySet())
			colorMap.get(s).a = f;
	}
}
