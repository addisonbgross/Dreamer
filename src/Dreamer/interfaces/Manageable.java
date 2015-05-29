package Dreamer.interfaces;

public interface Manageable extends java.io.Serializable {
	
	default void add() { Dreamer.Manager.add(this); }

	default void remove() { Dreamer.Manager.remove(this); }
}
