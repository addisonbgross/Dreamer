package Dreamer;

public class SaferTreeSet<T> extends java.util.TreeSet<T> {

	private static final long serialVersionUID = -6477755002348965658L;
	
	SaferTreeSet(java.util.Comparator<T> c) { super(c); }
	
	SaferTreeSet() { super(); }
	
	@SuppressWarnings("unchecked")
	public boolean tryAdd(Object o) { 
		
		try {
		
			add((T)o);
			return true;
			
		} catch(Exception e) {
			
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean tryRemove(Object o) { 
		
		try {
		
			remove((T)o);
			return true;
			
		} catch(Exception e) {
			
			return false;
		}
	}
}
