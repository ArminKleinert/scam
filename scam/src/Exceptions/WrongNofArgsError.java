package Exceptions;

public class WrongNofArgsError extends LispError {

	public WrongNofArgsError() {
		super("Wrong number of arguments");
	}

}
