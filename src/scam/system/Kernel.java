package scam.system;

import java.io.StringReader;

import scam.exceptions.LispError;
import scam.lib.LibArithmetic;
import scam.lib.LibSystem;
import scam.lisp_objects.Cons;
import scam.lisp_objects.LispObject;
import scam.lisp_objects.NIL;
import scam.lisp_objects.NumberInteger;
import scam.lisp_objects.NumberReal;
import scam.lisp_objects.Procedure;
import scam.lisp_objects.Symbol;
import scam.lisp_objects.Tail;

public class Kernel {

	public static final LispObject one = new NumberInteger(1);

	private final Environment topLevelEnvironment;

	/**
	 * Constructs a SC@M Kernel object. A SC@M Kernel consists essentially of a 
	 * top level environment required for evaluating expressions along with a 
	 * number of essential methods (eval and apply) used by the interpreter.
	 */
	public Kernel() {
		// Initialize top level environment
		topLevelEnvironment = new Environment();
		// Set constants
		topLevelEnvironment.define("nil", NIL.instance);
		topLevelEnvironment.define("pi", new NumberReal((float) Math.PI));
		// Load libraries				
		new LibSystem().init(topLevelEnvironment);
		new LibArithmetic().init(topLevelEnvironment);
		topLevelEnvironment.defineGlobal(new Symbol("eval"), new Peval());
		topLevelEnvironment.defineGlobal(new Symbol("apply"), new Papply());
	}
	
	/**
	 * Parse the given string and evaluates it. Returns the result of the
	 * evaluation as a LispObject.
	 * 
	 * @param statement Statement to execute.
	 * @return result of evaluation
	 * @throws LispError  .
	 */
	public LispObject execute(String statement) throws LispError {
		Parser parser = new Parser(new StringReader(statement), false);
		return eval(parser.parseObject(), topLevelEnvironment);
	}

	/**
	 * Evaluate the given lisp object in the top level environment of this
	 * kernel instance. There may be side effects, such as binding variables
     * in the top level environment.
	 * 
	 * @param o Object to evaluate.
	 * @return Result of evaluation.
	 * @throws LispError .
	 */
	public LispObject eval(LispObject o) throws LispError {
		return Kernel.eval(o, topLevelEnvironment);
	}

	/**
	 * Evaluate the given object in the given environment. This is a static method
	 * and it records every side effect in the environment that is provided, rather
	 * than the top-level environment of the kernel.
	 *  
	 * @param o Object to evaluate.
	 * @param environment Environment in which to evaluate.
	 * @return Result of evaluation.
	 * @throws LispError .
	 */
	public static LispObject eval(LispObject o, Environment environment) throws LispError {
		// Evaluate object accordingly
		if (o == NIL.instance) return o;
		if (o.isNumber()) return o;
		if (o.isString()) return o;
		if (o.isSymbol()) return environment.lookup(o.asSymbol());
		// Cons marked as tail: Tail object
		if (o.isCons() && o.asCons().tail) return new Tail(o.asCons(), environment);
		// List: procedure application (apply)
		if (o.isCons()) {
			LispObject pSymbol = o.asCons().getCar();
			Procedure p = eval(pSymbol, environment).asProcedure();
			return apply_proc(p, o.asCons().getCdr(), environment);
		}
		throw new LispError("Evaluation error for object:" + o);
	}

	/**
	 * Apply the given procedure to the given arguments in the given environment. 
	 * This is a static method and it records every side effect in the environment 
	 * that is provided, rather than the top-level environment of the kernel.
	 *
	 * @param p Procedure to apply.
	 * @param arguments Arguments to apply procedure to.
	 * @param environment Environment in which to apply the procedure.
	 * @return Result of applying procedure.
	 * @throws LispError .
	 */
	public static LispObject apply_proc(Procedure p, LispObject arguments, Environment environment) throws LispError {
		// Execute procedure
		LispObject r = p.execute(arguments, environment);
		// if procedure/Tail execution returned a Tail object, perform eval/apply here
		while (r.isTail()) {
			Cons form = r.asTail().getForm();
			form.tail = false;
			Environment env = r.asTail().getEnvironment();
			LispObject pSymbol = form.getCar().asSymbol();
			p = eval(pSymbol.asSymbol(), env).asProcedure();
			r = p.execute(form.getCdr(), env);
		}
		return r;
	}
	
	/**
	 * This method works just like the eval method but it accepts a list (or a  
	 * sequence of cons objects) of objects as input and returns a list of the 
	 * same size consisting of the results of evaluating these objects. 
	 * 
	 * @param list .
	 * @param environment .
	 * @return .
	 * @throws LispError .
	 */
	public static LispObject evalList(LispObject list, Environment environment)	throws LispError {
		if (list == NIL.instance) return list;
		Cons evalList = new Cons(); // TODO: change (no empty constructor)
		for (int i = 0; i < list.asCons().size(); i++) 
			evalList.append(eval(list.asCons().objectAt(i), environment));
		return evalList;
	}

	
	/**
	 * This class wraps the static apply method of the Kernel class into an actual Procedure class so
	 * that it can be used on the language level.
	 */
	public static class Papply implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			/* Check input */
			Cons c = operands.asCons();
			if (c.size() != 2) throw new LispError("Wrong number of arguments");
			/* Apply */
			return apply_proc(eval(c.getCar(), environment).asProcedure(), eval(c.getCadr(), environment), environment);
		}
	}

	/**
	 * This class wraps the static eval method of the Kernel class method into an actual Procedure class so
	 * that it can be used on the language level.
	 */
	public static class Peval implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			/* Check input */
			Cons c = operands.asCons();
			if (c.size() != 1) throw new LispError("Wrong number of arguments");
			/* Evaluate arguments twice! */
			return eval(eval(c.getCar(), environment), environment);
		}
	}
}
