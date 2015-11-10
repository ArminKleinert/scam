package LispObjects;

import java.util.ArrayList;
import java.util.Vector;

import Exceptions.LispError;
import Exceptions.WrongNofArgsError;
import Exceptions.WrongTypeError;
import System.Environment;
import System.Kernel;

/**
 * The lambda procedure extends Procedure and hence can be executed. A lambda procedure
 * is constructed on the basis of a function (i.e. parameters and function body) together
 * with a closure environment.
 * 
 * @author tjitze.rienstra
 *
 */
public class LambdaProcedure extends Procedure {

	private LispObject lambdaBody;
	private Environment closureEnvironment;
	private ArrayList<Symbol> params;

	/**
	 * Construct lambda procedure with given parameters, body and environment.
	 * 
	 * The parameters can be either a cons list containing multiple symbols, 
	 * or a single symbol, or NIL.
	 */
	public LambdaProcedure(LispObject parameters, LispObject body, Environment environment) throws LispError {
		/* Construct parameter processor list or single param symbol */
		params = new ArrayList<Symbol>();
		if (parameters == NIL.instance || parameters.isCons()) {
			while(parameters.isCons()) {
				params.add(parameters.asCons().getCar().asSymbol());
				parameters = parameters.asCons().getCdr();
			}
		} else if (parameters.isSymbol()) {
			params.add(parameters.asSymbol());
		} else {
			throw new WrongTypeError("List, NIL or Symbol", parameters);
		}
		/* store body and environment */
		lambdaBody = body;
		closureEnvironment = environment;		
	}

	public LispObject execute(LispObject operands, Environment environment) throws LispError {
		/* extend closure environment */
		Environment closureExtension = closureEnvironment.getClosure();
		/* process arguments (bind variables in closure extension) */
		processArgs(operands, closureExtension, environment);
		/* evaluate body in closure extension (mark as tail if lambda body is a list) */
		if (lambdaBody.isCons()) lambdaBody.asCons().tail = true; 
		return Kernel.eval(lambdaBody, closureExtension);
	}
	
	/**
	 * Evaluate arguments in paramEvaluationEnv, bind in closureExtension
	 */
	private void processArgs(LispObject o, Environment closureExtension, Environment paramEvaluationEnv) throws LispError {
		for (Symbol param: params) {
			if (o == NIL.instance) throw new WrongNofArgsError(); 
			closureExtension.define(param, Kernel.eval(o.asCons().getCar(), paramEvaluationEnv));
			o = o.asCons().getCdr();
		}
		if (o != NIL.instance) throw new WrongNofArgsError();
	}

}
