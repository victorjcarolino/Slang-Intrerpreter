;; list2vector takes a list and returns a vector, without using `list->vector`
;(define (list2vector vec) '())


(define list-length (lambda (list) 
    (if (null? list)
        0
        (+ 1 (list-length (cdr list)))
    )
))

(define l2v-helper (lambda (l counter vect)
    (begin
        (vector-set! vect counter (car l))
        (cond
            ((null? (cdr l))
                vect
            )
            (#t
                (l2v-helper (cdr l) (+ counter 1) vect)            
            )
        )
    )
))

(define list2vector (lambda (l)
    (if (null? l) 
        ;base case for l is null - return empty vector
        (make-vector 0)
        (l2v-helper l 0 (make-vector (list-length l)))
    )
))

;;Project 2 version:

;(define (list2vector l)
;    (if (null? l) 
;        ;base case for l is null - return empty vector
;        (make-vector 0)
;        (letrec 
;        (
;            ;vector we are adding elements to
;            (vect (make-vector (length l)))
;            ;helper method
;            (l2v-helper (lambda (l counter)  
;                (vector-set! vect counter (car l))
;                (cond
;                    ((null? (cdr l))
;                        vect
;                    )
;                    (else
;                        (l2v-helper (cdr l) (+ counter 1))            
;                    )
;                ))
;            )
;        )
;        (l2v-helper l 0)
;        )
;    )
;)