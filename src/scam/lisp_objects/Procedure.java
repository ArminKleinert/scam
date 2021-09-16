package scam.lisp_objects;

import scam.exceptions.LispError;
import scam.system.Environment;

/**
 * The Procedure class extends LispObject and implements a procedure that,
 * upon evaluation, gets executed (via the abstract execute method) and returns
 * a result.
 */
@FunctionalInterface
public interface Procedure  extends LispObject {

	/**
	 * Execute procedure with given operands in given environment.
	 */
	LispObject execute(LispObject t, Environment u) throws LispError;

	public String toString();
}
