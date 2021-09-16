package scam.lib;

import java.io.*;
import java.util.*;

import scam.exceptions.LispError;
import scam.exceptions.WrongNofArgsError;
import scam.exceptions.WrongTypeError;
import scam.lisp_objects.*;
import scam.system.Environment;
import scam.system.Kernel;
import scam.system.Parser;

/**
 * The LibSystem class contains a collection of procedures
 * that are internally defined.
 */
public class LibSystem {

	public void init(Environment e) {
		e.define(new Symbol("car"), new Pcar());
		e.define(new Symbol("cdr"), new Pcdr());
		e.define(new Symbol("read"), new Pread());
		e.define(new Symbol("print"), new Pprint());
		e.define(new Symbol("quote"), new Pquote());
		e.define(new Symbol("catch"), new Pcatch());
		e.define(new Symbol("throw"), new Pthrow());
		e.define(new Symbol("define"), new Pdefine());
		e.define(new Symbol("set!"), new Pset());
		e.define(new Symbol("let"), new Plet());
		e.define(new Symbol("let*"), new Pletstar());
		e.define(new Symbol("letrec"), new Pletrec());
		e.define(new Symbol("lambda"), new Plambda());
		e.define(new Symbol("macro"), new Pmacro());
		e.define(new Symbol("cond"), new Pcond());
		e.define(new Symbol("eq?"), new Peq());
		e.define(new Symbol("eqv?"), new Peqv());
		e.define(new Symbol("equal?"), new Pequal());
		e.define(new Symbol("objeq?"), new Pobjeq());
		e.define(new Symbol("begin"), new Pbegin());
		e.define(new Symbol("cons"), new Pcons());
		e.define(new Symbol("load"), new Pload());
		e.define(new Symbol("random"), new Prandom());
		//e.define(new Symbol("call/cc"), new Pcallcc());
	}

