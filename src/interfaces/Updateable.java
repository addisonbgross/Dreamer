package interfaces;

public interface Updateable { 
	
	public void update(); 

	default public boolean isPriority() {
		
		return (Dreamer.Actor.class.isAssignableFrom(getClass())
				|| Dreamer.Sweat.class.isAssignableFrom(getClass()));
	}
}