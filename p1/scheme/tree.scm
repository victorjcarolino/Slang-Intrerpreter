;; tree: A binary tree, implemented as a "closure"
;;
;; The tree should support the following methods:
;; - 'ins x      - Insert the value x into the tree
;; - 'clear      - Reset the tree to empty
;; - 'inslist l  - Insert all the elements from list `l` into the tree
;; - 'display    - Use `display` to print the tree
;; - 'inorder f  - Traverse the tree using an in-order traversal, applying
;;                 function `f` to the value in each non-null position
;; - 'preorder f - Traverse the tree using a pre-order traversal, applying
;;                 function `f` to the value in each non-null position
;;
;; Note: every method should take two arguments (the method name and a
;; parameter).  If a method is defined as not using any parameters, you
;; should still require a parameter, but your code can ignore it.
;;
;; Note: You should implement the tree as a closure.  One of the simplest
;; examples of a closure that acts like an object is the following:
;;
; (define (make-my-ds)
;   (let ( (x '()) ) 
;        (lambda (msg arg)
;              (cond 
;                  ((eq? msg 'set) (set! x arg) 'ok)
;                  ((eq? msg 'get) x) 
;                  ((eq? msg 'ins) () )
;                  (else 'error)
;              )
;      )
;   )
;  )


;; In that example, I have intentionally *not* commented anything.  You will
;; need to figure out what is going on there.  If it helps, consider the
;; following sequence:
;;
;; (define ds (make-my-ds)) ; returns nothing
;; (ds 'get 'empty)         ; returns '()
;; (ds 'set 0)              ; returns 'ok
;; (ds 'get 'empty)         ; returns 0
;; (ds 'do 3)               ; returns 'error
;;
;; For full points, your implementation should be *clean*.  That is, the only
;; global symbol exported by this file should be the `make-bst` function.

;; Questions:
;;   - How do you feel about closures versus objects?  Why?
;;   - How do you feel about defining a tree node as a generic triple?
;;   - Contrast your experience solving this problem in Java, Python, and
;;     Scheme.

(define (make-bst)
  (letrec (  (x '())
          (insert (lambda (tree arg)
            ;implement 'ins helper method here
            (set! tree
              (cond
               ;base case = current node is null
                ((null? tree) (list arg '() '()))
               ;otherwise, if arg < current node, call 'ins on left subtree
                ((< arg (car tree)) 
                  (list 
                    (car tree) 
                    (insert (cadr tree) arg)
                    (cddr tree)
                  )
                )
               ;else (if arg >= current node) call 'ins on right subtree   
                (else 
                  (list 
                    (car tree) 
                    (cadr tree) 
                    (insert (caddr tree) arg)
                  ) 
                )
              )
            )
          ))

          (disp (lambda (tree)
            (display tree)
          ))

          (insert-list (lambda (tree list) 
            ;;implement 'inslist helper method here
            ((cond
                ;base case = arg (a list) is null
              ((null? arg) #f)
                ;otherwise call 'ins with the (car arg) and call 'inslist with the (cdr arg)
              (else (insert-list tree (car arg)) (insert-list tree (cdr arg)))
            ))
          ))

          (inord (lambda (tree func)
            ;implement 'inorder helper method here
            ((cond
              ;base case = current node is null
              ((null? (car x)) #f)
              ;otherwise
              (else 
                (inord (cadr x) func)
                (func (car x))
                (display (car x))
                (inord (cddr x) func))
              )
            ))
          )

          (preord(lambda (tree func)
            ;;implement 'preorder helper method here
            ((cond
              ;base case = current node is null
              ((null? (car x)) #f)
              (else
                (func (car x))
                (display (car x))
                (preord (cadr x) arg)
                (preord (cddr x) arg)
              )
            ))
          ))

        )
      (lambda (func arg)
        (cond
          ((eq? func 'ins)
            (insert x arg)
          )
           
          ((eq? func 'clear) 
            (set! x '())
          )

          ((eq? func 'inslist)
            (insert-list x arg)
          )

          ((eq? func 'display)
            (disp x)
          )

          ((eq? func 'inorder)
            (inord x func)
          )

          ((eq? func 'preorder)
            (preord x func)
          ) 
          (else 'error)
        )       
      )
  )
)
