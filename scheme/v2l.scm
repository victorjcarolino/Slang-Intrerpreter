;; vector2list takes a vector and returns a list, without using `vector->list`
(define (vector2list vec)
    (letrec
        ((v2l-helper 
         (lambda (l vec counter)
            (cond
                ((= counter (vector-length vec))
                    l
                )
                (else
                    (v2l-helper (append l (list(vector-ref vec counter))) vec (+ counter 1))
                    ;If replaced with the below line, the function works in linear time, but creates nested lists, not a single list
                    ;(v2l-helper (cons l (list(vector-ref vec counter))) vec (+ counter 1))
                )
            )
         )
        ))
        (v2l-helper '() vec 0)
    )
)
