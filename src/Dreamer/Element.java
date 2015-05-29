package Dreamer;

import java.util.TreeMap;
import java.util.HashSet;
import Dreamer.interfaces.Manageable;

public class Element implements Manageable {

	private static final long serialVersionUID = 1384182428126670525L;

	protected Element() {}

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
