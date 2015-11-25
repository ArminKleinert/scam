
;;
;; English phrase generator
;;
;; Usage: (sentence)
;;

  (define sentence
    (lambda ()
      (append (noun-phrase) (verb-phrase))))

  (define noun-phrase
    (lambda ()
      (append (article) (noun))))

  (define verb-phrase
    (lambda ()
      (append (verb) (noun-phrase))))

  (define article
    (lambda ()
      (one-of '(the a))))

  (define noun
    (lambda ()
      (one-of '(cat dog man woman mouse chicken cow))))

  (define verb
    (lambda ()
      (one-of '(chased frightened killed loved ate))))

  (define one-of
    (lambda (lst)
      (nth lst (+ (random (length lst)) 1))))

  (define nth
    (lambda (ls n)
      (if (= n 1)
        (list (car ls))
        (nth (cdr ls) (- n 1)))))
