package LispObjects;

/**
 * The Symbol class extends LispObject. A symbol holds a name and, when evaluated,
 * returns the value bound to that name (see eval function).
 */
public class Symbol extends LispObject {

	private final String name;

	public Symbol(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

}