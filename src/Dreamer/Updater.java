package Dreamer;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Collection;
import java.util.LinkedHashSet;

import Dreamer.interfaces.Updateable;

/*

<T> ClassFocus(Class<?>... c) {
		for(Class<?> cn: c)
			for (Object o : Manager.masterList)
				if ((o.getClass() == cn) && (Positionable.class.isAssignableFrom(o.getClass())))
					classElements.add((Positionable)o);
	}

 */

class DiscriminatorSet<T> {

	private TreeSet<T> container;
	private Class<T> selected;
	
	DiscriminatorSet(Class<T> c) { selected = c; }
	
	void tryAdd(Object o) { if(selected.isInstance(o)) { container.add((T)o); } }
	
	void tryRemove(Object o) { if(selected.isInstance(o)) { container.remove(o); } }
	
	TreeSet<T> getContainer() {
		return container;
	}
}

public class Updater {
	
	static Collection<Updateable> updateSet = new TreeSet<>(new UpdateComparator());
	static Collection<Updateable> updatingThings = new LinkedHashSet<>();
	
	public static void tryAdd(Object o) {
		
		if(o instanceof Updateable)
			updateSet.add((Updateable) o);
	}
	
	public static void tryRemove(Object o) {
		
		if(o instanceof Updateable)
			updateSet.remove((Updateable) o);
	}

	public static void clear() { updateSet.clear(); }

	public static void updateAll() {		
		
		updatingThings.clear();
		updatingThings.addAll(updateSet);
		updatingThings.stream().forEach( (x)-> x.update() );	
	}
}

class UpdateComparator implements Comparator<Updateable> {
	
	@Override
	public int compare(Updateable arg0, Updateable arg1) {
	
		return (arg0.isPriority() && !arg1.isPriority())? 1 : -1;
	}
}
