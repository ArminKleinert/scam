package LispObjects;

import Exceptions.WrongTypeError;

public abstract class LispObject implements Cloneable { 

	public abstract String toString();

	public LispNumber asNumber() throws WrongTypeError {
		if (this instanceof LispNumber) return (LispNumber)this;
		throw new WrongTypeError("Number", this);
	}
	
	public Symbol asSymbol() throws WrongTypeError {
		if (this instanceof Symbol) return (Symbol)this;
		throw new WrongTypeError("Symbol", this);
	}
	
	public Cons asCons() throws WrongTypeError {
		if (this instanceof Cons) return (Cons)this;
		throw new WrongTypeError("Cons", this);
	}

	public Procedure asProcedure() throws WrongTypeError {
		if (this instanceof Procedure) return (Procedure)this;
		throw new WrongTypeError("Procedure", this);
	}
	
	public LString asString() throws WrongTypeError {
		if (this instanceof LString) return (LString)this;
		throw new WrongTypeError("String", this);
	}
		
	public Tail asTail() throws WrongTypeError {
		if (this instanceof Tail) return (Tail)this;
		throw new WrongTypeError("Tail object", this);
	}
	
	// Type checking
	
	public boolean isNumber() {
		if (this instanceof LispNumber) return true;
		return false;
	}
	
	public boolean isSymbol() {
		if (this instanceof Symbol) return true;
		return false;
	}
	
	public boolean isCons() {
		if (this instanceof Cons) return true;
		return false;
	}

	public boolean isProcedure() {
		if (this instanceof Procedure) return true;
		return false;
	}
	
	public boolean isString() {
		if (this instanceof LString) return true;
		return false;
	}
	
	public boolean isTail() {
		if (this instanceof Tail) return true;
		return false;
	}
	
	
}