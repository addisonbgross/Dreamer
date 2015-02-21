package Dreamer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class Element implements Serializable {
	private static final long serialVersionUID = 1384182428126670525L;
	// the masterList contains all Elements that have had their .add() method
	// called
	// they are removed from this list by calling their .remove() method
	protected static HashSet<Element> masterList = new HashSet<Element>(2000);
	protected static HashSet<Element> activeSet = new HashSet<Element>(2000);

	// each Collidable is placed in these maps according to it's x and y
	// position
	// from now on, only Collidables need to be on this
	// this list must have the current positions of the Collidable updated
	// manually,
	// which entails .remove()ing it before modifying it's position and calling
	// .add() to place it back on this list, as it's critical for efficient
	// collisions
	protected static ElementMap<Float, HashSet<Element>> xRange = new ElementMap<Float, HashSet<Element>>();
	protected static ElementMap<Float, HashSet<Element>> yRange = new ElementMap<Float, HashSet<Element>>();
	// for cleanup of empty containers (cleaning them up on-the fly introduces
	// concurrent errors)
	private static HashSet<Float> deathSet = new HashSet<Float>();
	// the set which all Elements implementing Updateable get added to with
	// .add()
	static Set<Updateable> updateSet = new HashSet<Updateable>(100);
	// to avoid concurrency errors and such
	static Set<Updateable> updateLaterSet = new HashSet<Updateable>(100);
	static Set<Updateable> updateBirthSet = new HashSet<Updateable>();
	static Set<Updateable> updateDeathSet = new HashSet<Updateable>();
	private static Set<Element> xSet = new HashSet<Element>();
	private static Set<Element> ySet = new HashSet<Element>();
	// backgrounds are drawn before other elements and still use a variety of
	// methods to render
	public static ArrayList<Element> background = new ArrayList<Element>();
	public static ArrayList<Element> foreground = new ArrayList<Element>();

	// set to false to turn off info
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
			updateBirthSet.add((Updateable) this);
		masterList.add(this);
	}

	void remove() {
		if (this instanceof Updateable)
			updateDeathSet.add((Updateable) this);
		masterList.remove(this);
	}

	boolean isVisible() {
		// TODO make this effective at filtering out unneeded Elements
		return true;
	}

	// only method subclasses must implement, even if just for debugging
	void draw(Graphics g) {}

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

	static void drawActive(Graphics g) {
		
		for (Element e : background) {
			Light.light(e);
			e.draw(g);
		}
		
		for (Element o : activeSet) {
			Light.light(o);
			o.draw(g);
		}
		
		Face.drawFaces();
		
		for (Element e : foreground)
			e.draw(g);
	}

	static int numberActive() {return activeSet.size();}
	static int numberXRangeSets() {return xRange.size();}
	static int numberYRangeSets() {return yRange.size();}
	static int numberTotal() {return masterList.size();}

	static void clearAll() {
		updateSet.clear();
		updateBirthSet.clear();
		updateDeathSet.clear();
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

	/**
	 * goes through a set of all Elements implementing Updateable interface also
	 * cleans up the active lists of Elements
	 */
	public static void updateAll() {

		updateSet.addAll(updateBirthSet);
		updateBirthSet.clear();

		Updateable z = null;
		try {
			for (Updateable e : updateSet) {
				if (Actor.class.isAssignableFrom(e.getClass())
						|| Sweat.class.isAssignableFrom(e.getClass()))
					e.update();
				else
					updateLaterSet.add(e);
				z = e;
			}
		} catch (java.util.ConcurrentModificationException e) {
			// TODO fix this exception
			// this is caused by Dreamer.Ninja adding and removing something,
			// probably a call to .remove() or .add() in update
			if (z != null)
				System.out.println(z.getClass().toString());
			e.printStackTrace();
		}
		for (Updateable e : updateLaterSet) {
			e.update();
		}
		updateSet.removeAll(updateDeathSet);
		updateDeathSet.clear();

		updateLaterSet.clear();

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

/**
 * ElementMap: allows a key to refer to a set of Element objects and
 * transparently allows addition and removal operations
 * 
 * @author Maxim
 *
 * @param <K>
 *            can be any key
 * @param <V>
 *            must be a HashSet<Element>
 */
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
			return super.get(key).remove(value);
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
