package LispObjects;

/** 
 * NumberInteger extends LispNumber and implements an integer number.
 */
public class NumberInteger extends LispNumber {

	private final long number;

	public NumberInteger(long n) {
		number = n;
	}

	public long getValue() {
		return number;
	}
	
	public NumberInteger toInteger() {
		return this;
	}

	public NumberRational toRational() {
		try {
			return new NumberRational(number, 1);
		} catch (Exception e) {
			return null;
		}
	}

	public NumberReal toReal() {
		return new NumberReal(new Long(number).floatValue());
	}
	
	public String toString() {
		return new Long(number).toString();
	}
	
}
