package scam.lisp_objects;

/**
 * An instance of this class represents the Scheme NIL constant. 
 * Use the instance field to get a unique instance.
 * 
 */
public class NIL implements LispObject {

	/**
	 * Unique instance of NIL object.
	 */
	public static NIL instance = new NIL();

	private NIL() { }
	
	public String toString() {
		return "nil";
	}
	
}
