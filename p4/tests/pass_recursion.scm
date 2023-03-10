(define recurse (lambda (a b c val)
  (cond
   ((and a b c) val)
   ((and   b c) (recurse #t #f #f 4) val)
   ((and a   c) (recurse #t #t #f 6) val)
   ((and a b  ) (recurse #t #t #t 7) val)
   ((and a    ) (recurse #t #f #t 5) val)
   ((and   b  ) (recurse #f #t #t 3) val)
   ((and     c) (recurse #f #t #f 2) val)
   (#t          (recurse #f #f #t 1) val)
   )))
(recurse #f #f #f 17)
