package scam.exceptions;

import scam.lisp_objects.Symbol;

public class UnboundVariableError extends LispError {

	public UnboundVariableError(String variable) {
		super("Unbound variable: " + variable);
	}

	public UnboundVariableError(Symbol symbol) {
		this( symbol.getName() );
	}


}
