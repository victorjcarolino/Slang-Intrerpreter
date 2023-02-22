;; This is an alternate version of l2v.scm, using a more functional programming style implementation.
;; The only side effects are the from initial define and the (vector-set!) function.

;; Instead of defining recursive helper functions, 
;; the helper functions are lambdas evaluated using Y combinators


(define list2vector (lambda (l)
    (if (null? l) 
        ;base case for l is null - return empty vector
        (make-vector 0)
        
     (
      (
        ;;Y combinator for a function that takes 3 arguments
        (lambda (r)
          ((lambda (f) (f f))
            (lambda (f) 
             (r (lambda (arg1 arg2 arg3) 
                 ((f f) arg1 arg2 arg3) )) 
          ))
        )
        
        ;;value for r: the pseudo-recursive function. Y takes this as input
        (lambda (l2v-help)
         (lambda (list index vect)
          (begin
           (vector-set! vect index (car list))
           (cond
               ((null? (cdr list))
                   vect
               )
               (#t
                   (l2v-help (cdr list) (+ index 1) vect)          
               )
           )
          )
         )
        )

      )   

       ;;The output from the Y combinator is evaluated with these arguments.
       ;;The third argument also uses a Y combinator
       l 0 (make-vector
            ((
            ;;Y combinator for a function that takes 1 argument
            (lambda (r)
              ((lambda (f) (f f))
                (lambda (f) 
                (r (lambda (arg) 
                    ((f f) arg) )) 
              ))
            )
            ;;non-recursive list-length function to be input for Y
            (lambda (list-length)
             (lambda (list)
                (if (null? list) 0 (+ 1 (list-length (cdr list))) )
             ))
            ) 
            ;;plug in the list, l
            l )
           )
      )
    )
))





;; Y combinator testing
;
;(define lazy-Y
;    (lambda (f) 
;        (f (Y f))
;    ))
;
;(define strict-Y
;    (lambda (f)
;        (f (lambda (x) ((strict-Y f) x)) )))
;
;(define dummy (lambda (double-dist-from-0)
;    (lambda (n)
;        (if (= n 0)
;            0
;            (+ 2 (double-dist-from-0 (- n 1)))
;        )
;    )
;))