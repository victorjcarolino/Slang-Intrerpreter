;; simple-hash creates a basic hash *set* of strings.  It uses the "method
;; receiver" style that you have seen previously: `make-hash` returns a function
;; that closes over some state; that function takes two arguments (a symbol and
;; a string).
;;
;; The argument to make-hash is a number: it should be positive.  It will be the
;; size of the "bucket vector" for your hash table.
;;
;; Your hash table should be a vector of lists.  Three operations can be
;; requested:
;; - 'contains string - Returns true if the string is in the hash set, false
;;   otherwise
;; - 'insert string - Returns true if the string was inserted into the hash set,
;;   false if it was alread there.
;; - 'remove string - Returns true if the string was removed from the hash set,
;;   false if it was not present to begin with.
;;
;; Here's an example execution:
;; (define my-hash (make-hash 32))
;; (my-hash 'insert "hello") <-- returns true
;; (my-hash 'contains "world") <-- returns false
;; (my-hash 'contains "hello") <-- returns true
;; (my-hash 'insert "hello") <-- returns false
;; (my-hash 'remove "world") <-- returns false
;; (my-hash 'remove "hello") <-- returns true
;; (my-hash 'remove "hello") <-- returns false
;; (my-hash 'contains "hello") <-- returns false
;;
;; To "hash" input strings, you should use the (very simple) djb2 function from
;; <http://www.cse.yorku.ca/~oz/hash.html>


;; TODO: implement this function
(define (make-hash size)
 (let*
    (
    (table (make-vector size '()))    ;initializes table to be a vector of empty lists
    ;(num-elements 0)
    ;(load-factor 0.75)
    (hash 
     (lambda (arg)
        (define hash-helper
            (lambda (str hash-val)
                (if (eq? (string-length str) 0)
                    hash-val ;string has been completely interated through, return hash-val
                    (let*
                        ((c (string-ref str 0) )
                            (new-hash-val (+ (* hash-val 33) (char->integer c)) )
                            (str-tail (substring str 1 (string-length str)) )
                        )
                        ;change hash-val based on the char at the head of str, then recurse
                        (hash-helper str-tail new-hash-val) )
                ) ) )
        (hash-helper arg 5381) ;5381 as specified by djb2 hash algorithm
     )
    )

    (contains 
     (lambda (arg)
        (let*
            ((index (remainder (hash arg) (vector-length table)))
             (possible-list (vector-ref table index))
            )
            (define contains-helper
                (lambda (l arg)
                    (if (null? l) 
                        #f  ;the potential list is empty, arg is not in the table
                        (if (equal? (car l) arg) 
                            #t  ;fist element is at the start of tte potential list, arg is in the table
                            (contains-helper (cdr l) arg)   ;check the next element of the potential list
                        ) 
                    ) ) )
            (contains-helper possible-list arg)
        )
     )
    )

    (insert 
     (lambda (arg)
        (if (contains arg) #f
            (let*
                ((index (remainder (hash arg) (vector-length table)) )
                 (list-to-insert (vector-ref table index))
                )
                
                (set! list-to-insert (cons arg list-to-insert))
                (vector-set! table index list-to-insert)
                ;(+ num-elements 1)
                ;(if (> (/ num-elements (vector-length table)) load-factor)
                    ;rehash here
                    ;#f)
                #t
            )
        )
     )
    )

    (remove
     (lambda (arg)
        (if (contains arg) 
            (let*
                ((new-list '())
                 (index (remainder (hash arg) (vector-length table)) )
                 (old-list (vector-ref table index))
                )
                (define remove-copier (lambda (new old element)
                    (if (null? old)
                        new
                        (if (equal? element (car old))
                            (if (null? (cdr old)) 
                                new
                                (remove-copier (cons (cadr old) new) (cddr old) element)
                            )
                            (remove-copier (cons (car old) new) (cdr old) element)
                        )
                    )
                 ))
                (vector-set! table index (remove-copier new-list old-list arg))
                ;(set! num-elements (- num-elements 1))     ;would be used for rehashing
                #t  ;return true on successfull remove
            )
            #f  ;if arg isn't in the table, nothing to remove. return #f
        )
     )
    )

    )

    (lambda (func arg)
        (cond
            ((eq? func 'insert) (insert arg))
            ((eq? func 'contains) (contains arg))
            ((eq? func 'remove) (remove arg))
            ;((eq? func 'print) table)
            (else 'error)
        )
    )
 )
)