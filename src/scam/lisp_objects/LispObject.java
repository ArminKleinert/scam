package scam.lisp_objects;

import scam.exceptions.WrongTypeError;

public interface LispObject {//implements Cloneable {

	String toString();

	default LispNumber asNumber() throws WrongTypeError {
		if (this instanceof LispNumber) return (LispNumber)this;
		throw new WrongTypeError("Number", this);
	}

	default Symbol asSymbol() throws WrongTypeError {
		if (this instanceof Symbol) return (Symbol)this;
		throw new WrongTypeError("Symbol", this);
	}

	default Cons asCons() throws WrongTypeError {
		if (this instanceof Cons) return (Cons)this;
		throw new WrongTypeError("Cons", this);
	}

	default Procedure asProcedure() throws WrongTypeError {
		if (this instanceof Procedure) return (Procedure)this;
		throw new WrongTypeError("Procedure", this);
	}

	default LString asString() throws WrongTypeError {
		if (this instanceof LString) return (LString)this;
		throw new WrongTypeError("String", this);
	}

	default Tail asTail() throws WrongTypeError {
		if (this instanceof Tail) return (Tail)this;
		throw new WrongTypeError("Tail object", this);
	}
	
	// Type checking

	default boolean isNumber() {
		return this instanceof LispNumber;
	}

	default boolean isSymbol() {
		return this instanceof Symbol;
	}

	default boolean isCons() {
		return this instanceof Cons;
	}

	default boolean isProcedure() {
		return this instanceof Procedure;
	}

	default boolean isString() {
		return this instanceof LString;
	}

	default boolean isTail() {
		return this instanceof Tail;
	}
	
	
}