;; my_reverse: reverse a list without using the scheme `reverse` function
;;
;; Your implementation of this function can use special forms and standard
;; functions, such as `car`, `cdr`, `list`, `append`, and `if`, but it cannot
;; use the built-in `reverse` function.
;;
;; Your implementation should be recursive.

(define (my-reverse l)
  (cond 
    ((null? l) '()) ;base case is when the list is empty
    (else 
      (list ;if l is not null, return a list in which:
          (my-reverse(cdr l)) ;the first part is the reverse of (cdr l)
          (car l) ;followed by (car l)
      )
    )
  )
)