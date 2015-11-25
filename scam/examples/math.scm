;; SC@M math functions demo
;; (C)2005 Tjitze Rienstra

;;
;; perfect? - Test if n is a perfect number
;;

(define perfect?
  (lambda (n)
    (define add-divisors 
      (lambda (n c)
        (cond
          ((= c 1) 1)
          ((= (modulo n c) 0) 
            (+ c (add-divisors n (- c 1))))
          (else
            (add-divisors n (- c 1))))))
    (= (add-divisors n (quotient n 2)) n)))

;;
;; Fermat's little theorem (primality test)
;;

  ; Returns 1 if p = prime, nil if not. Use C to define certainty.
  (define is-probably-prime?
    (lambda (p c)
      (cond
        ((= 0 c) 1)
        ((fermat-test (random p) p) (is-probably-prime? p (- c 1))))))
 
  ; Fermat's little theorem: should return 1 for any a if p is prime
  (define fermat-test
    (lambda (a p)
      (= (power-modulo a p p 1) (modulo a p))))

  ; raise to the power with modulus, because SC@M doesn't handle big enough integers for fermat with regular power function
  (define power-modulo
    (lambda (n x mod acc)
      (cond
        ((= x 0) acc)
        (else (power-modulo n (- x 1) mod (modulo (* acc n) mod))))))

;;
;; Generate prime numbers - Sieve of Erastosthenes algorithm
;;

  ; Generate prime numbers until n
  (define generate-primes
    (lambda (n)
      (sieve (create-list 2 n))))

  ; Create a list of numbers between start and end
  (define create-list
    (lambda (start end)
      (if (> start end)
        ()
        (cons start (create-list (+ start 1) end)))))

  ; Eliminate multiples of n from list
  (define eliminate-multiples
    (lambda (n list)
      (if (null? list) 
        ()
        (if (= 0 (modulo (car list) n))
          (eliminate-multiples n (cdr list))
          (cons (car list) (eliminate-multiples n (cdr list)))))))

  ; Sieve
  (define sieve
    (lambda (list)
      (if (null? list)
        nil
        (cons
          (car list)
          (sieve (eliminate-multiples (car list) (cdr list)))))))

