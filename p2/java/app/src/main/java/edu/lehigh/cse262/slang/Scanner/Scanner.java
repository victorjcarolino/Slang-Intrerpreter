// https://groups.csail.mit.edu/mac/ftpdir/scheme-7.4/doc-html/scheme_9.html

package edu.lehigh.cse262.slang.Scanner;

import java.util.ArrayList;

import edu.lehigh.cse262.slang.Scanner.Tokens.BaseToken;

/**
 * Scanner is responsible for taking a string that is the source code of a
 * program, and transforming it into a stream of tokens.
 *
 * [CSE 262] It is tempting to think "if my code doesn't crash when I give it
 * good input, then I have done a good job". However, a good scanner needs to be
 * able to handle incorrect programs. The bare minimum is that the scanner
 * should not crash if the input is invalid. Even better is if the scanner can
 * print a useful diagnostic message about the point in the source code that was
 * incorrect. Best, of course, is if the scanner can somehow "recover" and keep
 * on scanning, so that it can report additional syntax errors.
 *
 * [CSE 262] **In this class, "even better" is good enough for full credit**
 *
 * [CSE 262] With that said, if you make a scanner that can report multiple
 * errors, I'll give you extra credit. But you'll have to let me know that
 * you're doing this... I won't check for it on my own.
 *
 * [CSE 262] This is the only java file that you need to edit in p1. The
 * reference solution has ~240 lines of code (plus 43 blank lines and ~210 lines
 * of comments). Your code may be longer or shorter... the line-of-code count is
 * just a reference.
 *
 * [CSE 262] You are allowed to add private methods and fields to this class.
 * You may also add imports.
 */
public class Scanner {

    // Nodes of finite state machine
    public enum State{
        START, CLEANBREAK, LPAREN, RPAREN, STRING, INSTRING, INSTRING2, 
        EOF, ABBREV, INCOMMENT, INID, IDENTIFIER, PM, ININT, PREDBL, 
        INDBL, INT, DBL, VCB, VEC, PRECHAR, CHAR, BOOL, ERROR, NUM_SIZE_ERROR;
    }
    
    State state;
    State prevState; //For determining type of error
    //prevState needs to be assigned any time we enter an error state or a cleanbreak state, in case the cleanbreak has an error

    char currentChar;   //Current character Scanner is reading
    int charTracker;    //Position in the source String of currentChar

    int[] currentCoords;    //row and column of the currentChar
    int[] currentTokenCoords;   //row and column of the first character in the current token

    String currentTokenText;

    String source; //Source code
    

    /** Construct a scanner */
    public Scanner() {
        //set scanState to START
        state = State.START;
        prevState = State.START;
        charTracker = -1;
        currentTokenText = "";
        source = "";
        int[] startCoords = {1,0};
        currentCoords = startCoords;
        int[] startCoords2 = {1,0};
        currentTokenCoords = startCoords2;
    }

    /*
     * Increments the charTracker, consuming the currentChar if possible, and updates coordinates.
     * 
     * If the end of the source string is not reached:
     * - sets currentChar to the next character      
     * - returns true
     * 
     * Otherwise:
     * - sets currentChar to '\0'
     * - returns false
     */
    private boolean getNextChar(){
        charTracker ++;
        if(charTracker < source.length()){
            currentChar = source.charAt(charTracker);
            incrementCoords();
        }
        else{
            currentChar = '\0';
            incrementCoords();
            return false;
        }
        return true;
    }


    /*
     * Called by getNextChar()
     * 
     * Moves the coordinates to that of the next character.
     * Increments the column or sets to zero if newline.
     * Increments the row if newline. //Do this by checking lastCharBS and currentChar?
     */
    private void incrementCoords(){
        if(currentChar == '\n'){ //newline
                currentCoords[0] ++;
                currentCoords[1] = 0;
        }
        else if(currentChar != '\r'){ //anything other than a newline
            currentCoords[1] ++;
        }
    }


