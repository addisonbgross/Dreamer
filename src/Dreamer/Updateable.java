package Dreamer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public interface Updateable {
	default public void update() {
		((Shape3d)this).mutable = false;
	};
}

class Updater {
	// the set which all Elements implementing Updateable get added to with
	// .add()
	static Set<Updateable> updateSet = new HashSet<Updateable>(100);
	// to avoid concurrency errors and such
	static Set<Updateable> updateLaterSet = new HashSet<Updateable>(100);
	static Set<Updateable> updateBirthSet = new HashSet<Updateable>();
	static Set<Updateable> updateDeathSet = new HashSet<Updateable>();
	
	public static void add(Element e) {
		updateBirthSet.add((Updateable) e);
	}

	public static void clear() {
		updateSet.clear();
		updateBirthSet.clear();
		updateDeathSet.clear();
	}

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