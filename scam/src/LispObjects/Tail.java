package LispObjects;

import Exceptions.LispError;
import System.Environment;

/**
 * A tail procedure object is used to implement tail recursion. If a procedure is evaluated
 * and the last part consists of a call to another function, the result will be a tail procedure,
 * which holds a reference to the tail itself as well as the environment in which it must be 
 * evaluated. The tail object can then be executed without causing stack growth due to an extra
 * call to eval/apply.
 */
public class Tail extends Procedure {

	private final Cons form;
	private final Environment env;
	
	public Tail(Cons form, Environment env) {
		this.form = form;
		this.env = env;
	}
	
	public Cons getForm() {
		return form;
	}
	
	public Environment getEnvironment() {
		return env;
	}
	
	public String toString() { return "#tail : " + form; }

	@Override
	public LispObject execute(LispObject operands, Environment environment)	throws LispError {
		return null;
	}

}
