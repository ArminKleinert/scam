package Exceptions;

import LispObjects.Symbol;

public class UnboundVariableError extends LispError {

	public UnboundVariableError(String variable) {
		super("Unbound variable: " + variable);
	}

	public UnboundVariableError(Symbol symbol) {
		super("Unbound variable: " + symbol.getName() );
	}


}
