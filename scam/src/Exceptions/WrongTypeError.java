package Exceptions;

import LispObjects.LispObject;

public class WrongTypeError extends LispError {
  String expected;
  String found;

  public WrongTypeError(String e, String f) {
  	super("Wrong type: found " + f + ", expected " + e);
	found = f;
	expected = e;
  }

  public WrongTypeError(String e, LispObject f) {
  	super("Wrong type: found " + f.toString() + ", expected " + e);
	expected = e;
	found = f.toString();
  }

}
