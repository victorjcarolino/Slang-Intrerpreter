;; read_list: Use the `read` function to read from the keyboard and put the
;; results into a list.  The code should keep reading until EOF (control-d) is
;; input by the user.  It should use recursion, not iterative constructs.
;;
;; The order of elements in the list returned by (read-list) should the reverse
;; of the order in which they were entered.
;;
;; You should *not* define any other functions in the global namespace.  You may
;; need a helper function, but if you do, you should define it so that it is
;; local to `read-list`.
;; [CSE 262] Implement Me!
(define (read-list)
  (let ((input (read))) ;set input equal to (read), which reads input from the user
    (cond 
      ((eof-object? input) '() ) ;if input is EOF, return an empty list
      (else (list (read-list) input)) ;otherwise, return a list comprised of the next input and the current input
    )                                   ;(read-list) before input in order because need to store input in reverse order
  )
)
