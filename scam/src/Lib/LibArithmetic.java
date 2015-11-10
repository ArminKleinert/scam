package Lib;

import Exceptions.LispError;
import Exceptions.WrongNofArgsError;
import Exceptions.WrongTypeError;
import LispObjects.LispObject;
import LispObjects.NIL;
import LispObjects.NumberInteger;
import LispObjects.NumberRational;
import LispObjects.NumberReal;
import LispObjects.Procedure;
import LispObjects.Symbol;
import System.Environment;
import System.Kernel;

//
//  LibArithmetic.java
//  LispII
//

//
// Anonymous action classes implement this interface, to define how to perform a calculation on different types of numbers
//

interface ArithmeticAction {
	public LispObject doWithReals(float f1, float f2) throws LispError;
	public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError;
	public LispObject doWithIntegers(long i1, long i2) throws LispError;
}

//
// LibArithmetic
// 
// Procedures in this library work on operands of type number
//

public class LibArithmetic {

	public void init(Environment e) {
		e.define(new Symbol("+"), new Parithmetic(addition, new NumberInteger(0), null, new NumberInteger(0)));
		e.define(new Symbol("-"), new Parithmetic(substraction, null, null, new NumberInteger(0)));
		e.define(new Symbol("/"), new Parithmetic(division, null, null, new NumberInteger(1)));
		e.define(new Symbol("*"), new Parithmetic(multiplication, new NumberInteger(1), null, new NumberInteger(1)));
		e.define(new Symbol("modulo"), new Parithmetic(modulo, null, null, null));
		e.define(new Symbol("quotient"), new Parithmetic(quotient, null, null, null));
		e.define(new Symbol("="), new Parithmetic(compare, null, Kernel.one, null));
		e.define(new Symbol(">"), new Parithmetic(greater, null, Kernel.one, null));
		e.define(new Symbol("<"), new Parithmetic(smaller, null, Kernel.one, null));
	}

	//
	// ArithmeticActions define how to perform an arithmetic action on different types of numbers
	//
	
