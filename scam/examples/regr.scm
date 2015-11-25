;;
;; Regression test
;;
;; Usage: (test <list>)
;;


(define test 
	(macro ( (@app @ans . @rest) ) 
		(if (equal? (catch app) ans) 
			(if (null? 'rest) 
				1 	
				(test 'rest)) 
			'(failed app ans))))

;; arithmetic tests
(define arithmetic-tests '(

	(define a 1)
	(define b 1/2)
	(define c 2.0)

	(+)	0
	(+ 1)	1
	(+ a a a)	3	(+ a a b)	5/2	(+ a a c)	4.0	
	(+ a b a)	5/2	(+ a b b)	2	(+ a b c)	3.5	
	(+ a c a)	4.0	(+ a c b)	3.5	(+ a c c)	5.0	
	(+ b a a)	5/2	(+ b a b)	2	(+ b a c)	3.5	
	(+ b b a)	2	(+ b b b)	3/2	(+ b b c)	3.0	
	(+ b c a)	3.5	(+ b c b)	3.0	(+ b c c)	4.5	
	(+ c a a)	4.0	(+ c a b)	3.5	(+ c a c)	5.0	
	(+ c b a)	3.5	(+ c b b)	3.0	(+ c b c)	4.5	
	(+ c c a)	5.0	(+ c c b)	4.5	(+ c c c)	6.0	

	(-)	"Wrong number of arguments"
	(- 1)	-1
	(- a a a)	-1	(- a a b)	-1/2	(- a a c)	-2.0	
	(- a b a)	-1/2	(- a b b)	0	(- a b c)	-1.5	
	(- a c a)	-2.0	(- a c b)	-1.5	(- a c c)	-3.0	
	(- b a a)	-3/2	(- b a b)	-1	(- b a c)	-2.5	
	(- b b a)	-1	(- b b b)	-1/2	(- b b c)	-2.0	
	(- b c a)	-2.5	(- b c b)	-2.0	(- b c c)	-3.5	
	(- c a a)	0.0	(- c a b)	0.5	(- c a c)	-1.0	
	(- c b a)	0.5	(- c b b)	1.0	(- c b c)	-0.5	
	(- c c a)	-1.0	(- c c b)	-0.5	(- c c c)	-2.0	

	(*)	1
	(* 1)	1
	(* a a a)	1	(* a a b)	1/2	(* a a c)	2.0	
	(* a b a)	1/2	(* a b b)	1/4	(* a b c)	1.0	
	(* a c a)	2.0	(* a c b)	1.0	(* a c c)	4.0	
	(* b a a)	1/2	(* b a b)	1/4	(* b a c)	1.0	
	(* b b a)	1/4	(* b b b)	1/8	(* b b c)	0.5	
	(* b c a)	1.0	(* b c b)	0.5	(* b c c)	2.0	
	(* c a a)	2.0	(* c a b)	1.0	(* c a c)	4.0	
	(* c b a)	1.0	(* c b b)	0.5	(* c b c)	2.0	
	(* c c a)	4.0	(* c c b)	2.0	(* c c c)	8.0	

	(/)	"Wrong number of arguments"
	(/ 2)	1/2
	(/ a a a)	1	(/ a a b)	2	(/ a a c)	0.5	
	(/ a b a)	2	(/ a b b)	4	(/ a b c)	1.0	
	(/ a c a)	0.5	(/ a c b)	1.0	(/ a c c)	0.25	
	(/ b a a)	1/2	(/ b a b)	1	(/ b a c)	0.25	
	(/ b b a)	1	(/ b b b)	2	(/ b b c)	0.5	
	(/ b c a)	0.25	(/ b c b)	0.5	(/ b c c)	0.125	
	(/ c a a)	2.0	(/ c a b)	4.0	(/ c a c)	1.0	
	(/ c b a)	4.0	(/ c b b)	8.0	(/ c b c)	2.0	
	(/ c c a)	1.0	(/ c c b)	2.0	(/ c c c)	0.5	

	; =

	(=)					"Wrong number of arguments"
	(= 1)					1
	(= 1 1)					1
	(= 1 0)					nil
	(= 1 1 0)				nil
	(= 0 1 1)				nil

	; <

	(<)					"Wrong number of arguments"
	(< 1)					1
	(< 1 1)					nil
	(< 1 2)					2
	(< 1 2 1)				nil
	(< 1 2 3)				3
	(< 3 2 1)				nil

	; >

	(>)					"Wrong number of arguments"
	(> 1)					1
	(> 1 1)					nil
	(> 2 1)					1
	(> 2 1 2)				nil
	(> 3 2 1)				1

	; modulo

	(modulo)				"Wrong number of arguments"
	(modulo 1)				"Wrong number of arguments"
	(modulo 1 1)				0
	(modulo 3 2)				1
	(modulo 13 5)				3
	(modulo 13 5 2)				1
	(modulo 37 13 5)			1

	; quotient

	(quotient)				"Wrong number of arguments"
	(quotient 1)				"Wrong number of arguments"
	(quotient 1 1)				1
	(quotient 3 2)				1
	(quotient 13 5)				2
	(quotient 13 5 2)			1
	(quotient 4095 32 16)			7
	
))
		
;; LibSystem tests

(define libsystem-tests '(

	; quote

	(quote)					"Wrong number of arguments"
	(quote 1 2)				"Wrong number of arguments"
	(quote 0)				0
	(quote a)				'a
	(quote (1 2 3))				'(1 2 3)
	(quote "test")				"test"
	(quote ())				()

	; catch	

	(catch)					"Wrong number of arguments"
	(catch 1 2)				"Wrong number of arguments"
	(catch (-))				"Wrong number of arguments"
	(catch (+ * - /))			"Wrong type: found #procedure, expected Number"
	(catch (+ 1 2 3))			6
	(catch 0)				0

	; throw

	(throw)					"Wrong number of arguments"
	(throw "yada" "yada")			"Wrong number of arguments"
	(throw 100)				"Wrong type: found 100, expected String"
	(catch (throw "T3St 3rr0R"))		"T3St 3rr0R"

	; define

	(define)				"Wrong number of arguments"
	(define a)				"Wrong number of arguments"
	(define 1 2)				"Wrong type: found 1, expected Symbol"
	(define a 1 2)				"Wrong number of arguments"
	(define test-a 100)			100
	test-a					100
	(define test-b test-a)			100
	test-b					100
	(define test-c 'test-b)			'test-b
	test-c					'test-b
	(let () test-a)				100 ; access to global variable in closure
	(let ((test-a 200)) test-a)		200 ; closure hides global variable

	; set!

	(set!)					"Wrong number of arguments"
	(set! x)				"Wrong number of arguments"
	(set! x 1 2)				"Wrong number of arguments"
	(set! 1 2)				"Wrong type: found 1, expected Symbol"
	(set! test-d 100)			"Unbound variable: test-d"
	(define test-d 0)			0
	(set! test-d 100)			100
	test-d					100

	; let
	(let)					"Wrong number of arguments"
	(let 100)				"Wrong number of arguments"
	(let ())				"Wrong number of arguments"
	(let () 100)				100
	(let () 100 200)			200
	(let () test-e 100)			"Unbound variable: test-e"
	(let ((test-e 100)) test-e)		100
	test-e					"Unbound variable: test-e"
	(define test-f 100)			100
	(let () test-f)				100
	(let ((test-f 200)) test-f)		200
	(let ((test-f (+ 1 2))
		(test-ff (+ 3 4)))
			(+ test-f test-ff))	10
	test-f					100

	; lambda

	(lambda)				"Wrong number of arguments"
	(lambda 100)				"Wrong number of arguments"
	(lambda 100 200)			"Wrong type: found 100, expected List, NIL or Symbol"
	((lambda () 2))				2
	((lambda (x) 2))			"Wrong number of arguments"
	((lambda (x) x) 2)			2
	((lambda (x y) (+ x y)) 1 2)		3
	(((lambda () (lambda () 2))))		2
	(define test-g 
		(lambda (x y z) (+ x y)))	test-g
	(test-g 5 6 7)				11
	(define test-h 
		(let ((c 0)) 
		(lambda () 
			(set! c (+ c 1)))))	test-h
	(test-h)				1
	(test-h)				2
	c					"Unbound variable: c"

	; macro

	; ***

	; cond

	(cond)					"Wrong number of arguments"
	(cond (100))				"Wrong number of arguments"
	(cond (100 100 100))			"Wrong number of arguments"
	(cond 100)				"Wrong type: found 100, expected Cons"
	(cond (1 100))				100
	(cond (nil 100) (nil 200))		nil
	(cond (nil 100) (1 200))		200
	(cond (1 (+ 1 2)) (undef undef))	3 ; undef = undefined variable, should not be evaluated
	(cond (nil undef) (1 (+ 1 2)))		3
	
	; eqv?

	(eqv?)					"Wrong number of arguments"
	(eqv? 1 2 3)				"Wrong number of arguments"
	(eqv? 1 1)				1
	(eqv? 1 2)				nil
	(eqv? 'a 'a)				1
	(eqv? 'a 'b)				nil
	(eqv? () nil)				1
	(eqv? () ())				1
	(eqv? '(1 2 3) '(1 2 3))		nil
	(define test-i '(1 2 3))		'(1 2 3)
	(eqv? test-i test-i)			1
	(eqv? nil nil)				1

	; equal?

	(equal?)				"Wrong number of arguments"
	(equal? 1 2 3)				"Wrong number of arguments"
	(equal? 1 1)				1
	(equal? 1 2)				nil
	(equal? 'a 'a)				1
	(equal? 'a 'b)				nil
	(equal? () nil)				1
	(equal? () ())				1
	(equal? '(1 2 3) '(1 2 3))		1
	(equal? '(1 (a b) 2) '(1 (a b) 2))	1
	(define test-j '(1 2 3))		'(1 2 3)
	(equal? test-j test-j)			1


	; objeq?

	(objeq?)				"Wrong number of arguments"
	(objeq? 1)				"Wrong number of arguments"
	(objeq? 1 2)				1
	(objeq? 1 'a)				nil
	(objeq? 1 "bla")			nil
	(objeq? 1 '(1 . 2))			nil
	(objeq? 'a 'b)				1
	(objeq? 'a "bla")			nil
	(objeq? 'a '(1 . 2))			nil
	(objeq? "bla" "yada")			1
	(objeq? "bla" '(1 . 2))			nil
	(objeq? '(1 . 2) '(1 2 3))		1

	; begin

	(begin)					"Wrong number of arguments"
	(begin 1)				1
	(begin 1 2)				2
	(begin 1 2 (+ 1 2))			3

	; cons

	(cons)					"Wrong number of arguments"
	(cons 1)				"Wrong number of arguments"
	(cons 1 2 3)				"Wrong number of arguments"
	(cons 1 2)				'(1 . 2)
	(cons '(1 2 3) 4)			'((1 2 3) . 4)
	(cons 1 '(1 2 3))			'(1 1 2 3)
	(cons (+ 1 2) '(1 2 3))			'(3 1 2 3)
	(cons 1 nil)				'(1)
	(cons '(1 2) '(3 4))			'((1 2) 3 4)

	; not tested:

	; load
	; print
	; read
	
))




	