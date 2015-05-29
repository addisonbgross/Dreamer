package Dreamer;

import java.util.ArrayList;

public class Foreground extends Positionable {
	
	private static final long serialVersionUID = -8726863549786269562L;
	public static ArrayList<Element> foreground = new ArrayList<Element>();
	
	@Override
	public void add() {
		foreground.add(this);
	}
	@Override
	public void remove() {
		foreground.remove(this);
	}
}