    /**
     * scanTokens works through the `source` and transforms it into a list of
     * tokens. It adds an EOF token at the end, unless there is an error.
     *
     * @param source The source code of the program, as one big string, or a
     *               line of code from the REPL.
     *
     * @return A list of tokens
     */
    public TokenStream scanTokens(String source) {
        var tokens = new ArrayList<Tokens.BaseToken>();
        this.source = source;
        getNextChar();
        while(state != State.EOF){
            if(state == State.START){
                startTransition();
            }
            else if(state == State.CLEANBREAK){
                cleanBreakTransition();
            }
            else if(state == State.ABBREV){
                tokens.add(new Tokens.Abbrev(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]));
                currentTokenText = "";
                state = State.START;
            }
            else if(state == State.LPAREN){
                tokens.add(new Tokens.LeftParen(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]));
                currentTokenText = "";
                state = State.START;
            }
            else if(state == State.RPAREN) {
                tokens.add(new Tokens.RightParen(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]));
                currentTokenText = "";
                state = State.START;
            }
            else if(state == State.INCOMMENT) {
                inCommentTransition();
            }
            else if(state == State.INSTRING){
                inStringTransition();
            }
            else if(state == State.INSTRING2){  //INSTR+ equivalent
                inString2Transition();
            }
            else if(state == State.STRING) {
                String strText = currentTokenText.substring(1,currentTokenText.length() - 1);
                //What is the literal vs. tokenText for Strings?
                tokens.add(new Tokens.Str(currentTokenText, currentTokenCoords[0], currentTokenCoords[1], strText));
                currentTokenText = "";
                prevState = State.STRING;
                state = State.CLEANBREAK;
            }
            else if(state == State.INID) {
                inIdTransition();
            }
            else if(state == State.IDENTIFIER){
                //First check if the token should be for a keyword
                BaseToken keyword = keywordCheck();
                if(keyword != null){
                    tokens.add(keyword);
                }
                //If not one of the keywords, then token is an identifier
                else{
                    tokens.add(new Tokens.Identifier(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]));
                }
                currentTokenText = "";
                prevState = State.IDENTIFIER;
                state = State.CLEANBREAK;
            }
            else if(state == State.PM){
                pmTransition();
            }
            else if(state == State.ININT) {
                inIntTransition();
            }
            else if(state == State.INT){
                try{
                    int intValue = Integer.parseInt(currentTokenText);
                    tokens.add(new Tokens.Int(currentTokenText, currentTokenCoords[0], currentTokenCoords[1], intValue));
                    currentTokenText = "";
                    prevState = State.INT;
                    state = State.CLEANBREAK;
                }
                //Catch block should only ever be triggered if 
                // input number is too large to store as type int
                catch (NumberFormatException e){ 
                    prevState = State.NUM_SIZE_ERROR;
                    state = State.ERROR;
                }
            }
            else if(state == State.PREDBL) {
                preDblTransition();
            }
            else if(state == State.INDBL) {
                inDblTransition();
            }
            else if(state == State.DBL) {
                try{
                    double dblValue = Double.parseDouble(currentTokenText);
                    tokens.add(new Tokens.Dbl(currentTokenText, currentTokenCoords[0], currentTokenCoords[1], dblValue));
                    currentTokenText = "";
                    prevState = State.DBL;
                    state = State.CLEANBREAK;
                }
                //Catch block should only ever be triggered if 
                // input number is too large to store as type double               
                catch (NumberFormatException e){ 
                    prevState = State.NUM_SIZE_ERROR;
                    state = State.ERROR;
                }
            }
            else if(state == State.VCB) {
                vcbTransition();
            }
            else if(state == State.VEC) {
                tokens.add(new Tokens.Vec(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]));
                currentTokenText = "";
                state = State.START;
            }
            else if(state == State.PRECHAR){
                preCharTransition();
            }
            else if(state == State.BOOL) {
                //At this point, currentTokenText will either be "#t" or "#f"
                boolean boolValue = currentTokenText.charAt(1) == 't';
                tokens.add(new Tokens.Bool(currentTokenText, currentTokenCoords[0], currentTokenCoords[1], boolValue));
                currentTokenText = "";
                prevState = State.BOOL;
                state = State.CLEANBREAK;
            }
            else if(state == State.CHAR){
                //At this point, currentTokenText is "#\" + [some character]
                char charValue = currentTokenText.charAt(2);
                tokens.add(new Tokens.Char(currentTokenText, currentTokenCoords[0], currentTokenCoords[1], charValue));
                currentTokenText = "";
                prevState = State.CHAR;
                state = State.CLEANBREAK;
            }
            else if(state == State.ERROR){
                String errorMessage = "Error"; //placeholder text
                //Some types of errors move to the next char, so save coords first
                int errorLine = currentCoords[0];
                int errorCol = currentCoords[1];
                if(currentChar == '\0'){
                    errorMessage = "Unexpected EOF";
                    state = State.EOF;
                }
                //any time an error happens, prevState will be appropriately assigned.
                //prevState is the previous state, unless it was a CLEANBREAK - then prevState is the previous previous state.
                //prevState indicates what the error message should be and the behavior to recover from the error.
                else if(prevState == State.IDENTIFIER | prevState == State.INT | prevState == State.DBL |
                        prevState == State.BOOL | prevState == State.CHAR | prevState == State.STRING){
                    errorMessage = "Expected a cleanbreak but found character: "+currentChar;
                    state = State.START;
                }
                else if(prevState == State.START){
                    errorMessage = "Invalid character: "+currentChar;
                    getNextChar();
                    state = State.START;
                }
                else if(prevState == State.INSTRING2){
                    errorMessage = "Invalid escape character: "+currentChar;
                    getNextChar();
                    state = State.INSTRING;
                }
                else if(prevState == State.VCB){
                    errorMessage = "# followed by unexpected character: "+currentChar+" Skipping #";
                    state = State.START;
                }
                else if(prevState == State.PRECHAR){
                    errorMessage = "Incomplete char sequence. Skipping #\\";
                    state = State.START;
                }
                else if(prevState == State.PREDBL){
                    errorMessage = "Incomplete double. Skipping";
                    state = State.START;
                }
                else if(prevState == State.NUM_SIZE_ERROR){
                    errorMessage = "Number too large to be stored. Skipping";
                    state = State.CLEANBREAK;
                    prevState = State.INT; //special case - need to always specify prevState before entering CLEANBREAK
                }
                //This else block shouldn't ever be reached. Just a protection against potential bugs
                else{
                    errorMessage = "Unclassified Error, stopping scan. Inavlid character: "+currentChar;
                    tokens.clear();
                    tokens.add(new Tokens.Error(errorMessage, currentCoords[0], currentCoords[1]));
                    return new TokenStream(tokens);
                }
                //Reset token text, unless the error was from incorrect escaping
                if(state != State.INSTRING){
                    currentTokenText = "";
                }
                tokens.add(new Tokens.Error(errorMessage, errorLine, errorCol));
            }
        }
        tokens.add(new Tokens.Eof("End of File", currentCoords[0], currentCoords[1]));
        return new TokenStream(tokens);
    }

    /**
     * Adds the current character to the token text.
     * If the token text was previously empty, sets the token 
     * coordinates to the current character coordinates.
     */
    private void addToToken(){
        if(currentTokenText.length() == 0){
            currentTokenCoords[0] = currentCoords[0];
            currentTokenCoords[1] = currentCoords[1];
        }
        currentTokenText += currentChar;
    }

    private void startTransition(){
        if("'".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.ABBREV;
        }
        else if("\"".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.INSTRING;
        }
        else if("!$%&*/:<=>?~_^abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.INID;
        }
        else if("+-".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.PM;
        }
        else if("0123456789".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.ININT;
        }
        else if("#".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.VCB;
        }
        else{
            prevState = State.START;
            state = State.CLEANBREAK;
        }
        
    }

    private void cleanBreakTransition(){
        if("(".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();            
            state = State.LPAREN;
        }
        else if(")".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.RPAREN;
        }
        //space whitespace
        else if(" ".indexOf(currentChar) != -1){
            getNextChar();
            state = State.START;
        }
        //tab whitespace
        else if(currentChar == '\t'){
            getNextChar();
            state = State.START;
        }
        // \n whitespace
        else if(currentChar == '\n'){
            getNextChar();
            state = State.START;
        }
        // \r whitespace
        else if(currentChar == '\r'){
            getNextChar();
            state = State.START;
        }

        else if(";".indexOf(currentChar) != -1) {
            getNextChar();
            state = State.INCOMMENT;
        }
        else if(currentChar == '\0'){
            state = State.EOF;
        }
        else{
            state = State.ERROR;
        }
    }

    private void inCommentTransition(){
        if(currentChar == '\0'){
            state = State.EOF;
        }
        //new line exits comment
        else if(currentChar == '\n' || currentChar == '\r'){
             getNextChar();
             state = State.START;
        }
        else{ //any other character
            getNextChar();
            state = State.INCOMMENT; //redundant but for clarity
        }
    }

    private void inStringTransition(){
        if("\"".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.STRING;
        }

        //else if backslash
        //go to INSTRING+
        else if("\\".indexOf(currentChar) != -1){
            getNextChar();
            state = State.INSTRING2;
        }
        
        else if(currentChar != '\0'){
           addToToken();
           getNextChar();
           state = State.INSTRING;  //redundant but for clarity
        }
        //Otherwise, EOF reached, handle error
        else{
            prevState = State.INSTRING; //Redundant but for consistency - EOF Error will trigger before checking
            state = State.ERROR;
        }
    }

    private void inString2Transition(){
        //If character to add is a backslash or double quote,
        // we simply call addToToken
        if("\\\"".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.INSTRING;
        }
        //For the other cases, we don't want to add currentChar itself,
        // so we need to check for \n, \t, and \r cases separately
        else if("n".indexOf(currentChar) != -1){
            currentTokenText += '\n';
            getNextChar();
            state = State.INSTRING;
        }
        else if("t".indexOf(currentChar) != -1){
            currentTokenText += '\t';
            getNextChar();
            state = State.INSTRING;
        }
        else if("r".indexOf(currentChar) != -1){
            currentTokenText += '\r';
            getNextChar();
            state = State.INSTRING;
        }
        //If not one of these 5 tokens, an error is expected
        else{
            prevState = State.INSTRING2;
            state = State.ERROR;
        }
    }

    private void inIdTransition() {
        if("!$%&*/:<=>?~_^".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.INID;
        }
        else if("abcdefghijklmnopqrstuvwxyz".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.INID;
        }
        else if("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.INID;
        }
        else if("0123456789".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.INID;
        }
        else if(".+-".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.INID;
        }
        else {
            state = State.IDENTIFIER;
        }
    }

    private void pmTransition(){
        if("0123456789".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.ININT;
        }
        else{
            state = State.IDENTIFIER;
        }
    }

    private void inIntTransition() {
        if("1234567890".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.ININT;
        }
        else if(".".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.PREDBL;
        }
        else{
            state = State.INT;
        }
    }

    private void preDblTransition() {
        if("1234567890".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.INDBL;
        }
        else{
            prevState = State.PREDBL;
            state = State.ERROR;
        }
    }

    private void inDblTransition() {
        if("1234567890".indexOf(currentChar) != -1) {
            addToToken();
            getNextChar();
            state = State.INDBL;
        }
        else {
            state = State.DBL;
        }
    }

    private void vcbTransition(){
        if("(".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.VEC;
        }
        // handle /newline, /space, /tab in PRECHAR state
        else if("\\".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.PRECHAR;
        }
        else if("tf".indexOf(currentChar) != -1){
            addToToken();
            getNextChar();
            state = State.BOOL;
        }
        else{
            prevState = State.VCB;
            state = State.ERROR;
        }
    }

    private void preCharTransition(){
        //First check for \newline, \space, \tab
        //Using indexOf() to check takes care of cases where the source String
        // terminates too early, character name isn't present, or character name is present.

        // \newline
        if(source.indexOf("newline", charTracker) == charTracker){
            currentTokenText += '\n';
            //must consume the amount of chars in the special word instead of just one
            for(int i=0; i<"newline".length(); i++){    
                getNextChar();
            }
            state = State.CHAR;
        }
        // \space
        else if(source.indexOf("space", charTracker) == charTracker){
            currentTokenText += ' ';
            for(int i=0; i<"space".length(); i++){
                getNextChar();
            }
            state = State.CHAR;
        }
        // \tab
        else if(source.indexOf("tab", charTracker) == charTracker){
            currentTokenText += '\t';
            for(int i=0; i<"tab".length(); i++){
                getNextChar();
            }
            state = State.CHAR;
        }
        else if(currentCharAcceptable()){
            addToToken();
            getNextChar();
            state = State.CHAR;
        }
        else{
            prevState = State.PRECHAR;
            state = State.ERROR;
        }
    }

    /**
     * Helper method for preCharTransition.
     * Checks if currentChar is one of the disallowed chars
     * for Char Tokens: ' ', '\t', '\n', '\r', '\0'
     * @return True if and only if currentChar is not a disallowed char.
     */
    private boolean currentCharAcceptable(){
        char[] disallowedChars = {' ', '\t', '\n', '\r', '\0'};
        for(char c : disallowedChars){
            if(currentChar == c){
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a BaseToken from currentTokenText if it matches any keyword, 
     * otherwise return null
     * @return BaseToken of a keyword type, or null
     */
    private Tokens.BaseToken keywordCheck(){
        BaseToken keyword = null;
        if(currentTokenText.equals("and")){
            keyword = new Tokens.And(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        else if(currentTokenText.equals("begin")){
            keyword = new Tokens.Begin(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        else if(currentTokenText.equals("cond")){
            keyword = new Tokens.Cond(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        else if(currentTokenText.equals("define")){
            keyword = new Tokens.Define(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        else if(currentTokenText.equals("if")){
            keyword = new Tokens.If(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        else if(currentTokenText.equals("lambda")){
            keyword = new Tokens.Lambda(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        else if(currentTokenText.equals("or")){
            keyword = new Tokens.Or(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        else if(currentTokenText.equals("quote")){
            keyword = new Tokens.Quote(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        else if(currentTokenText.equals("set!")){
            keyword = new Tokens.Set(currentTokenText, currentTokenCoords[0], currentTokenCoords[1]);
        }
        return keyword;
    }

}