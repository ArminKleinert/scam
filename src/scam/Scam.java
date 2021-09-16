package scam;

import java.io.*;

import scam.exceptions.LispError;
import scam.lisp_objects.LispObject;
import scam.system.Kernel;
import scam.system.Parser;

public class Scam {

    public static void main (String[] args) {

		System.out.println("Welcome to SC@M");
		Kernel kernel = new Kernel();
		
		KernelThread kthread = new KernelThread(kernel);
		
		// Read, parse and evaluate built-in.scm
		InputStream ins = ClassLoader.getSystemResourceAsStream("built-in.scm");
		assert ins != null;
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
		kthread.start();
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
		