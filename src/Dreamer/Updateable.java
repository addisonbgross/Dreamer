package Dreamer;

import java.util.Comparator;

public interface Updateable {
	default public void update() {
		((Shape3d)this).mutable = false;
	};
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