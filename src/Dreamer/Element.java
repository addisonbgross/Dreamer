package Dreamer;

import java.util.TreeMap;
import java.util.HashSet;
import java.io.Serializable;

public class Element implements Serializable {

	private static final long serialVersionUID = 1384182428126670525L;

	protected Element() {}

	/**
	 * WARNING: if you update add() remove() MUST be changed as well leaving
	 * references to objects on this list will cause massive memory leaks
	 */
	void add() {
		Manager.add(this);
	}

	void remove() {
		Manager.remove(this);
	}

	boolean isVisible() {
		// TODO make this effective at filtering out unneeded Elements
		return true;
	}

	void draw() {
		System.err.println("No draw method attached to " + this.getClass());
	}

	void print() {
		System.out.println(this.toString());
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
			if (super.get(key).remove(value)) {
				if (super.get(key).size() == 0) {
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
