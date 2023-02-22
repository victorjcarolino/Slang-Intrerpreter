;; my_map: apply a function to every element in a list, and return a list
;; that holds the results.
;;
;; Your implementation of this function is not allowed to use the built-in
;; `map` function.

(define (my-map func l)
  (cond ((null? l) '()) ;base case: l is null
    (else 
      (list                    ;return a list comprised of:
        (func (car l))         ;the output of func when the car of l is passed
        (my-map func (cdr l))) ;the output of my-map when the cdr of l is passed as the list
    )
  )
)

;testing
;(define (add-one x)
;  (+ x 1)
;)
;
;(define incre (lambda (x) (+ x 1)))
;
;(define (now-two x) 2)
;
;(define test '("hello" "world" "scheme" incre))
;
;(display (my-map now-two test))
