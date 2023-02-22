;; Compute the exclusive or of two values, using only and, or, and not
;;
;; xor should always return a boolean value
(define (xor a b) 
    (or (and a (not b)) (and b (not a)))  ; True if A is true and B is false OR B is true and A is false
)