package scam.lisp_objects;

//
//  MacroProcedure.java
//  LispII
//

import java.util.ArrayList;

import scam.exceptions.LispError;
import scam.system.Environment;
import scam.system.Kernel;

/**
 * MacroProcedure implements Procedure and implements a user defined function. MacroProcedure is 
 * different from LambdaProcedure in that the arguments are not evaluated. This permits the
 * definition of functions (called special forms in Scheme) such as IF. Furthermore, MacroProcedure
 * holds no reference to a closure environment, but is instead simply executed in the environment
 * in which it is called. This provides a kind of 'macro' behavior.
 *
 */
public class MacroProcedure implements Procedure {

	private final ParamTreeNode paramTreeRoot;
	private final LispObject macroBody;

	/**
	 * Construct macro procedure. 
	 * 
	 * @param parameters Parameters of macro.
	 * @param body Body of macro.
	 * @throws LispError .
	 */
	public MacroProcedure(LispObject parameters, LispObject body) throws LispError {
		paramTreeRoot = new NevalList(parameters);
		macroBody = body;		
	}

	/**
	 * Execute macro with given operands in given environment.
	 */
	public LispObject execute(LispObject operands, Environment environment) throws LispError {
		Environment envInstance = environment.getClosure();
		LispObject o = paramTreeRoot.process(operands, envInstance, macroBody);
		if (o.isCons()) o.asCons().tail = true;
		return Kernel.eval(o, envInstance);
	}
		
	/**
	 * MacroParamTreeNode is the base class for parameter tree nodes for macro procedures.
	 * The tree is constructed from the parameter list through the constructors.
	 * Parameters are processed by passing the argument list to the process method of the root node.
	 */
	private static abstract class ParamTreeNode {
		abstract public LispObject process(LispObject o, Environment environment, LispObject body) throws LispError;
	}

	private class EvalList extends NevalList { 
		EvalList(Cons c) throws LispError {
			super(c);
		}
		public LispObject process(LispObject o, Environment environment, LispObject body) throws LispError {
			return super.process(Kernel.eval(o, environment.getParentEnv()), environment, body);
		}
	}
	
	private class NevalList extends ParamTreeNode { 
		protected ArrayList<ParamTreeNode> v = new ArrayList<>();
		protected ParamTreeNode tail = null;
		
		private NevalList(LispObject c) throws LispError { 
			LispObject o = c;
			while(o.isCons()) {
				o = processElement(o.asCons());
				o = o.asCons().getCdr();
			}
			if (o != NIL.instance) {
				// only NevalParam for tail!!
				tail = new NevalParam(o.asSymbol().getName().substring(1));
			}
		}
		
		private Cons processElement(Cons c) throws LispError {
			if (c.getCar().isSymbol() && c.getCar().asSymbol().getName().equals("@")) {
				c = c.getCdr().asCons();
				v.add(new NevalList(c.getCar().asCons()));
			} else if (c.getCar().isSymbol() && c.getCar().asSymbol().getName().startsWith("@")) {
				v.add(new NevalParam(c.getCar().asSymbol().getName().substring(1)));
			} else if (c.getCar().isSymbol()) {
				v.add(new EvalParam(c.getCar().asSymbol()));
			} else {
				v.add(new EvalList(c.getCar().asCons()));
			}
			return c;
		}
				
		public LispObject process(LispObject o, Environment environment, LispObject body) throws LispError {
			LispObject c = o.asCons();
			for (ParamTreeNode n: v) {
				body = n.process(c.asCons().getCar(), environment, body);
				c = c.asCons().getCdr();
			}
			if (tail != null) body = tail.process(c, environment, body);
			return body;
		}
	}
	
	/**
	 * Param Tree Node for parameters that must be evaluated (i.e., non-macro parameters).
	 */
	private static class EvalParam extends ParamTreeNode {
		private final Symbol param;
		EvalParam(Symbol s) throws LispError {
			param = s.asSymbol();
		}		
		public LispObject process(LispObject o, Environment environment, LispObject body) throws LispError {
			environment.define(param, Kernel.eval(o, environment.getParentEnv())); // .superEnv ??
			return body;
		}
	}
	
	/**
	 * Param Tree Node for parameters that must not be evaluated (i.e., macro parameters preceded by "@").
	 */
	private static class NevalParam extends ParamTreeNode {
		private final String name;
		NevalParam(String s) {
			name = s;
		}
		public LispObject process(LispObject o, Environment environment, LispObject body) throws LispError {
			return subst(name, o, body);
		}
		private LispObject subst(String s, LispObject o, LispObject exp) throws LispError {
			if (exp.isCons()) return new Cons(subst(s, o, exp.asCons().getCar()), subst(s, o, exp.asCons().getCdr()));
			if (exp.isSymbol() && exp.asSymbol().getName().equals(s)) return o;
			return exp;
		}
	}
}