	/**
	 * (CAR <list>)
	 * 
	 * Returns the CAR element of <list>
	 */
	private static class Pcar implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || (operands.asCons().size()!=1)) throw new WrongNofArgsError();
			return Kernel.eval(operands.asCons().getCar(), environment).asCons().getCar();
		}
	}

	/**
	 * (CDR <list>)
	 * 
	 * Returns the CDR element of <list>
	 */
	private static class Pcdr implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || (operands.asCons().size()!=1)) throw new WrongNofArgsError();
			return Kernel.eval(operands.asCons().getCar(), environment).asCons().getCdr();
		}
	}

	/**
	 *  (CONS <OBJECT> <LIST>)
	 *  
	 *  Returns a new cons object where CAR is <OBJECT> and CDR is <LIST>.
	 */
	private static class Pcons implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || (operands.asCons().size()!=2)) throw new WrongNofArgsError();
			operands = Kernel.evalList(operands, environment);
			return new Cons(operands.asCons().getCar(), operands.asCons().getCadr());
		}
	}

	/**
	 * (LAMBDA <PARAMETER-LIST> <BODY>)
	 * 
	 * Returns a procedure that represents the given lambda expression.
	 */
	private static class Plambda implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size() < 2) throw new WrongNofArgsError();
			// Get parameter list and body (place in (begin ...) list)
			LispObject parameter_list = operands.asCons().getCar();
			LispObject body = new Cons(new Symbol("begin"), operands.asCons().getCdr());
			return new LambdaProcedure(parameter_list, body, environment);
		}
	}

	// (BEGIN <OBJECT1>*)
	private static class Pbegin implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) return NIL.instance;
			Cons operandsAsCons = operands.asCons();
			int size = operandsAsCons.size();
			/* Mark last expression as tail if last expression is a list */
			LispObject lastExpression = operandsAsCons.objectAt(size-1);
			if (lastExpression.isCons()) lastExpression.asCons().tail = true;
			/* Evaluate expressions */
			LispObject o = NIL.instance;
			for (LispObject e : operandsAsCons				 ) {
				o = Kernel.eval(e, environment);
			}
			return o;
		}
	}

	// (EQUAL? <OBJECT1> <OBJECT2>)
	private static class Pequal implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject o1 = Kernel.eval(operands.asCons().getCar(), environment);
			LispObject o2 = Kernel.eval(operands.asCons().getCadr(), environment);
			if (equal(o1, o2)) return Kernel.one;
			return NIL.instance;
		}
	}

	public static boolean equal(LispObject o1, LispObject o2) throws LispError {
		// Check for car/cdr equality when comparing lists
		if (o1.isCons() && o2.isCons()) return (equal(o1.asCons().getCar(), o2.asCons().getCar()) && equal(o1.asCons().getCdr(),o2.asCons().getCdr()));
		// Otherwise, compare with eqv
		return eqv(o1, o2);
	}

	// (EQ? <OBJECT1> <OBJECT2>)
	private static class Peq implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject o1 = Kernel.eval(operands.asCons().getCar(), environment);
			LispObject o2 = Kernel.eval(operands.asCons().getCadr(), environment);
			if (o1 == o2) return Kernel.one;
			return NIL.instance;
		}
	}

	// (EQV? <OBJECT1> <OBJECT2>)
	private static class Peqv implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject o1 = Kernel.eval(operands.asCons().getCar(), environment);
			LispObject o2 = Kernel.eval(operands.asCons().getCadr(), environment);
			if (eqv(o1, o2)) return Kernel.one;
			return NIL.instance;
		}
	}

	public static boolean eqv(LispObject o1, LispObject o2) throws LispError {
		if (o1 == o2) return true;
		if (o1.isNumber() && o2.isNumber()) return (LibArithmetic.perform(o1.asNumber(), o2.asNumber(), LibArithmetic.compare) != NIL.instance); // perform arithmetic "="
		if (o1.isSymbol() && o2.isSymbol() && (o1.asSymbol().getName().equals(o2.asSymbol().getName()))) return true;
		if (o1.isString() && o2.isString() && (o1.asString().getString().equals(o2.asString().getString()))) return true;
		if (o1.hashCode() == o2.hashCode()) return true;
		return false;
	}

	// (OBJEQ? <PARAMETER-LIST> <BODY>)
	private static class Pobjeq implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject o1 = Kernel.eval(operands.asCons().getCar(), environment);
			LispObject o2 = Kernel.eval(operands.asCons().getCadr(), environment);
			if (o1.getClass() == o2.getClass()) return Kernel.one;
			return NIL.instance;
		}
	}

	// (MACRO <PARAMETER-LIST> <BODY>)
	private static class Pmacro implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size() < 2) throw new WrongNofArgsError();
			// Get parameter list and body (place in (begin ...) list)
			LispObject parameter_list = operands.asCons().getCar();
			LispObject body = new Cons(new Symbol("begin"), operands.asCons().getCdr());
			return new MacroProcedure(parameter_list, body);
		}
	}

	// (COND <(test exp)>*)
	private static class Pcond implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size() < 1) throw new WrongNofArgsError();
			// Process test/exp lists
			for (LispObject e : operands.asCons()) {
				Cons clause = e.asCons();
				LispObject first = clause.getCar();
				// (else <expression>*) : evaluate and return expressions
				if (first.isSymbol() && first.asSymbol().getName().equals("else")) return new Pbegin().execute(clause.getCdr(), environment);
				// (<test> => <procedure>) : apply procedure
				LispObject testResult = Kernel.eval(first, environment);
				if (testResult != NIL.instance && clause.size() > 1 && clause.getCadr().isSymbol() && clause.getCadr().asSymbol().getName().equals("=>")) {
					Cons expression = new Cons(clause.objectAt(2), new Cons(first, NIL.instance)); // opnieuw evaluatie van first?!?!?
					expression.tail = true;
					return Kernel.eval(expression, environment); 
				}
				// (<test> <expression>*) : conditional evaluation
				if (testResult != NIL.instance) return new Pbegin().execute(clause.getCdr(), environment);
			}
			return NIL.instance;
		}
	}

	// (QUOTE <EXPRESSION>)
	private static class Pquote implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			return operands.asCons().getCar();
		}
	}

	// (RANDOM <NUMBER>)
	private static class Prandom implements Procedure {
		Random random = new Random((new Date()).getTime());
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			LispNumber n = Kernel.eval(operands.asCons().getCar(), environment).asNumber();
			if (n.isInteger()) return new NumberInteger(Float.valueOf(random.nextFloat() * n.toInteger().getValue()).longValue());
			if (n.isReal()) return new NumberReal(random.nextFloat() * n.toReal().getValue());
			throw new WrongTypeError("Integer or Real", n);
		}
	}

	// (DEFINE <SYMBOL> <EXPRESSION>)
	private static class Pdefine implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject value = Kernel.eval(operands.asCons().getCadr(), environment);
			environment.defineGlobal(operands.asCons().getCar().asSymbol(), value);
			return value;
		}
	}

	// (set! <SYMBOL> <EXPRESSION>)
	private static class Pset implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject value = Kernel.eval(operands.asCons().getCadr(), environment);
			environment.set(operands.asCons().getCar().asSymbol(), value);
			return value;
		}
	}

	// (LET ([SYMBOL EXPRESSION]*) <EXPRESSION>*)
	private static class Plet implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || operands.asCons().size() < 2) throw new WrongNofArgsError();

			/* Evaluate values in current environment, bind in closure */
			Environment closure = environment.getClosure();
			if (operands.asCons().getCar() != NIL.instance) {
				Cons bindings = operands.asCons().getCar().asCons();
				int size = bindings.size();
				for (int i = 0; i < size; i++) {
					Cons binding = bindings.objectAt(i).asCons();
					if (binding.size() != 2) throw new WrongNofArgsError();
					Symbol s = binding.getCar().asSymbol();
					LispObject v = Kernel.eval(binding.getCadr(), environment);
					closure.define(s, v);
				}
			}
			
			/* Evaluate expressions with closure environment */
			return new Pbegin().execute(operands.asCons().getCdr(), closure);
		}
	}

	// (LET* ([SYMBOL EXPRESSION]*) <EXPRESSION>*)
	private static class Pletstar implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || operands.asCons().size() < 2) throw new WrongNofArgsError();

			/* Evaluate and bind values in nested environments */
			Environment closure = environment;
			if (operands.asCons().getCar() != NIL.instance) {
				Cons bindings = operands.asCons().getCar().asCons();
				int size = bindings.size();
				for (int i = 0; i < size; i++) {
					Cons binding = bindings.objectAt(i).asCons();
					if (binding.size() != 2) throw new WrongNofArgsError();
					Symbol s = binding.getCar().asSymbol();
					LispObject v = Kernel.eval(binding.getCadr(), environment);
					closure = closure.getClosure();
					closure.define(s, v);
				}
			}
			
			/* Evaluate expressions with closure environment */
			return new Pbegin().execute(operands.asCons().getCdr(), closure);
		}
	}
	
	// (LETREC ([SYMBOL EXPRESSION]*) <EXPRESSION>*)
	private static class Pletrec implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || operands.asCons().size() < 2) throw new WrongNofArgsError();

			/* Bind variables */
			Environment closure = environment.getClosure();
			if (operands.asCons().getCar() != NIL.instance) {
				Cons bindings = operands.asCons().getCar().asCons();
				int size = bindings.size();
				for (int i = 0; i < size; i++) {
					Cons binding = bindings.objectAt(i).asCons();
					if (binding.size() != 2) throw new WrongNofArgsError();
					Symbol s = binding.getCar().asSymbol();
					closure.define(s, NIL.instance);
				}
			}
			
			/* Evaluate values */
			if (operands.asCons().getCar() != NIL.instance) {
				Cons bindings = operands.asCons().getCar().asCons();
				int size = bindings.size();
				for (int i = 0; i < size; i++) {
					Cons binding = bindings.objectAt(i).asCons();
					if (binding.size() != 2) throw new WrongNofArgsError();
					Symbol s = binding.getCar().asSymbol();
					LispObject v = Kernel.eval(binding.getCadr(), closure);
					closure.set(s, v);
				}
			}
			
			/* Evaluate expressions with closure environment */
			return new Pbegin().execute(operands.asCons().getCdr(), closure);
		}
	}

	// (THROW <STRING>)
	private static class Pthrow implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			throw new LispError(operands.asCons().getCar().asString().getString());
		}
	}

	// (CATCH <EXPRESSION>)
	private static class Pcatch implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			LispObject o;
			try {
				o = Kernel.eval(operands.asCons().getCar(), environment); // evaluate normally
			} catch (LispError e) {
				o = new LString(e.getMessage()); // convert LispError into string
			} catch (StackOverflowError e) {
				o = new LString("Java stack overflow");
			}
			return o;
		}
	}
	
	// (READ)
	private static class Pread implements Procedure {
		Parser parser = new Parser(new InputStreamReader(System.in), true);
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands != NIL.instance) throw new WrongNofArgsError();
			return parser.parseObject();
		}
	}
	
	// (PRINT <EXPRESSION>)
	private static class Pprint implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			operands = Kernel.evalList(operands, environment);
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			System.out.println(operands.asCons().getCar().toString());
			return operands.asCons().getCar();
		}
	}
	
	// (LOAD <filename-string>)
	private static  class Pload implements Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			// Open file
			String filename = Kernel.eval(operands.asCons().getCar(), environment).asString().getString();
			FileReader f;
			try {
				f = new FileReader(filename);
			} catch (FileNotFoundException e) {
				throw new LispError("File not found");
			}
			// Parse & eval until null (eof)
			Parser p = new Parser(f, false);
			LispObject o;
			while ((o = p.parseObject()) != null) {
				Kernel.eval(o, environment);
			}
			return new NumberInteger(1);
		}
	}

}
