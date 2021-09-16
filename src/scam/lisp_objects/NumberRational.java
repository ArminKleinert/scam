package scam.lisp_objects;

import scam.exceptions.ParseError;

/** 
 * NumberInteger extends LispNumber and implements an integer number.
 */
public class NumberRational extends LispNumber {

	private long denominator;
	private long numerator;
	
	public NumberRational(long numerator, long denominator) {
		this.denominator = denominator;
		this.numerator = numerator;
	}
	
	public NumberRational(String s) throws ParseError {
		this.numerator = Integer.parseInt(s.substring(0, s.indexOf('/')));
		//if (denominator == 0) throw new LispError("Division by zero");
		this.denominator = Integer.parseInt(s.substring(s.indexOf('/') + 1, s.length()));
	}
	
	public long getDenominator() {
		return denominator;
	}

	public long getNumerator() {
		return numerator;
	}
	
	/**
	 * Reduce the denominator and numerator to the lowest values possible.
	 */
	public void reduce() {
		long gcd = gcd(numerator, denominator);
		numerator = numerator / gcd;
		denominator = denominator / gcd;
	}
		
	/**
	 * @return The greatest common divisor of a and b.
	 */
	private static long gcd(long a, long b) {
		long t;
		while (b != 0) {
			t = b;
			b = a % b;
			a = t;
		}
		return a;
	}
	
	public NumberInteger toInteger() {
		reduce();
		if (denominator == 1) return new NumberInteger(numerator);
		return null;
	}

	public NumberRational toRational() {
		return this;
	}

	public NumberReal toReal() {
		return new NumberReal(Long.valueOf(numerator).floatValue() / Long.valueOf(denominator).floatValue());
	}
	
	public String toString() {
		reduce();
		String s = Long.toString(numerator);
		if (denominator != 1) s = s + "/" + denominator;
		return s;
	}
}
