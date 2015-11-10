package Lib;

import java.io.*;
import java.util.*;

import Exceptions.LispError;
import Exceptions.WrongNofArgsError;
import Exceptions.WrongTypeError;
import LispObjects.Cons;
import LispObjects.LString;
import LispObjects.LambdaProcedure;
import LispObjects.LispObject;
import LispObjects.MacroProcedure;
import LispObjects.NIL;
import LispObjects.NumberInteger;
import LispObjects.NumberReal;
import LispObjects.Procedure;
import LispObjects.Symbol;
import System.Environment;
import System.Kernel;
import System.Parser;

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
	class Pcar extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || (operands.asCons().size()!=1)) throw new WrongNofArgsError();
			return Kernel.eval(operands.asCons().objectAt(0), environment).asCons().getCar();
		}
	}

	/**
	 * (CDR <list>)
	 * 
	 * Returns the CDR element of <list>
	 */
	class Pcdr extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || (operands.asCons().size()!=1)) throw new WrongNofArgsError();
			return Kernel.eval(operands.asCons().objectAt(0), environment).asCons().getCdr();
		}
	}

	/**
	 *  (CONS <OBJECT> <LIST>)
	 *  
	 *  Returns a new cons object where CAR is <OBJECT> and CDR is <LIST>.
	 */
	class Pcons extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance || (operands.asCons().size()!=2)) throw new WrongNofArgsError();
			operands = Kernel.evalList(operands, environment);
			return new Cons(operands.asCons().objectAt(0), operands.asCons().objectAt(1));
		}
	}

	/**
	 * (LAMBDA <PARAMETER-LIST> <BODY>)
	 * 
	 * Returns a procedure that represents the given lambda expression.
	 */
	class Plambda extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size() < 2) throw new WrongNofArgsError();
			// Get parameter list and body (place in (begin ...) list)
			LispObject parameter_list = operands.asCons().objectAt(0);
			LispObject body = new Cons(new Symbol("begin"), operands.asCons().getCdr());
			return new LambdaProcedure(parameter_list, body, environment);
		}
	}

	// (BEGIN <OBJECT1>*)
	class Pbegin extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) return NIL.instance;
			Cons operandsAsCons = operands.asCons();
			int size = operandsAsCons.size();
			/* Mark last expression as tail if last expression is a list */
			LispObject lastExpression = operandsAsCons.objectAt(size-1);
			if (lastExpression.isCons()) lastExpression.asCons().tail = true;
			/* Evaluate expressions */
			LispObject o = NIL.instance;
			for (int i = 0; i < size; i++) {
				o = Kernel.eval(operandsAsCons.objectAt(i), environment);
			}
			return o;
		}
	}

	// (EQUAL? <OBJECT1> <OBJECT2>)
	class Pequal extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject o1 = Kernel.eval(operands.asCons().objectAt(0), environment);
			LispObject o2 = Kernel.eval(operands.asCons().objectAt(1), environment);
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
	class Peq extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject o1 = Kernel.eval(operands.asCons().objectAt(0), environment);
			LispObject o2 = Kernel.eval(operands.asCons().objectAt(1), environment);
			if (o1 == o2) return Kernel.one;
			return NIL.instance;
		}
	}

	// (EQV? <OBJECT1> <OBJECT2>)
	class Peqv extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject o1 = Kernel.eval(operands.asCons().objectAt(0), environment);
			LispObject o2 = Kernel.eval(operands.asCons().objectAt(1), environment);
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
	class Pobjeq extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject o1 = Kernel.eval(operands.asCons().objectAt(0), environment);
			LispObject o2 = Kernel.eval(operands.asCons().objectAt(1), environment);
			if (o1.getClass() == o2.getClass()) return Kernel.one;
			return NIL.instance;
		}
	}

	// (MACRO <PARAMETER-LIST> <BODY>)
	class Pmacro extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size() < 2) throw new WrongNofArgsError();
			// Get parameter list and body (place in (begin ...) list)
			LispObject parameter_list = operands.asCons().objectAt(0);
			LispObject body = new Cons(new Symbol("begin"), operands.asCons().getCdr());
			return new MacroProcedure(parameter_list, body);
		}
	}

	// (COND <(test exp)>*)
	class Pcond extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size() < 1) throw new WrongNofArgsError();
			// Process test/exp lists
			for (int i = 0; i < operands.asCons().size(); i++) {
				Cons clause = operands.asCons().objectAt(i).asCons();
				LispObject first = clause.getCar();
				// (else <expression>*) : evaluate and return expressions
				if (first.isSymbol() && first.asSymbol().getName().equals("else")) return new Pbegin().execute(clause.getCdr(), environment);
				// (<test> => <procedure>) : apply procedure
				LispObject testResult = Kernel.eval(first, environment);
				if (testResult != NIL.instance && clause.size() > 1 && clause.objectAt(1).isSymbol() && clause.objectAt(1).asSymbol().getName().equals("=>")) {
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
	class Pquote extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			return operands.asCons().objectAt(0);
		}
	}

	// (RANDOM <NUMBER>)
	class Prandom extends Procedure {
		Random random = new Random((new Date()).getTime());
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			LispObjects.LispNumber n = Kernel.eval(operands.asCons().objectAt(0), environment).asNumber();
			if (n.isInteger()) return new NumberInteger(new Float(random.nextFloat() * n.toInteger().getValue()).longValue());
			if (n.isReal()) return new NumberReal(random.nextFloat() * n.toReal().getValue());
			throw new WrongTypeError("Integer or Real", n);
		}
	}

	// (DEFINE <SYMBOL> <EXPRESSION>)
	class Pdefine extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject value = Kernel.eval(operands.asCons().objectAt(1), environment);
			environment.defineGlobal(operands.asCons().objectAt(0).asSymbol(), value);
			return value;
		}
	}

	// (set! <SYMBOL> <EXPRESSION>)
	class Pset extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=2) throw new WrongNofArgsError();
			LispObject value = Kernel.eval(operands.asCons().objectAt(1), environment);
			environment.set(operands.asCons().objectAt(0).asSymbol(), value);
			return value;
		}
	}

	// (LET ([SYMBOL EXPRESSION]*) <EXPRESSION>*)
	class Plet extends Procedure {
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
					Symbol s = binding.objectAt(0).asSymbol();
					LispObject v = Kernel.eval(binding.objectAt(1), environment);
					closure.define(s, v);
				}
			}
			
			/* Evaluate expressions with closure environment */
			return new Pbegin().execute(operands.asCons().getCdr(), closure);
		}
	}

	// (LET* ([SYMBOL EXPRESSION]*) <EXPRESSION>*)
	class Pletstar extends Procedure {
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
					Symbol s = binding.objectAt(0).asSymbol();
					LispObject v = Kernel.eval(binding.objectAt(1), environment);
					closure = closure.getClosure();
					closure.define(s, v);
				}
			}
			
			/* Evaluate expressions with closure environment */
			return new Pbegin().execute(operands.asCons().getCdr(), closure);
		}
	}
	
	// (LETREC ([SYMBOL EXPRESSION]*) <EXPRESSION>*)
	class Pletrec extends Procedure {
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
					Symbol s = binding.objectAt(0).asSymbol();
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
					Symbol s = binding.objectAt(0).asSymbol();
					LispObject v = Kernel.eval(binding.objectAt(1), closure);
					closure.set(s, v);
				}
			}
			
			/* Evaluate expressions with closure environment */
			return new Pbegin().execute(operands.asCons().getCdr(), closure);
		}
	}

	// (THROW <STRING>)
	class Pthrow extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			throw new LispError(operands.asCons().objectAt(0).asString().getString());
		}
	}

	// (CATCH <EXPRESSION>)
	class Pcatch extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			LispObject o;
			try {
				o = Kernel.eval(operands.asCons().objectAt(0), environment); // evaluate normally
			} catch (LispError e) {
				o = new LString(e.getMessage()); // convert LispError into string
			} catch (StackOverflowError e) {
				o = new LString("Java stack overflow");
			}
			return o;
		}
	}
	
	// (READ)
	class Pread extends Procedure {
		Parser parser = new Parser(new InputStreamReader(System.in), true);
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands != NIL.instance) throw new WrongNofArgsError();
			return parser.parseObject();
		}
	}
	
	// (PRINT <EXPRESSION>)
	class Pprint extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			operands = Kernel.evalList(operands, environment);
			if (operands == NIL.instance) throw new WrongNofArgsError();
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			System.out.println(operands.asCons().objectAt(0).toString());
			return operands.asCons().objectAt(0);
		}
	}
	
	// (LOAD <filename-string>)
	class Pload extends Procedure {
		public LispObject execute(LispObject operands, Environment environment) throws LispError {
			if (operands.asCons().size()!=1) throw new WrongNofArgsError();
			// Open file
			String filename = Kernel.eval(operands.asCons().objectAt(0), environment).asString().getString();
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
