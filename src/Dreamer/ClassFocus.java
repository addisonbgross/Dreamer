package Dreamer;

import java.util.ArrayList;
import Dreamer.interfaces.*;

class ClassFocus extends Positionable
implements Updateable, Drawable {
	
	private static final long serialVersionUID = 2627661786688015225L;
	ArrayList<Positionable> classElements = new ArrayList<Positionable>();
	float maxDistance = 0;
	int yOffset = 0;
	
	/* HOLY SHIT THIS IS CRAZY... TOO.... GENERIC... TO LIVE */
	
	<T> ClassFocus(Class<?>... c) {
		for(Class<?> cn: c)
			for (Object o : Manager.masterList)
				if ((o.getClass() == cn) && (Positionable.class.isAssignableFrom(o.getClass())))
					classElements.add((Positionable)o);
	}
	
	<T> ClassFocus(int y, Class<?>... c) {
		this(c);
		yOffset = y;
	}

	public boolean isVisible() {
		return Camera.isPointVisible(getX(), getY(), getZ()); 
	}
	
	public void draw() {
		
		if(Manager.debug) {
			String s = "ClassFocus@("+(int)getMinX()+", "+(int)getMinY()+")";
			Drawer.drawCursor(s, getX(), getY(), getZ());
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
		for (Positionable p : classElements) {
			try {
				if(minX == 0 && maxX == 0 && minY == 0 && maxY == 0) {
					minX = p.getX();
					maxX = p.getX();
					minY = p.getY();
					maxY = p.getY();
					minZ = p.getZ();
					maxZ = p.getZ();
				}
				else {
					minX = Math.min(minX, p.getX());
					maxX = Math.max(maxX, p.getX());
					minY = Math.min(minY, p.getY());
					maxY = Math.max(maxY, p.getY());
					minZ = Math.min(minZ, p.getZ());
					maxZ = Math.max(maxZ, p.getZ());
				}
				setPosition(p.getX() + getX(), p.getY() + getY(), p.getZ() + getZ());
				i++;
			} catch(NullPointerException ex) {
				//class is empty, focus is on origin
			}
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