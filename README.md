# Slang-Intrerpreter

As part of my CSE 262: Programming Languages course at Lehigh University, I, along with my teammate, created a fully functional interpreter for a Scheme functional programming language subset that we called Slang. The only difference between Slang and Scheme is that Slang does not support some syntactic sugar expressions from Scheme such as let, let*, and letrec.

Each subproject (4 in total) has its own README that details the specifications that the interpreter was designed for, and each subproject folder will contain a Scheme, Java, and Python folder. The Scheme folders are programming assignments that me and my teammate had to complete to gain a better understanding of the functional programming language paradigms. The Java and Python folders contain the code written for the interpreter. The Java code also contains some starter-code provided by our instructor. The Python folders however, had no starter code and were completely developed by myself and my teammate. Each subproject does not assume that we have completed the previous assignment, even if the assignments build atop each other. With this being said, my partner and I were able to secure an average of a 99% accross the 4 assignments with only minimal bugs.

Project 1 was meant to introduce us to the concept of functional programming and its differences compared to statically typed object-oriented programming languages and dynamically typed all-purpose languages such as Python. For this reason, I will omit the README summary for this section. Feel free to look further into the repository for the associated code if you'd like.

I will now present short exerpts from each of the programming project's REAMDME's as to give the viewer (you) an easier time understanding the scope of this project without having to traverse the repository if you would not like to.

#CSE 262: Programming Assignment 2: Scanning "slang"

In the remaining three assignments of the semester, we will be building a full
interpreter for a relatively complete subset of the Scheme programming language.
To avoid confusion, we will call our language "slang" (because it is a
Scheme-like LANGuage).

Slang will be (with very few exceptions) a proper subset of Scheme: any valid
slang program will be a valid scheme program, but not all valid scheme programs
will work in slang.

In the first phase of the assignment, we will only worry about *scanning* slang.
That is, given a string that purports to be slang code, we will try to turn it
into a sequence of tokens.  Just like `gsi`, slang's interpreter can either be
given a file, or take input via a "read, evaluate, print loop" (REPL).

Implementing a scanner for slang will provide an opportunity to learn more about
Scheme.  Implementing it twice, in two different languages, will provide an
opportunity to compare and contrast different programming languages.  We'll also
write some Scheme code as part of this assignment, in order to get a deeper
understanding of functional programming and Scheme syntax.

## Project Details

Scanning is the first step in any compiler or interpreter.  It is the step that
turns source code into "tokens" that are easier to work with throughout the rest
of the compiler/interpreter.  Since the scanner in this assignment is not
connected to a parser, it will simply output the tokens.  Java code for this
output is provided.


# CSE 262: Programming Assignment 3: Parsing "slang"

In this assignment, you will build a parser for the relatively complete subset
of the Scheme programming language that we started working with in the previous
assignment.  In order to gain experience working in new languages, you will
implement the parser twice, once in Java, and then in Python.  You will also
solve a few programs in Scheme.

## Project Details

Parsing is the second step in any compiler or interpreter.  It is the step
that turns tokens into some sort of tree structure.  Since this parser is not
connected to an interpreter, it will simply output the tree that it produces.
Java code for this output is provided.  Note that since the context free
grammar has nested constructs, the tree will not be a list: there will be
children of some nodes.


# CSE 262: Programming Assignment 4: Interpreting Slang

In this assignment, we will complete the last stage of our "Slang" interpreter,
by building a tree-walk interpreter.  This will let you execute programs for the
relatively complete subset of the Slang programming language that we have been
working with in the previous assignments.  In order to gain experience working
in new languages, you will implement the interpreter twice, once in Java, and
then in Python.  You will also write some Scheme code that you can use as test
cases for your interpreter.

## Project Details

Interpreting is not hard, once you get the hang of what it entails.  The hard
part is creating a standard library of useful functions.  One way to think about
it is like this: as you walk the AST (via a Visitor), many nodes just need to
return themselves, and many of the rest just need to do somewhat "standard"
computation.  However, when there is an Apply of a function to its arguments, if
that is a built-in function, we need to have some code to run.  Furthermore,
that code needs to be very smart: it needs to check the types of its arguments,
check the number of arguments, and only operate if it is reasonable to do so.
