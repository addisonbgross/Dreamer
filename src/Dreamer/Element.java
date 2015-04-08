package Dreamer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.TreeMap;

public class Element implements Serializable {
	
	private static final long serialVersionUID = 1384182428126670525L;
	
	protected static HashSet<Element> masterList = new HashSet<Element>(2000);
	protected static HashSet<Element> activeSet = new HashSet<Element>(2000);

	public static boolean debug = false;
	
	protected Element() {}

	/**
	 * add: maps multiple keys to each element for fast retrieval
	 * 
	 * add this element's minimum, intermediate, and extreme x and y values as
	 * references to it, as well as adding a reference by id to the
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

	static void activateVisible() {
		for (Element e : masterList)
			if (e.isVisible())
				activeSet.add(e);
	}

	static void printActive() {
		System.out.println("ACTIVE ELEMENTS");
		for (Element e : activeSet) {
			e.print();
		}
	}

	static void drawActive() {
		
		for (Element e : Background.background) {
			Light.light(e);
			e.draw();
		}
		
		for (Element o : activeSet) {
			Light.light(o);
			o.draw();
		}
		
		Face.drawFaces();
		
		for (Element e : Foreground.foreground)
			e.draw();
	}

	static void clearAll() {
		
		masterList.clear();
		activeSet.clear();
		Updater.clear();
		Collidable.clear();
		Background.background.clear();
		Foreground.foreground.clear();
		Light.clearAll();
	}

	static void clearActive() {
		
		activeSet.clear();
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
