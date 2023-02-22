;; prime_divisors: compute the prime factorization of a number
;;
;; To test this function, open a new `gsi` instance and then type:
;;  (load "prime_divisors.scm")
;; Then you can issue commands such as:
;;  (prime-divisors 60)
;; And you should see results of the form:
;;  (2 2 3 5)

;; This is a skeleton for the prime-divisors function.  For now, it just
;; returns #f (false)
;;
;; Note that you will almost certainly want to write some helper functions,
;; and also that this will probably need to be a recursive function.  You are
;; not required to use good information hiding.  That is, you may `define`
;; other functions in the global namespace and use them from
;; `prime-divisors`.

(define (factor? divisor dividend) ;checks if divisor is a factor of dividend
    (= (modulo dividend divisor) 0)  
)

(define (test-factor i n) ;tests if a specific i is a factor of n
  (cond
    ;base case #1: if i > n/2 return 0. n has no factors >= i
    ((> i (/ n 2)) 0)

    ;base case #2: if i is a factor of n return i
    ((factor? i n) i)

    ;otherwise check if i+1 is a factor
    (else (test-factor (+ i 1) n) )
  )
)

(define (find-factor n) ;calls test factor with initial input of n = 2
    (test-factor 2 n)
)

(define (prime-divisors n) 
    (cond 
      ((= (find-factor n) 0) (list n)) ;base case: when no prime factor smaller than n is found => then n is prime
      (else
        (list (find-factor n) ;output of find-factor will be prime, so append it to the list
        (prime-divisors (/ n (find-factor n)))) ;the complementary factor may or may not be prime, so recursively find its prime divisors
      )
   )
)
 ;; [CSE 262] Implement Me!



 

