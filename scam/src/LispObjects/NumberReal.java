package LispObjects;

public class NumberReal extends LispNumber {
	
	private float real;

	public NumberReal(float n) {
		real = n;
	}

	public float getValue() {
		return real;
	}
	
	public NumberInteger toInteger() {
		return null;
	}

	public NumberRational toRational() {
		return null;
	}

	public NumberReal toReal() {
		return this;
	}

	public String toString() {
		return new Float(real).toString();
	}
}
