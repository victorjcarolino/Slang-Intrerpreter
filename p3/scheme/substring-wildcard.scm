;; substring-wildcard is like contains-substring, but it understands
;; single-character wildcards in the pattern string.  Wildcards are represented
;; by the ? character.  Note that this is a slightly broken way of doing
;; wildcards: the '?' character cannot be matched exactly.
;;
;; Here's an example execution: 
;; (contains-substring "hello" "e?lo") <-- returns true
;; (contains-substring "hello" "yell") <-- returns false
;; (contains-substring "The quick brown fox jumps over lazy dogs" "q?ick") <-- returns true
;;
;; You should implement this on your own, by comparing one character at a time,
;; and should not use any string comparison functions that are provided by gsi.

;; TODO: implement this function
(define (contains-substring source pattern) 

    (define (contains-substring-helper source source-recover pattern original-pattern)

        ;true if first chars are equal, false otherwise
        (define (first-match source pattern)
            (or 
                (equal? (substring pattern 0 1) "?")    ;wildcard check
                (equal? (substring source 0 1) (substring pattern 0 1))
            )
        )

        (cond
            ;Base case 1: pattern == "". Return true
            ((eq? (string-length pattern) 0)
                ;(string-null? pattern)
                #t
            )


            ;;Base case 2: pattern != "" and source == "". Return fasle
            ((eq? (string-length source) 0)
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
            (else
                ; Set source back to source-recover, then iterate both of them. Set pattern back to original-pattern. 
                (contains-substring-helper
                    (substring source-recover 1 (string-length source-recover)) (substring source-recover 1 (string-length source-recover))
                    original-pattern original-pattern
                )
            )
        )
    )
    (contains-substring-helper source source pattern pattern)
)

