package Dreamer;

import java.util.ArrayList;
import java.util.Random;

public class Brains {
	static ArrayList<Trait> makeSoldier() {
		ArrayList<Trait> brain = new ArrayList<Trait>();
		Random r = new Random();
		
		brain.add(new Speed(r.nextFloat()));
		brain.add(new Follow());
		brain.add(new Duelist());
		brain.add(new Violent());
		brain.add(new Jumpy());
		
		return brain;
	}
}