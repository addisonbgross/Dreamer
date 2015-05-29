package Dreamer.interfaces;

public interface Manageable extends java.io.Serializable {
	
	java.util.Collection<Manageable> getChildren();
	
	default void add() { 
		
		Dreamer.Manager.add(this);
		
		try {
			getChildren().stream().forEach( (x)-> x.add() );
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	default void remove() { 
	
		Dreamer.Manager.remove(this);
		
		try {
			getChildren().stream().forEach( (x)-> x.remove() );
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
