package Dreamer;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Collection;
import java.util.LinkedHashSet;

import Dreamer.interfaces.Updateable;

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
	
	public static boolean isPriority(Object o) {
	
		return (Actor.class.isAssignableFrom(o.getClass())
				|| Sweat.class.isAssignableFrom(o.getClass()));
	}

	public static void updateAll() {		
		
		updatingThings.clear();
		updatingThings.addAll(updateSet);
		updatingThings.stream().forEach( (x)-> x.update() );	
	}
}

class UpdateComparator implements Comparator<Updateable> {
	
	boolean a, b;
	
	@Override
	public int compare(Updateable arg0, Updateable arg1) {
		
		a = Updater.isPriority(arg0);
		b = Updater.isPriority(arg1);
		
		if(a && !b) return 1;
		
		return -1;
	}
}
