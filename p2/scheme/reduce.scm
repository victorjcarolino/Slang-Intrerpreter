;; reduce takes a binary function, a list, and an identity value, and computes
;; the result of repeatedly applying that function
;;
;; Example: (reduce + '(1 2 3) 0) ==> 6
;;
;; Example: (reduce * '() 1) ==> 1
(define (reduce op l identity)
    (letrec
        ((reduce-helper (lambda (op l identity partial)
            (cond
                ;base case, the list is empty
                ((null? l) 
                    ;return partial, no elements remaining on which to operate.
                    partial
                )
                (else
                    ;operate on the first element of list and partial, pass results as new partial
                    ; and cdr l as the new l into helper function
                    (reduce-helper op (cdr l) identity (op partial (car l)))
                )
            ))
        ))
        (reduce-helper op l identity identity)
    )
)