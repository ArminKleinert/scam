//
//  Scam.java
//  SC@M V 1
//

import java.util.*;
import java.io.*;

import Exceptions.LispError;
import LispObjects.LispObject;
import System.Kernel;
import System.Parser;

public class Scam {

    public static void main (String args[]) {

		//try {
		//	TypesTest.testAll();
		//	EnvironmentTest.testDefine();
		//} catch (LispError e) { System.out.println(e); }


		System.out.println("Welcome to SC@M");
		Kernel kernel = new Kernel();
		
		KernelThread kthread = new KernelThread(kernel);
		
		// Read, parse and evaluate built-in.scm
		InputStream ins = ClassLoader.getSystemResourceAsStream("built-in.scm");
		Parser p = new Parser(new InputStreamReader(ins), false);
		LispObject o;
		try {
			while ((o = p.parseObject()) != null) {
				kernel.eval(o);
			}
		} catch (LispError e) {
			System.out.println("Error while processing built-in.scm: " + e);
		}
		
		// Start REPL
		kthread.run();
	}
}


// execute repl in thread, because command line stack size settings do not affect the 
// main thread, only other threads.

class KernelThread extends Thread {
	Kernel k;
	
	KernelThread(Kernel k) {
		this.k = k;
	}
	
	public void run() {
		try {
			k.execute("(repl)");
		} catch (LispError e) {
			System.out.println("Uncaught SC@M error: " + e);
		}
	}
}
		