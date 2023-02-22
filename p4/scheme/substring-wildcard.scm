;; substring-wildcard is like contains-substring, but it understands
;; single-character wildcards in the pattern string.  Wildcards are represented
;; by the ? character.  Note that this is a slightly broken way of doing
;; wildcards: the '?' character cannot be matched exactly.
;;
;; Here's an example execution: 
;; (substring-wildcard "hello" "e?lo") <-- returns true
;; (substring-wildcard "hello" "yell") <-- returns false
;; (substring-wildcard "The quick brown fox jumps over lazy dogs" "q?ick") <-- returns true
;;
;; You should implement this on your own, by comparing one character at a time,
;; and should not use any string comparison functions that are provided by gsi.


;;For testing purposes:
;(define string-equal? string=?)
;(define == eq?)


;true if first chars are equal, false otherwise
(define first-match (lambda (source pattern)
    (or 
        (string-equal? (substring pattern 0 1) "?")    ;wildcard check
        (string-equal? (substring source 0 1) (substring pattern 0 1))
    )
))

(define contains-substring-helper (lambda (source source-recover pattern original-pattern)
  (cond
    ;Base case 1: pattern == "". Return true
    ((== (string-length pattern) 0)
        ;(string-null? pattern)
        #t
    )

    ;;Base case 2: pattern != "" and source == "". Return false
    ((== (string-length source) 0)
        ;(string-null? source)
        #f
    )
    
    ;; First characters match
    ((first-match source pattern)
        ; Iterate source and pattern. Keep source-recover and original-pattern the same.
        (contains-substring-helper
            (substring source 1 (string-length source)) source-recover 
            (substring pattern 1 (string-length pattern)) original-pattern
        )
    )

    ;; First characters don't match
    (#t
        ; Set source back to source-recover, then iterate both of them. Set pattern back to original-pattern. 
        (contains-substring-helper
            (substring source-recover 1 (string-length source-recover)) (substring source-recover 1 (string-length source-recover))
            original-pattern original-pattern
        )
    )
  )
))

(define substring-wildcard (lambda (source pattern) 
    (contains-substring-helper source source pattern pattern)
))