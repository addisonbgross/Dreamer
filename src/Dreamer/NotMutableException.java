package Dreamer;

public class NotMutableException extends RuntimeException {
	private static final long serialVersionUID = 3179647164739181839L;
	public NotMutableException() {
		super("Element is not mutable: remove(), modify, and then add() it");
	}
}