
# What is SC@M?

SC@M is an interpreter written in Java for a language based on Scheme. SC@M does not pretend to comply to 
the official Scheme standard. It misses some key features such as continuations and a compatible 
macro system. Also, some things are done entirely different, like error handling and macro's. 

The name SC@M is derived from Scheme, the @ symbol comes from @ prefix used in SC@M’s macro system.

SC@M is kind of useful as an example on how a Scheme/LISP like language could be implemented in Java.

Some similarities with Scheme

- The nature of the language: linked lists, eval/apply, dynamic typing etc. etc.
- First-class procedures
- Lambda enclosures
- Tail call optimization
- Data types: integer, rational, real, symbol, string, cons, nil
- Global scoping (define) or lexical scoping (let)

Some differences

- No booleans, just NIL for false and anything but NIL for true (like LISP, but the T symbol has no special meaning)
- No complex numbers, vectors or arrays
- No continuations
- Only explicit (define function-name (lambda ...)), no support for the shorter (define (function-name parameters...))
- No quasi quoting
- A different macro system
- A different error system

# Getting started

The example directory contains example code for SC@M:

- **Phrase-generator.scm** - A simple english phrase generator
- **Sort.scm** - Quicksort, Mergesort and Bubblesort
- **Math.scm** - Prime generator, prime tester and perfect number tester
- **Tail-demo.scm** - Demonstration of tail call optimization
- **Regr.scm** - Regression tests used during development
- **Built-in.scm** - Definitions of built-in lambda/macro functions

The SC@M interpreter is compiled into a jar file, it must be started as follows:

> Java –jar Scam.jar

Assuming sc@m is started in the original directory, you can load an example like this:

> (load “examples/filename.scm”) [enter]

SC@M is exited with

> ’exit [enter]

# Tail call optimization	

SC@M performs tail call optimization. This is extremely useful because in Scheme like languages, you often 
perform iteration trough recursion. Tail call optimization is handled internally by marking forms when they 
can be considered to be tail calls. When such a marked form is evaluated in Kernel.eval, it doesn't return 
the evaluation as it normally does, but it returns a Tail object. A Tail object holds the expression and a 
reference to the environment in which it should be evaluated. Returning a Tail object gives the possiblility 
to perform the actual evaluation at a different place than where it would have been evaluated otherwise.

Every execution of a procedure takes place in Kernel.apply. Normally, this method returns the result of the 
execution of the procedure. But when the execution returns a Tail object, it executes its stored expression 
and returns the value, or executes again if the the execution of the Tail object returned another Tail object. 
The essence of this is that the tail call expression is evaluated outside of the method context in which it
was created, so that there is no stack growth when a tail call is performed.

Since testing for the result of an execution and consequent Tail objects is done in a while loop, it's easy 
to observe that tail recursion is performed as if it is an imperative loop.

See the Tail-demo.scm example file for a demonstration of tail call optimization in SC@M.

# Implemented Scheme functions

The functions listed here are based on the official scheme standard (R5RS). Note that it is possible that some 
functions may not fully comply to the official standard, but most of them will. There are two different types 
of implemented functions. Internal functions are defined in Java, as Procedure objects. They cannot be expressed 
in terms of SC@M code. Pre-defined functions are defined as lambda functions in terms of other implemented 
functions. The pre-defined functions are defined in the file built-in.scm, which resides inside Scam.jar. When 
the interpreter is started, this file is read and evaluated before the interpreter shows the prompt. A copy of 
built-in.scm file can also be found in the examples directory.

**Internal:** Car cdr read print quote define set! Lambda cond eq? eqv? Equal? Begin cons load modulo quotient + - * / > <						
**Pre-defined:** If loop append memq memv member list length reverse map Assq assv assoc pair? Number? Symbol? Null? And or 
not <= >= even? Odd? power abs lcm gcd sqrt force delay									

# Other functions

The functions listed here do not appear in the Scheme standard. They are specific to SC@M.

> (catch <expression>)
	
Evaluate <expression> and return its value. When an error is thrown within <expression>, it is caught, converted to a 
string object and returned instead of the evaluation of <expression>.

> (throw <string>)

Throw an error with a message described in <string>. If the error is not caught, it will make the kernel exit with an 
“Uncaught error” message.

> (macro <param-list> <body>)

Create a macro procedure. This works just like a lambda definition, but parameters in <param-list> can be preceded by 
a @ character, indicating the argument should not be evaluated and bound, but should replace each occurrence of the 
parameter symbol in the macro body. Unlike a lambda procedure, a macro does not hold a reference to the environment 
in which it is created, so it is not an enclosure. It also supports nested lists as parameters. See the files in the 
example directory for examples.

> (objeq? <object1> <object2>)

Checks if two objects are of the same type.

> (repl)
	
Starts a print-eval-read loop with error catching.

