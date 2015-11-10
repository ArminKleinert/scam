package Exceptions;

public class LispError extends Exception {
	public LispError(String message) {
		super(message);
	}
	LispError() {}
}
