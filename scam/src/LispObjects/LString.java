package LispObjects;

/**
 * This class extends LispObject and implements a string value.
 */
public class LString extends LispObject {

	private final String str;

	/**
	 * Construct string.
	 * 
	 * @param str The string.
	 */
	public LString(String str) {
		this.str = str;
	}

	/**
	 * @return String value.
	 */
	public String getString() {
		return str;
	}

	public String toString() {
		return "\"" + str + "\"";
	}
}
