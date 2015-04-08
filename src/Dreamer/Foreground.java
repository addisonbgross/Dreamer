package Dreamer;

import java.util.ArrayList;

public class Foreground extends Positionable {
	private static final long serialVersionUID = -8726863549786269562L;
	public static ArrayList<Element> foreground = new ArrayList<Element>();
	
	@Override
	void add() {
		Foreground.foreground.add(this);
	}
	@Override
	void remove() {
		Foreground.foreground.remove(this);
		int x = 0;
	}
}
