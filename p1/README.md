# CSE 262 Assignment 1

The purpose of this assignment is to ensure that you are familiar with the three
programming languages that we will use in this class: Java, Python, and Scheme.
Among the goals of this assignment are:

* To make sure you have a proper development environment for using these
  languages
* To introduce you to these languages, if you haven't used them before
* To introduce you to some features of these languages that you may not have
  seen before
* To get you thinking about how to program idiomatically

## Parts of the Assignment

This assignment has *four* parts, which are contained in three sub-folders:
`java`, `python`, and `scheme`.  Three tasks are similar: in Java, Python, and
Scheme, you will implement five "programs":

* `read_list` -- Read a list of values from stdin and put them in a list
* `reverse` -- Reverse a list, without using any built-in list functions
* `map` -- Apply a function to all elements of a list, without using any
  built-in map functions
* `tree` -- Implement a binary tree
* `prime_divisors`-- Factor an integer into its prime divisors

The README file in each sub-folder has some more information about programming
in each of these languages.

The *fourth* part of the assignment is to answer the questions at the end of
this file.

## Development Environments

I strongly encourage you to use Visual Studio Code as your development
environment.  It has good plug in support for Java and Python, and reasonable
support for Scheme.  This support is not just syntax highlighting, but also code
formatting, refactoring, code completion, and tooltips.  It will help you to
write better code in less time.

VSCode also has two very important features for this assignment: VSCode Remote
and Live Share.  If you do not want to install Java, Python, and Scheme on your
computer, you can use the sunlab, and with VSCode Remote, you can use VSCode to
connect to sunlab.  It's very nice.  If you choose to work in a team of two,
Live Share will make it much easier to pair program.

## Teaming

You may work in teams of two for this assignment.  If you choose to work in a
team, you should **pair program**.  You should not split the assignment.  You
will not be able to succeed in this class if you do not understand everything in
this assignment.  Furthermore, if you split the work, you and your teammate will
wind up having to solve the same hard problems, which means you'll do 100% of
the work for each step.  In contrast, if you pair program, things you figure out
in Java won't need to be re-learned in Python, so you'll do only about 50% of
the work for Python... that savings adds up!

If you wish to work in a team, you must email Prof Spear <spear@lehigh.edu>.
Your email must follow these rules:

1. You must cc your project partner, so that I know that both team members
are aware of the team request.
2. You must tell me which team member's repository you will be working in.

I will change the permissions on that repository, so that both students can
read and write to it.  You will not need to submit the assignment twice.

## Documentation

You are **required** to follow the documentation instructions
that accompany each part of the assignment.  Correct code that does not have
documentation will not receive full points.

**DO NOT FORGET THE QUESTIONS AT THE END OF THIS FILE**

## Deadlines

This assignment is due by 11:59 PM on Friday, September 9th.  You should have
received this assignment by using `git` to `clone` a repository onto the machine
where you like to work.  You can use `git add`, `git commit`, and `git push` to
submit your work.

You are strongly encouraged to proceed *incrementally*: as you finish parts of
the assignment, `commit` and `push` them.

## Start Early

You should not wait until the last minute to start this assignment.  Start
early, and stop often.  This strategy will maximize your learning and minimize
your stress.  I promise.

## Questions

Please be sure to answer all of the following questions by writing responses in
this document.

### Read List

* Did you run into any trouble using `let`?  Why?
    We ran into several issues with let revolving around an "ill-formed let" error. The issue, we believe, arose due to an incorrect amount of arguments being passed to let from parentheses confusion.
* What happens if the user enters several values on one line?
    (read) will detect the white space and split multiple values into different elements in the list
* What happens if the user enters non-integer values?
    Non-integer values have worked in our testing, specifically using strings. (list) can take any expression as an argument.
* Contrast your experience solving this problem in Java, Python, and Scheme.
    In both Java and Python we used while loops, repeating the same process until EOF was detected.
    In Scheme, we used a recursive function with a base case using
    the function (eof-object?) to check the input.

### Reverse

* What is tail recursion?
    Tail recursion occurs when the recursive call is the last expression. This eliminates the need
    for storing the stack frame of the 'outer' function. Some languages (such as Scheme) take advantage
    of this in order to be more efficient.
* Is your code tail recursive?
    No, after the recursion call, we have the expression (car l), so the stack must keep track
    of l at every level.
* How would you write a test to see if Scheme is applying tail recursion
  optimizations?
    To test if Scheme has tail recursion optimizations, create two versions of the same function, but
    with the last two expressions reversed, such that one function uses tail recursion and the other does not.
    Test both with a "large" input that requires many recursive calls, and compare how much memory is being used 
    to run the scheme program for the tail recursion version versus the non tail recursion version. If there are 
    optimizations, the tail recursion function should use less memory.
* Contrast your experience solving this problem in Java, Python, and Scheme.
    In Java and Python, we used iteration, while in Scheme, we used recursion.
    In Java we used an iterator starting at the end of the List, adding its output to a new list as it
    iterates through the entire input List.
    In Python, we used a for loop and simply accessed each element in reverse order, appending them to a new list.
    In Scheme, our recursion had a base case of the list being empty, otherwise it used the (list) function 
    to return: First: (my-reverse) with the (cdr) of the list as the input, and Second: the (car) of the list

  

### Map

* What kinds of values can be in `l`?
   l is list of any expression - could be strings, numbers, functions. We used ints, strings, and functions in testing, and all worked with a function that converts inputs to the int 2.
* What are the arguments to the function `func`?
    The car of current list l is the argument to the function 'func'
* Why is this function built into scheme when it's so simple to write?
    With all structures in scheme revolving around lists, altering the values in a list with a native function is a useful tool
* Contrast your experience solving this problem in Java, Python, and Scheme.
    In Java and Python, we used a for loop to iterate through the given list and append the output list. In scheme we created a recursive function that applies the function func to the first value of list l and appends the value to a list.


### Tree

* How do you feel about closures versus objects?  Why?
    Closures are confusing...but seem to work the same as objects? - At least for our 
    purposes so far. Closures might find more errors at compile time because everything 
    is defined at once.(?) But they lose the flexibility of inheritance.(?)
    
* How do you feel about defining a tree node as a generic triple?
    It feels like a natural way to condense the information of a node. The syntax is uglier, so it's worse to read
    but simpler. Another advantage is that defining a tree (not necessarily binary tree) node as an 
    n-tuple is much more scalable than defining each child node with a specific name 
    (like doing: .leftleft, .leftright, .rightleft, .rightright).
    
* Contrast your experience solving this problem in Java, Python, and Scheme.
    In Java and Python, we defined Node classes for the tree methods to use. In Scheme, using closure essentially 
    required defining every method within the "constructor". 
    

### Prime Divisors

* Why did you choose the Scheme constructs that you chose in order to solve this
  problem?
    We chose our Scheme constructs based off of what solved the errors we were receiving. We used (define) for 
    helper functions that served as the Scheme equivalent of iterating an int i with a for loop and checking if 
    i is a factor of n.
* Contrast your experience solving this problem in Java, Python, and Scheme.
    In both Java and Python, we use a for loop to iterate from 2 to n/2 and check if n%i == 0. In scheme, we use a recursive function with helper functions to complete tasks done in the for loop from Java and Python such as checking if i is a factor of n.
    The algorithm remained nearly exact between languages, but in Scheme we used recursion for tasks instead of one: 
    1. finding the smallest prime factor of n (in place of using a for loop)
    2. finding prime divisors for the complementary factor of n (same in all languages)
    
