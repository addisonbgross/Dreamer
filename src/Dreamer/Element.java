package Dreamer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;

public class Element implements Serializable {
	
	private static final long serialVersionUID = 1384182428126670525L;
	
	protected static HashSet<Element> masterList = new HashSet<Element>(2000);
	protected static HashSet<Element> activeSet = new HashSet<Element>(2000);

	protected static ElementMap<Float, HashSet<Element>> xRange = new ElementMap<Float, HashSet<Element>>();
	protected static ElementMap<Float, HashSet<Element>> yRange = new ElementMap<Float, HashSet<Element>>();
	
	private static HashSet<Float> deathSet = new HashSet<Float>();

	private static Set<Element> xSet = new HashSet<Element>();
	private static Set<Element> ySet = new HashSet<Element>();
	
	public static ArrayList<Element> background = new ArrayList<Element>();
	public static ArrayList<Element> foreground = new ArrayList<Element>();

	public static boolean debug = false;
	
	protected Element() {}

	/**
	 * add: maps multiple keys to each element for fast retrieval
	 * 
	 * add this element's minimum, intermediate, and extreme x and y values as
	 * references to it, as well as adding a reference by id to the
	 * 
	 * if an object is farther away(z < 1) it will be added to the background
	 * list objects on this list will always be active but do NOT get updated
	 * like everything else during normal game flow
	 * 
	 * WARNING: if you update add() remove() MUST be changed as well leaving
	 * references to objects on this list will cause massive memory leaks
	 */
	void add() {
		if (this instanceof Updateable)
			Updater.add(this);
		masterList.add(this);
	}

	void remove() {
		if (this instanceof Updateable)
			Updater.add(this);
		masterList.remove(this);
	}

	boolean isVisible() {
		// TODO make this effective at filtering out unneeded Elements
		return true;
	}

	// only method subclasses must implement, even if just for debugging
	void draw() {
		System.err.println("No draw method attached to " + this.getClass());
	}

	void print() {
		System.out.println(this.toString());
	}

	public boolean contains(String... strings) {
		for (String s : strings)
			if (!getClass().toString().toLowerCase().contains(s.toLowerCase()))
				return false;
		return true;
	}

	/*
	 * this function adds Elements to be drawn to the activeSeteach element
	 * checks whether it isVisible() to determine if it is drawn MODIFIES:
	 * static activeSet
	 */
	static void activateVisible() {
		for (Element e : masterList)
			if (e.isVisible())
				activeSet.add(e);
	}

	/**
	 * getActiveWithin: returns the active set bound within a rectangle of Shape
	 * width and height
	 * 
	 * @param <T>
	 * 
	 * @param s
	 *            Shape x and y extremes
	 * @return Set of elements within a given bounds
	 */
	static Set<Element> getActiveWithin(Shape s) {
		
		Set<Map.Entry<Float, HashSet<Element>>> temp = new HashSet<Map.Entry<Float, HashSet<Element>>>();
		Set<Element> tempActive = new HashSet<Element>();

		// take all set of elements in the camera scene x range
		temp.addAll(xRange.subMap(s.getMinX(), true, s.getMaxX(), true)
				.entrySet());

		// add each set together in xSet
		for (Map.Entry<Float, HashSet<Element>> entry : temp) {
			xSet.addAll(entry.getValue());
		}

		temp.clear();

		// take all the elements in scene y range
		temp.addAll(yRange.subMap(s.getMinY(), true, s.getMaxY(), true)
				.entrySet());

		// add them together in ySet
		for (Map.Entry<Float, HashSet<Element>> entry : temp) {
			ySet.addAll(entry.getValue());
		}

		// if an element is in both x and y sets then draw it, make it active,
		// this has the effect of rendering and activating only the elements
		// that are within the camera scene boundaries
		for (Element o : xSet) {
			if (ySet.contains(o)) {
				tempActive.add(o);
			}
		}
		return tempActive;
	}

	static void printActive() {
		System.out.println("ACTIVE ELEMENTS");
		for (Element e : activeSet) {
			e.print();
		}
	}

	static void drawActive() {
		
		for (Element e : background) {
			Light.light(e);
			e.draw();
		}
		
		for (Element o : activeSet) {
			Light.light(o);
			o.draw();
		}
		
		Face.drawFaces();
		
		for (Element e : foreground)
			e.draw();
	}

	static int numberActive() {return activeSet.size();}
	static int numberXRangeSets() {return xRange.size();}
	static int numberYRangeSets() {return yRange.size();}
	static int numberTotal() {return masterList.size();}

	static void clearAll() {
		
		Updater.clear();
		masterList.clear();
		xRange.clear();
		yRange.clear();
		activeSet.clear();
		xSet.clear();
		ySet.clear();
		background.clear();
		foreground.clear();
		Light.clearAll();
	}

	static void clearActive() {
		
		activeSet.clear();
		xSet.clear();
		ySet.clear();
	}

	static void cleanup() {
		
		deathSet.clear();

		for (Map.Entry<Float, HashSet<Element>> entry : xRange.entrySet()) {
			if (entry.getValue().isEmpty())
				deathSet.add(entry.getKey());
		}
		for (Float f : deathSet)
			xRange.remove(f);
		deathSet.clear();
		for (Map.Entry<Float, HashSet<Element>> entry : yRange.entrySet()) {
			if (entry.getValue().isEmpty())
				deathSet.add(entry.getKey());
		}
		for (Float f : deathSet)
			yRange.remove(f);
	}
}

class ElementMap<K, V> extends TreeMap<K, HashSet<Element>> {
	private static final long serialVersionUID = 186057469873355492L;

	boolean add(K key, Element value) {
		if (super.containsKey(key)) {
			super.get(key).add(value);
		} else {
			HashSet<Element> a = new HashSet<Element>();
			a.add(value);
			super.put(key, a);
		}
		return true;
	}

	boolean remove(K key, Element value) {
		try {
			if(super.get(key).remove(value)) {
				if(super.get(key).size() == 0) {
					super.remove(key);
				}	
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
/*
class zComparator implements Comparator<Element> {
	@Override
	public int compare(Element a, Element b) {
		return a.getZ() > b.getZ() ? 1 : (a.getZ() == b.getZ()) ? 0 : -1;
	}
}
*/
