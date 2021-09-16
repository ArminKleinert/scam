;; SC@M Predefined lambda and macro procedures
;; Version 0.1
;;

;;
;; Read-Eval-Print loop
;;

  (define repl (lambda ()
    (loop
      (print
        (catch
          (eval
            (read)))))))

;;
;; Program flow control
;;

  ; (if <test> <true-part> [false-part])
  (define if 
    (macro (@test @true-part @false-part) 
      (cond 
        (test true-part) 
        (else false-part))))

  ; (loop <expression>)
  (define loop 
    (macro (@exp) 
      (begin 
        (if (eqv? exp 'exit) 
          1 
          (loop exp)))))

  ; (do ( (<clause>*) (<test> <results>*) <body> ))
  ;   clause = (<variable> <init-value> <step>)
  (define do
    (macro ( @var-init-step @(@test . @results) . @body )
      (let ()
	    (init-do . var-init-step)			; initialize variables
        (begin
		  (loop								; loop:
	        (if test							;   if test = true
              'exit							;     exit loop
		      (begin							;   else
		        (begin . body)				;	  evaluate body
	            (step-do . var-init-step))))	;	  perform step
          (begin . results)))))			; evaluate results
			  
  (define init-do								; initialize var/init/step list
    (macro ( @(@var @init @step) . @rest)
      (define var init)
	  (if (null? 'rest) 
	    1
	    (init-do . rest))))
	  
  (define step-do 
    (macro ( @(@var @init @step) . @rest) 	; step var/init/step list
      (set! var step)
	  (if (null? 'rest) 
	    1
	    (step-do . rest))))

;;
;; List functions
;;

  ; (append <list>*)
  (define append 
    (macro (ls . @rest) 
      (cond
        ((null? ls) 
          (cond
            ((null? 'rest) nil)
            (else (append . rest))))
        (else (cons (car ls) (append (cdr ls) . rest))))))

  ; (memq <item> <list>)
  (define member 
    (lambda (item list)
      (if (null? list) 
        nil
        (if (eq? (car list) item)
		  list
		  (memq item (cdr list))))))

  ; (memv <item> <list>)
  (define member 
    (lambda (item list)
      (if (null? list) 
        nil
        (if (eqv? (car list) item)
		  list
		  (memv item (cdr list))))))

  ; (member <item> <list>)
  (define member 
    (lambda (item list)
      (if (null? list) 
        nil
        (if (equal? (car list) item)
		  list
		  (member item (cdr list))))))

  ; (list <element>*)
  (define list 
    (macro (h . @t) 
      (if (null? 't) 
        (cons h nil) 
        (cons h (list . t)))))

  ; (length <list>)
  (define length (macro ((@h . @t)) 
    (if (null? 't) 
      1 
      (+ 1 (length 't)))))

  ; (reverse <list>)
  (define reverse (macro ((@h . @t)) 
    (if (null? 't) 
      (cons 'h nil) 
      (cons 'h (reverse 't)))))

  ; (map <procedure> <list>)
  (define map 
    (lambda (f lst)
      (cond 
        ((null? lst) lst)
        (else 
          (cons 
            (apply f (car lst))
            (map f (cdr lst)))))))

  ; (assq <obj> <list>)
  (define assq
    (lambda (obj alist)
      (if (null? alist)
        nil
        (if (eq? (car (car alist)) obj)
          (car alist)
          (assq obj (cdr alist))))))

  ; (assv <obj> <list>)
  (define assv
    (lambda (obj alist)
      (if (null? alist)
        nil
        (if (eqv? (car (car alist)) obj)
          (car alist)
          (assv obj (cdr alist))))))

  ; (assoc <obj> <list>)
  (define assoc
    (lambda (obj alist)
      (if (null? alist)
        nil
        (if (equal? (car (car alist)) obj)
          (car alist)
          (assoc obj (cdr alist))))))

;;
;; car / cdr compositions
;;

  (define caar (lambda (list) (car (car list))))
  (define cadr (lambda (list) (car (cdr list))))
  (define cdar (lambda (list) (cdr (car list))))
  (define cddr (lambda (list) (cdr (cdr list))))
  (define caaar (lambda (list) (car (car (car list)))))
  (define caadr (lambda (list) (car (car (cdr list)))))
  (define cadar (lambda (list) (car (cdr (car list)))))
  (define caddr (lambda (list) (car (cdr (cdr list)))))
  (define cdaar (lambda (list) (cdr (car (car list)))))
  (define cdadr (lambda (list) (cdr (car (cdr list)))))
  (define cddar (lambda (list) (cdr (cdr (car list)))))
  (define cdddr (lambda (list) (cdr (cdr (cdr list))))) 

;;
;; Object type predicates
;;

  (define pair? (lambda (object) 
    (objeq? object (cons 0 nil))))
		
  (define number? (lambda (object) 
    (objeq? object 0)))
		
  (define symbol? (lambda (object) 
    (objeq? object symbol)))

  (define null? (lambda (object) 
    (objeq? object nil)))

;;
;; Logical and, or, not
;;

  (define and 
    (macro (@h . @t) 
      (cond
	    ((null? 't) h)
	    (h (and . t)))))
	  
  (define or 
    (macro (@h . @t) 
      (cond
	    ((null? 't) h)
	    (h 1)
	    (else (or . t)))))
	  
  (define not 
    (macro (exp) 
      (cond ((null? exp) 1))))

;;
;; Mathematical functions
;;

  (define <= 
    (lambda (x y) 
      (or 
	    (< x y) 
        (= x y))))

  (define >= 
    (lambda (x y) 
      (or 
	    (> x y) 
	    (= x y))))

  (define even?
    (lambda (n)
      (= 0 (modulo n 2))))
	
  (define odd?
	(lambda (n)
	  (= 1 (modulo n 2))))

  (define power 
    (lambda (n x) 
      (if (= 0 x) 
        1
        (* n (power n (- x 1))))))

  (define abs
    (lambda (n)
	  (if (< n 0) (- n) n)))

  (define lcm
    (lambda (n1 n2)
      (/ (* n1 n2) (gcd n1 n2))))

  (define gcd
    (lambda (n1 n2)
      (cond
        ((= n2 0) n1)
        (else (gcd n2 (modulo n1 n2))))))

  (define sqrt 
    (lambda (x) 
      (sqrt-iter 1 x)))

  (define sqrt-iter 
    (lambda (guess x)
      (if (good-enough-p guess x)
        guess
        (sqrt-iter (improve guess x) x))))

  (define improve 
    (lambda (guess x) 
      (* 0.5 (+ (/ x guess) guess))))

  (define good-enough-p
    (lambda (guess x)
      (< (abs (- (* guess guess) x)) 0.0001)))

;;
;; Force / delay
;;

  (define force								; force delay
    (lambda (object)
      (object)))

  (define delay								; create delay
    (macro (@expression)
      (make-promise (lambda () expression))))

  (define make-promise						; create promise closure
    (lambda (proc)
      (let 
        ((result-ready? nil)					; init: not ready, no result
         (result nil))
        (lambda ()							; create lambda closure:
          (if result-ready?					;   if result ready, return result
            result
            (let ((x (proc)))
              (if result-ready?				
                result
                (begin 
                  (set! result-ready? 1)
                  (set! result x)
                  result))))))))
