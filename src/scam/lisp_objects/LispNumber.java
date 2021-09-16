package scam.lisp_objects;

/**
 * LispNumber is an abstract class extending LispObject and implementing a
 * number. Various convenience methods are included that provide casting and
 * type checking functionality.
 */
public abstract class LispNumber implements LispObject {
	
    // Casting

	public abstract NumberInteger toInteger();

	public abstract NumberRational toRational();

	public abstract NumberReal toReal();
	
	// Type checking
	
	public boolean isInteger() {
		return (this instanceof NumberInteger);
	}
	
	public boolean isRational() {
		return (this instanceof NumberRational);
	}
	
	public boolean isReal() {
		return (this instanceof NumberReal);
	}
	
	public abstract String toString();
	

}