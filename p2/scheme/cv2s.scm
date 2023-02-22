;; charvec2string takes a vector of characters and returns a string
(define (charvec2string cv) ""
    (if (null? cv)
        (make-string 0)
        (letrec
        (
            ;string we are adding elements to
            (str (make-string (vector-length cv)))
            ;helper method
            (cv2s-helper (lambda (cv counter)
                (cond 
                    ((= counter (vector-length cv))
                        str)
                    (else
                        (string-set! str counter (vector-ref cv counter))
                        (cv2s-helper cv (+ 1 counter))
                    )
                )
            )
            )
        )
        (cv2s-helper cv 0)
        )
    )
)