package Dreamer;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

class ClassFocus extends Element implements Updateable {
	ArrayList<Element> classElements = new ArrayList<Element>();
	float maxDistance = 0;
	int yOffset = 0;
	
	<T> ClassFocus(Class<?>... c) {
		for(Class<?> cn: c)
			for (Element e : masterList)
				if (e.getClass() == cn)
					classElements.add(e);
	}
	<T> ClassFocus(int y, Class<?>... c) {
		this(c);
		yOffset = y;
	}

	@Override
	void draw(Graphics g) {
		if(Element.debug) {
			String s = "ClassFocus@("+(int)getMinX()+", "+(int)getMinY()+")";
			drawCursor(s, getX(), getY(), getZ(), g);
		}
	}
	public void update() {
		remove();
		setPosition(0, 0, 0);
		maxDistance = 0;
		double minX = 0;
		double maxX = 0;
		double minY = 0;
		double maxY = 0;
		double minZ = 0;
		double maxZ = 0;
		int i = 0;
		for (Element e : classElements)
			try {
				if(minX == 0 && maxX == 0 && minY == 0 && maxY == 0) {
					minX = e.getX();
					maxX = e.getX();
					minY = e.getY();
					maxY = e.getY();
					minZ = e.getZ();
					maxZ = e.getZ();
				}
				else {
					minX = Math.min(minX, e.getX());
					maxX = Math.max(maxX, e.getX());
					minY = Math.min(minY, e.getY());
					maxY = Math.max(maxY, e.getY());
					minZ = Math.min(minZ, e.getZ());
					maxZ = Math.max(maxZ, e.getZ());
				}
				setPosition(e.getX() + getX(), e.getY() + getY(), e.getZ() + getZ());
				i++;
			} catch(NullPointerException ex) {
			//class is empty, focus is on origin
		}
		if(i != 0 ) {
			setPosition(getX() / i, (getY() / i) + yOffset, getZ() / i);
			maxDistance = (float)Math.sqrt((Math.pow(maxX - minX, 2) + Math.pow(maxY - minY, 2)));
		}
		add();
	}
	public String toString() {
		String s = classElements+" focus@("+(int)getMinX()+", "+(int)getMinY()+")";
		return s;
	}
}