	public static final ArithmeticAction compare = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			return (f1 == f2)? Kernel.one : NIL.instance;
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			long lcm = lcm(denominator1, denominator2);
			return (numerator1 * (lcm / denominator1) == numerator2 * (lcm / denominator2)) ? Kernel.one : NIL.instance;
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			return (i1 == i2)? Kernel.one : NIL.instance;
		}
	};
		
	public static final ArithmeticAction addition = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			return new NumberReal(f1 + f2);
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			long lcm = lcm(denominator1, denominator2);
			return new NumberRational(numerator1 * (lcm/denominator1) + numerator2 * (lcm/denominator2), lcm);
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			return new NumberInteger(i1 + i2);
		}
	};

	public static final ArithmeticAction substraction = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			return new NumberReal(f1 - f2);
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			long lcm = lcm(denominator1, denominator2);
			return new NumberRational(numerator1 * (lcm/denominator1) - numerator2 * (lcm/denominator2), lcm);
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			return new NumberInteger(i1 - i2);
		}
	};

	public static final ArithmeticAction division = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			return new NumberReal(f1 / f2);
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			return new NumberRational(numerator1 * denominator2, denominator1 * numerator2);
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			return new NumberRational(i1, i2);
		}
	};

	public static final ArithmeticAction multiplication = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			return new NumberReal(f1 * f2);
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			return new NumberRational(numerator1 * numerator2, denominator1 * denominator2);
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			return new NumberInteger(i1 * i2);
		}
	};

	public static final ArithmeticAction greater = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			return (f1 > f2)? new NumberReal(f2) : NIL.instance;
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			long lcm = lcm(denominator1, denominator2);
			return (numerator1 * (lcm / denominator1) > numerator2 * (lcm / denominator2)) ? new NumberRational(numerator2, denominator2) : NIL.instance;
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			return (i1 > i2)? new NumberInteger(i2) : NIL.instance;
		}
	};

	public static final ArithmeticAction smaller = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			return (f1 < f2)? new NumberReal(f2) : NIL.instance;
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			long lcm = lcm(denominator1, denominator2);
			return (numerator1 * (lcm / denominator1) < numerator2 * (lcm / denominator2)) ? new NumberRational(numerator2, denominator2) : NIL.instance;
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			return (i1 < i2)? new NumberInteger(i2) : NIL.instance;
		}
	};

	public static final ArithmeticAction modulo = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			throw new WrongTypeError("Integer", "Real");
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			throw new WrongTypeError("Integer", "Rational");
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			if (i2 == 0) throw new LispError("Division by zero");
			return new NumberInteger(i1 % i2);
		}
	};

	public static final ArithmeticAction quotient = new ArithmeticAction() {
		public LispObject doWithReals(float f1, float f2) throws LispError {
			throw new WrongTypeError("Integer", "Real");
		}
		public LispObject doWithRationals(long numerator1, long denominator1, long numerator2, long denominator2) throws LispError {
			throw new WrongTypeError("Integer", "Rational");
		}
		public LispObject doWithIntegers(long i1, long i2) throws LispError {
			if (i2 == 0) throw new LispError("Division by zero");
			return new NumberInteger(i1 / i2);
		}
	};

	//
	// perform calls the appropriate method of an action object, depending on the input number types
	// 

	public static LispObject perform(LispObjects.LispNumber n1, LispObjects.LispNumber n2, ArithmeticAction a) throws LispError {
		// One or two reals
		if (n1.isReal() || n2.isReal())
			return a.doWithReals(n1.toReal().getValue(), n2.toReal().getValue());
		// One or two rationals
		if (n1.isRational() || n2.isRational())
			return a.doWithRationals(n1.toRational().getNumerator(), n1.toRational().getDenominator(), n2.toRational().getNumerator(), n2.toRational().getDenominator());
		// Otherwise they are two integers
		return a.doWithIntegers(n1.toInteger().getValue(), n2.toInteger().getValue());
	}

	//
	// Parithmetic is a procedure class for using ArithmeticActions
	// 
	// Parameters of constructor:
	// action					The ArithmeticAction to perform on the operands
	// resultIfZeroOperands		What to return if there are 0 operands, null throws a wrong num of args error in this case
	// resultIfOneOperand		Same as above, for 1 operand
	// firstValueIfOneOperand	What to use as first value for the action if there is one argument, resultIfOneOperand must be null
	//

	class Parithmetic extends Procedure {
		
		ArithmeticAction action;
		LispObject resultIfZeroOperands;
		LispObject resultIfOneOperand;
		LispObjects.LispNumber firstValueIfOneOperand;
				
		Parithmetic(ArithmeticAction action, LispObject resultIfZeroOperands, LispObject resultIfOneOperand, LispObjects.LispNumber firstValueIfOneOperand) {
			this.action = action;
			this.resultIfZeroOperands = resultIfZeroOperands;
			this.resultIfOneOperand = resultIfOneOperand;
			this.firstValueIfOneOperand = firstValueIfOneOperand;
		}
		
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			// Check if there are no operands
			if (operands == NIL.instance) {
				if (resultIfZeroOperands == null) throw new WrongNofArgsError();
					else return resultIfZeroOperands;
			}
			
			// Check if there is one operand
			LispObjects.LispNumber firstOperand = Kernel.eval(operands.asCons().objectAt(0), environment).asNumber();
			if (operands.asCons().size() == 1) {
				if (resultIfOneOperand == null) {
					if (firstValueIfOneOperand == null) {
						throw new WrongNofArgsError();
					} else {
						return perform(firstValueIfOneOperand, firstOperand, action);
					}
				} else {
					return resultIfOneOperand;
				}					
			}
			
			// 2 or more operands
			LispObject result = firstOperand;
			operands = operands.asCons().getCdr();
			while (operands != NIL.instance) {
				result = perform(result.asNumber(), Kernel.eval(operands.asCons().getCar(), environment).asNumber(), action);
				if (!result.isNumber()) return result;
				operands = operands.asCons().getCdr();
			}
			
			return result;
		}
	}

	// Least common multiple
	private static long lcm(long a, long b) {
		return (a * b) / gcd(a, b);
	}

	// Greatest common divisor
	private static long gcd(long a, long b) {
		long t;
		while (b != 0) {
			t = b;
			b = a % b;
			a = t;
		}
		return a;
	}
}

