package Dreamer;

import interfaces.Drawable;

import java.util.ArrayList;

public abstract class Foreground extends Positionable 
implements Drawable {
	
	private static final long serialVersionUID = -8726863549786269562L;
	public static ArrayList<Drawable> foreground = new ArrayList<Drawable>();
	
	@Override
	public void add() { foreground.add(this); }
	
	@Override
	public void remove() { foreground.remove(this); }
}
