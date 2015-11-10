package System;

import java.util.HashMap;
import java.util.Hashtable;

import Exceptions.LispError;
import Exceptions.UnboundVariableError;
import LispObjects.LispObject;
import LispObjects.Symbol;

/**
 * This class implements the scheme environment model. An environment consists of a
 * number of bindings of variables to values, and (optionally) a reference to a parent
 * environment, from which other bindings are inherited.
 */
public class Environment {

	/* HashMap that contains bindings. We set it at null initially and construct it lazily */
	private HashMap<String, LispObject> t = null;

	/* Optional parent environment */
	private Environment parentEnv = null;
	
	/**
	 * Construct environment with no parent.
	 */
	public Environment() {}
	
	/**
	 * Construct environment with parent.
	 * 
	 * @param e Parent environment.
	 */
	public Environment(Environment e) {
		parentEnv = e; 
	}

	/**
	 * This method implements the Scheme set function.
	 * It finds the first ancestor environment in which
	 * the given symbol is bound and rebinds it, or throws
	 * an exception if the variable is not bound in this
	 * environment or one of its ancestors.
	 * 
	 * @param s
	 * @param o
	 * @throws LispError
	 */
	public void set(Symbol s, LispObject o) throws LispError {
		if ((s == null) || (o == null)) throw new NullPointerException();
		
		/* Lazy init */
		if (t == null) t = new HashMap<String, LispObject>();
		
		/* Rebind if variable is bound at this level */
		if (t.get(s.getName()) != null) {
			t.put(s.getName().toLowerCase(), o);
		} else {
			/* Otherwise rebind in parent env or throw unbound error if top level is reached */
			if (parentEnv != null) {
				parentEnv.set(s, o);
			} else {
				throw new UnboundVariableError(s);
			}
		}		
	}
	
	/**
	 * This method implements the Scheme define function. 
	 * 
	 * @param s Symbol to bind to. 
	 * @param o Value to bind symbol to.
	 */
	public void define(Symbol s, LispObject o) {
		if (t == null) t = new HashMap<String, LispObject>();
		t.put(s.getName().toLowerCase(), o);
	}

	/**
	 * This method implements the Scheme define function. 
	 * 
	 * @param s String of symbol to bind to. 
	 * @param o Value to bind symbol to.
	 */
	public void define(String s, LispObject o) {
		if (t == null) t = new HashMap<String, LispObject>();
		t.put(s.toLowerCase(), o);
	}

	/**
	 * Calls define in the top level environment (i.e. the highest
	 * available ancestor).
	 * 
	 * @param s Symbol to bind to. 
	 * @param o Value to bind symbol to.
	 */
	public void defineGlobal(Symbol s, LispObject o) {
		if (parentEnv == null) {
			if (t == null) t = new HashMap<String, LispObject>();
			t.put(s.getName().toLowerCase(), o);
		} else {
			parentEnv.defineGlobal(s, o);
		}
	}
	
	/**
	 * Lookup value bound to symbol. Returns the value bound in the 
	 * highest available ancestor in which the symbol is bound.
	 * 
	 * @param s Symbol.
	 * @return Value of symbol in the highest available ancestor in which the symbol is bound.
	 * @throws LispError If not bound.
	 */
	public LispObject lookup(Symbol s) throws LispError {
		LispObject o = null;
		if (t != null) o = (LispObject)t.get(s.getName().toLowerCase());
		if ((o == null) && (parentEnv != null)) o = parentEnv.lookup(s);
		if (o == null) throw new UnboundVariableError(s);
		return o;
	}

	/**
	 * @return A new environment whose parent is the current one. 
	 */
	public Environment getClosure() {
		return new Environment(this);
	}

	/**
	 * @return The parent environment.
 	 */
	public Environment getParentEnv() {
		if (parentEnv == null) return this;
		return parentEnv;
	}


}
