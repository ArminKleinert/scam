;;
;; Merge sort
;;
;; This is the preferred sort method for Scheme like languages, because
;; it's friendly to environments where elements are accessed sequentially,
;; as is the case with Scheme lists.
;;

  (define mergesort
    (lambda (x)
      (cond 
        ((null? (cdr x)) x)
        (else
          (merge
            (mergesort 
              (get-elements x 0 (quotient (length x) 2) 1))
            (mergesort 
              (get-elements x (quotient (length x) 2) (length x) 1)))))))

  (define merge
    (lambda (a b)
      (cond
        ((null? a) b)
        ((null? b) a)
        ((< (car a) (car b)) (cons (car a) (merge (cdr a) b)))
        (else (cons (car b) (merge a (cdr b)))))))

  (define get-elements
    (lambda (lst start end c)
      (cond
        ((= c end) (cons (car lst) nil))
        ((> c start) 
          (cons (car lst) (get-elements (cdr lst) start end (+ c 1))))
        (else (get-elements (cdr lst) start end (+ c 1))))))

;;
;; Quick sort
;;
;; Note: This implementation is not efficient because (sub)lists are 
;; traversed twice: once for getting elements larger than p, once for
;; elements smaller than p. This sort method would be complex to 
;; implement in an efficient way.
;;

  (define quicksort 
    (lambda (lst)
      (cond
        ((null? lst) nil)
        (else 
          (append 
           (quicksort (get <= (car lst) (cdr lst)))
            (list (car lst))
            (quicksort (get > (car lst) (cdr lst))))))))

  (define get
    (lambda (op p lst)
      (cond
        ((null? lst) nil)
        ((op (car lst) p) 
          (cons (car lst) (get op p (cdr lst))))
        (else
          (get op p (cdr lst))))))

;;
;; Bubble sort
;;

  (define bubblesort
    (lambda (lst)
      (bubble lst (- (length lst) 1))))

  (define bubble
    (lambda (lst n)
       (cond
         ((= 0 n) lst)
         (else (bubble (bubble-pass lst n) (- n 1))))))

  (define bubble-pass
    (lambda (lst n)
      (cond
        ((or (= 0 n) (null? (cdr lst))) 
           lst)
        ((> (car lst) (cadr lst)) 
          (cons 
            (cadr lst) 
            (bubble-pass (cons (car lst) (cddr lst)) (- n 1))))
        (else 
          (cons (car lst) (bubble-pass (cdr lst) (- n 1)))))))

