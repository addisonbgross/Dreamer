package Dreamer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import Dreamer.interfaces.Updateable;

public class Updater {
	// the set which all Elements implementing Updateable get added to with
	// .add()
	static Set<Updateable> updateSet = new HashSet<>(100);
	// to avoid concurrency errors and such
	static Set<Updateable> updateLaterSet = new HashSet<>(100);
	
	public static void add(Element e) {
		updateSet.add((Updateable) e);
	}
	
	public static void remove(Element e) {
		updateSet.add((Updateable) e);
	}

	public static void clear() {
		updateSet.clear();
	}

	public static void updateAll() {		
		
		Set<Updateable> updatingThings = new HashSet<>();
		updatingThings.addAll(updateSet);

		Updateable z = null;
		
		try {
			
			for (Updateable e : updatingThings) {
				
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
		
		for (Updateable e : updateLaterSet) { e.update(); }

		updateLaterSet.clear();
	}
}

class UpdateComparator implements Comparator<Updateable> {
	
	boolean a, b;
	
	@Override
	public int compare(Updateable arg0, Updateable arg1) {
		a = b = false;
		if(arg0.getClass().equals(Actor.class))
				a = true;
		if(arg1.getClass().equals(Actor.class))
				b = true;
		return 1;
	}
}
