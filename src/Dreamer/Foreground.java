package Dreamer;

public class Foreground extends Element {
	private static final long serialVersionUID = -8726863549786269562L;
	
	@Override
	void add() {
		foreground.add(this);
	}
	@Override
	void remove() {
		foreground.remove(this);
	}
}
