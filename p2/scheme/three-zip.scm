;; three-zip takes three lists, and returns a single list, where each entry in
;; the returned list is a list with three elements.
;;
;; The nth element of the returned list is a list containing the nth element of
;; the first list, the nth element of the second list, and the nth element of
;; the third list.
;;  
;; Your implementation should be tail recursive.
;;
;; If the three lists do not have the same length, then your code should behave
;; as if all of the lists were as long as the longest list, by replicating the
;; last element of each of the short lists.
;;
;; Example: (three-zip '(1 2 3) '("hi" "bye" "hello") '(a b c))
;;          -> ('(1 "hi" a) '(2 "bye" b) '(3 "hello" c))
;;
;; Example: (three-zip '(1 2 3 4) '("hi" "bye" "hello") '(a b c))
;;          -> ('(1 "hi" a) '(2 "bye" b) '(3 "hello" c) '(4 "hello" c))
(define (three-zip l1 l2 l3)
    ;base case #1: if any of the lists are empty, return an empty list
    (if (or (null? l1) (null? l2) (null? l3))
        '()
    )
    (letrec
        ((tz-helper ;define recursion helper method
            (lambda (front l1 l2 l3)
            (cond
            ;base case #2: if each list's length is 1, return a list containing each element
            ((and (null? (cdr l1)) (null? (cdr l2)) (null? (cdr l3)))
                (append front (list (list (car l1) (car l2) (car l3))))
            )
            (else
                ;if l1 only has one element (cdr is null), reassign the list to have a cdr equal to the car, otherwise don't change it
                (if (null? (cdr l1)) 
                    (set! l1 (list (car l1) (car l1)))
                    #f
                )
                ;same process for l2 and l3
                (if (null? (cdr l2))
                    (set! l2 (list (car l2) (car l2)))
                    #f
                )
                (if (null? (cdr l3))
                    (set! l3 (list (car l3) (car l3)))
                    #f
                )
                (if (null? front)
                    ;calling (list) twice for formatting - because append will add to first list, not pair objects
                    (tz-helper (list (list (car l1) (car l2) (car l3))) (cdr l1) (cdr l2) (cdr l3))
                    (tz-helper (append front (list (list (car l1) (car l2) (car l3)))) (cdr l1) (cdr l2) (cdr l3))
                )
            ))
            )))
        (tz-helper '() l1 l2 l3)
    )
)