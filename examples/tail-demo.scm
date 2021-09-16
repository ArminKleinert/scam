
;; This file contains a demonstration of SC@M tail optimization
;; 
;; These functions contain either tail calls (resulting into infinite loops) or
;; non-tail calls (resulting into a stack overflow).
;;
;; Note that tail calls are always lists. Any other object that is to be evaluated
;; is never treated like a tail call.
;;

; An expression is considered to be a tail call if it's the body of a lambda.
; Result: infinite loop, no stack overflow

  (define lambda-body-tail
    (lambda () 
      (lambda-body-tail)))

; A call is considered to be a tail call if it's the last argument of a (begin ...) expression.
; When this function is evaluated, there are actually two tail calls performed; one for the body
; expression (see above) and one for the last argument of the (begin ...) expression.
; Result: infinite loop, no stack overflow

  (define begin-tail
    (lambda () 
      (begin 
        (print "Hello")
        (begin-tail))))

; Because a lambda body is turned into a (begin ...) expression, the following is a tail call too.
; Result: infinite loop, no stack overflow

  (define lambda-body-begin-tail
    (lambda () 
      (print "Hello") 
      (lambda-body-begin-tail)))

; However, a statement not at the end of a lambda body is no tail call.
; Result: Stack overflow

  (define lambda-no-tail-call
    (lambda () 
      (print "Hello") 
      (lambda-no-tail-call) 
      (print "This is never printed")))

; Result statements in a cond expression are considered to be tail calls too.
; Result: infinite loop, no stack overflow

  (define cond-tail
    (lambda ()
      (cond
        (nil 'a)
        (1   (cond-tail))
        (nil 'b))))

; Macro bodies are considered to be tail calls just like lambda's. "if" is a macro which translates
; into a cond expression. The same rules apply here: the true/false expressions of "if" are tail calls.
; Result: infinite loop, no stack overflow

  (define if-tail
    (lambda ()
      (if nil
        'false
        (if-tail))))

; Ofcourse, the above does not apply if the if or cond expression is not at the end
; Result: stack overflow

  (define if-no-tail
    (lambda ()
      (if nil
        'false
        (if-no-tail))
      "this is never evaluated"))

; The logical AND, OR macro procedures support tail optimization too.
; Result: infinite loop

  (define or-tail
    (lambda ()
      (or (or-tail))))

  (define and-tail
    (lambda ()
      (and (and-tail))))


