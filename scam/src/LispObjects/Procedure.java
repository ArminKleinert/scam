package LispObjects;

import Exceptions.LispError;
import System.Environment;

/**
 * The Procedure class extends LispObject and implements a procedure that,
 * upon evaluation, gets executed (via the abstract execute method) and returns
 * a result.
 */
public abstract class Procedure extends LispObject {

	/**
	 * Execute procedure with given operands in given environment.
	 */
	public abstract LispObject execute(LispObject operands, Environment environment) throws LispError;
	
	public String toString() {
		return "#procedure";
	}
}
