package edu.lehigh.cse262.slang.Parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import edu.lehigh.cse262.slang.Scanner.Tokens;
import edu.lehigh.cse262.slang.Scanner.TokenStream;

/**
 * Parser is the second step in our interpreter. It is responsible for turning a
 * sequence of tokens into an abstract syntax tree.
 */
public class Parser {
    private final Nodes.Bool _true;
    private final Nodes.Bool _false;
    private final Nodes.Cons _empty;
    private Stack<String> ruleStack;
    private TokenStream tokens;


    public Parser(Nodes.Bool _true, Nodes.Bool _false, Nodes.Cons _empty) {
        this._true = _true;
        this._false = _false;
        this._empty = _empty;
        this.ruleStack = new Stack<String>();   //Used to track errors. Push every time a new rule is entered
        // tokens = new TokenStream();
    }

    /**
     * Transform a stream of tokens into an AST
     *
     * @param tokens a stream of tokens
     *
     * @return A list of AstNodes, because a Scheme program may have multiple
     *         top-level expressions.
     */
    public List<Nodes.BaseNode> parse(TokenStream tokens) throws Exception {
        this.tokens = tokens;
        ArrayList<Nodes.BaseNode> result = new ArrayList<Nodes.BaseNode>();
        while(!(tokens.nextToken() instanceof Tokens.Eof)){
            result.add(formTransition());
            if(!ruleStack.empty()){
                throw new Exception("Failure trying to parse: " + ruleStack.peek());
            }
        }
        return result;
    }

    /** 
     * Transitions from the form rule to either the definition or expresssion transitions
     * 
     * @return The definition/expression that is being evaluated
     */
    public Nodes.BaseNode formTransition(){
        Nodes.BaseNode form = null;
        if(tokens.nextNextToken() instanceof Tokens.Define){
            ruleStack.push("definition");
            form = definitionTransition();
        }
        else{
            ruleStack.push("expression");
            form = expressionTransition();
        }
        return form;
    }
    

    /**
     * Consumes the Tokens making up a definition
     * @return Nodes.Define
     */
    public Nodes.Define definitionTransition(){
        Nodes.Define definition = null;
        if(tokens.nextToken() instanceof Tokens.LeftParen){
            tokens.popToken();   //Consume LPAREN

            if(tokens.nextToken() instanceof Tokens.Define){
                tokens.popToken();   //Consume DEFINE

                ruleStack.push("identifier");
                Nodes.Identifier idNode = identifierTransition();
                
                ruleStack.push("expression");
                Nodes.BaseNode ex = expressionTransition();

                if(tokens.nextToken() instanceof Tokens.RightParen){
                    tokens.popToken(); //Consume RPAREN
                   
                    //check parameters are not null
                    definition = new Nodes.Define(idNode, ex);
                }
            }
        }
        if(definition != null){
            ruleStack.pop();
        }
        return definition;
    }

    /**
     * Consumes all the tokens making up an expression
     * 
     * @return The expression that is being evaluated
     */
    public Nodes.BaseNode expressionTransition(){
        Nodes.BaseNode expression = null;
        if(tokens.nextToken() instanceof Tokens.LeftParen){
            if(tokens.nextNextToken() instanceof Tokens.Quote){
                ruleStack.push("quote");
                expression = quoteHelperTransition();
            }
            else if (tokens.nextNextToken() instanceof Tokens.Lambda){
                ruleStack.push("lambda");
                expression = lambdaHelperTransition();
            }
            else if(tokens.nextNextToken() instanceof Tokens.If){
                ruleStack.push("if");
                expression = ifHelperTransition();
            }
            else if(tokens.nextNextToken() instanceof Tokens.Set){
                ruleStack.push("set");
                expression = setHelperTransition();
            }
            else if (tokens.nextNextToken() instanceof Tokens.And){
                ruleStack.push("and");
                expression = andHelperTransition();
            }
            else if (tokens.nextNextToken() instanceof Tokens.Or){
                ruleStack.push("or");
                expression = orHelperTransition();
            }
            else if(tokens.nextNextToken() instanceof Tokens.Begin){
                ruleStack.push("begin");
                expression = beginHelperTransition();
            }
            else if(tokens.nextNextToken() instanceof Tokens.Cond){
                ruleStack.push("cond");
                expression = condHelperTransition();
            }
            else{
                ruleStack.push("application");
                expression = applicationTransition();
            }
        }
        else if(tokens.nextToken() instanceof Tokens.Abbrev){
            // create a Nodes.Tick
            tokens.popToken(); //popping ABBREV
            ruleStack.push("datum");
            IValue datum = datumTransition();
            if(datum == null){
                return null;
            }
            expression = new Nodes.Tick(datum);
        }
        else if(tokens.nextToken() instanceof Tokens.Identifier){
            ruleStack.push("identifier");
            expression = identifierTransition();
        }
        else{
            ruleStack.push("constant");
            expression = constantTransition();
        }
        if(expression != null){
            ruleStack.pop();
        }
        return expression;
    }

    /**
     * Helper transition function for expressionTransition() in case the next token is Tokens.Quote
     * 
     * @return the Quote expression being evaluated
     */
    public Nodes.Quote quoteHelperTransition(){
        Nodes.Quote quote = null;
        if((tokens.nextToken() instanceof Tokens.LeftParen) && (tokens.nextNextToken() instanceof Tokens.Quote)){
            tokens.popToken();  //popping LParen
            tokens.popToken();  //popping Quote
            ruleStack.push("datum");
            IValue datum = datumTransition();
            if(datum == null){
                return null;
            }
            if(tokens.nextToken() instanceof Tokens.RightParen){
                tokens.popToken();  //popping RParen
                quote = new Nodes.Quote(datum);
            }
        }
        //Quote constructor called only if grammar is correct
        if(quote != null){
            ruleStack.pop();
        }
        return quote;
    }

    /**
     * Helper transition function for expressionTransition() in case the next token is Tokens.Lambda
     * 
     * @return the Lambda expression being evaluated
     */
    public Nodes.LambdaDef lambdaHelperTransition(){
        Nodes.LambdaDef lambda = null;
        List<Nodes.Identifier> args = null;
        List<Nodes.BaseNode> body = null;
        if((tokens.nextToken() instanceof Tokens.LeftParen) && (tokens.nextNextToken() instanceof Tokens.Lambda)){
            tokens.popToken();  //popping LParen
            tokens.popToken();  //popping Lambda
            ruleStack.push("formals");
            args = formalsTransition();
            if(args == null){
                return null;
            }
            ruleStack.push("body");
            body = bodyTransition();
            if(body == null){
                return null;
            }            
            if(tokens.nextToken() instanceof Tokens.RightParen){
                tokens.popToken();  //popping RParen
                lambda = new Nodes.LambdaDef(args, body);
            } 
        }
        if(lambda != null){
            ruleStack.pop();
        }
        return lambda;
    }

    /**
     * Helper transition function for expressionTransition() in case the next token is Tokens.If
     * 
     * @return the If expression being evaluated
     */
    public Nodes.If ifHelperTransition(){
        Nodes.If ifNode = null;
        Nodes.BaseNode cond = null;
        Nodes.BaseNode ifTrue = null;
        Nodes.BaseNode ifFalse = null;
        int counter = 0;

        if((tokens.nextToken() instanceof Tokens.LeftParen) && (tokens.nextNextToken() instanceof Tokens.If)){
            tokens.popToken(); //pop LParen
            tokens.popToken(); //pop If
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                ruleStack.push("expression");
                Nodes.BaseNode expr = expressionTransition();
                if (expr == null) 
                    return null;
                if (counter == 0)
                    cond = expr;
                else if(counter == 1)
                    ifTrue = expr;
                else if(counter == 2)
                    ifFalse = expr;
                else{   //counter >= 3
                    break;
                }
                if (tokens.nextToken() instanceof Tokens.Eof){
                    return null;
                }
                counter++;
            }
            //After the while loop, nextToken should be RParen
            if(tokens.nextToken() instanceof Tokens.RightParen){
                tokens.popToken();  //popping RParen
            }
            else{   //Otherwise, error
                //More than 3 expressions for example, or some other invalid token
                return null;    
            }
        }
        if ((cond != null) && (ifTrue != null) && (ifFalse != null)){
            ifNode = new Nodes.If(cond, ifTrue, ifFalse);
            ruleStack.pop();
        }
        return ifNode;
    }

    /**
     * Helper transition function for expressionTransition() in case the next token is Tokens.Set
     * 
     * @return the Set expression being evaluated
     */
    public Nodes.Set setHelperTransition(){
        Nodes.Set set = null;
        Nodes.Identifier iden = null;
        Nodes.BaseNode expr = null;

        if ((tokens.nextToken() instanceof Tokens.LeftParen) && (tokens.nextNextToken() instanceof Tokens.Set)){
            tokens.popToken(); //pop LParen
            tokens.popToken(); //pop Set
            if (tokens.nextToken() instanceof Tokens.Identifier){
                ruleStack.push("identifier");
                iden = identifierTransition();
                if(iden == null){
                    return null;
                }
            }
            ruleStack.push("expression");
            expr = expressionTransition();
            if(expr == null){
                return null;
            }
            if (tokens.nextToken() instanceof Tokens.RightParen){
                tokens.popToken();  //popping RParen
                set = new Nodes.Set(iden, expr);
            }
        }
        if(set != null){
            ruleStack.pop();
        }
        return set;
    }

    /**
     * Helper transition function for expressionTransition() in case the next token is Tokens.And
     * 
     * @return the And expression being evaluated
     */
    public Nodes.And andHelperTransition(){
        Nodes.And and = null;
        ArrayList<Nodes.BaseNode> exprList = new ArrayList<Nodes.BaseNode>();
        if((tokens.nextToken() instanceof Tokens.LeftParen) && (tokens.nextNextToken() instanceof Tokens.And)){
            tokens.popToken(); //pop LParen
            tokens.popToken(); //pop And
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                ruleStack.push("expression");
                Nodes.BaseNode expression = expressionTransition();
                if (expression == null){
                    return null;
                }
                exprList.add(expression);
                if (tokens.nextToken() instanceof Tokens.Eof){
                    return null;
                }
            }
            tokens.popToken();  //popping RParen
        } 

        if (exprList.size() > 0){
            and = new Nodes.And(exprList);
            ruleStack.pop();
        }
        return and;
    }
    
    /**
     * Helper transition function for expressionTransition() in case the next token is Tokens.Or
     * 
     * @return the Or expression being evaluated
     */
    public Nodes.Or orHelperTransition(){
        Nodes.Or or = null;
        ArrayList<Nodes.BaseNode> exprList = new ArrayList<Nodes.BaseNode>();
        if ((tokens.nextToken() instanceof Tokens.LeftParen) && (tokens.nextNextToken() instanceof Tokens.Or)){
            tokens.popToken(); //pop LParen
            tokens.popToken(); //pop Or
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                ruleStack.push("expression");
                Nodes.BaseNode expression = expressionTransition();
                if (expression == null){
                    return null;
                }
                exprList.add(expression);
                if (tokens.nextToken() instanceof Tokens.Eof){
                    return null;
                }
            }
            tokens.popToken();  //popping RParen
        } 
        if (exprList.size() > 0){
            or = new Nodes.Or(exprList);
            ruleStack.pop();
        }
        return or;
    }

    /**
     * Helper transition function for expressionTransition() in case the next token is Tokens.Begin
     * 
     * @return the Begin expression being evaluated
     */
    public Nodes.Begin beginHelperTransition(){
        Nodes.Begin begin = null;
        ArrayList<Nodes.BaseNode> exprList = new ArrayList<Nodes.BaseNode>();
        if ((tokens.nextToken() instanceof Tokens.LeftParen) && (tokens.nextNextToken() instanceof Tokens.Begin)){
            tokens.popToken(); //pop LParen
            tokens.popToken(); //pop Begin
            
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                ruleStack.push("expression");
                Nodes.BaseNode expression = expressionTransition();
                if (expression == null){
                    return null;
                }
                exprList.add(expression);
                if (tokens.nextToken() instanceof Tokens.Eof){
                    return null;
                }
            }
            tokens.popToken();  //popping RParen
        } 
        if (exprList.size() > 0){
            begin = new Nodes.Begin(exprList);
            ruleStack.pop();
        }
        return begin;
    }
    
    /**
     * Helper transition function for conditionTransition()
     * 
     * @return the Cond expression being evaluated
     */
    public Nodes.Cond condHelperTransition(){
        Nodes.Cond cond = null;
        ArrayList<Nodes.Cond.Condition> conditions = new ArrayList<Nodes.Cond.Condition>();
        
        if((tokens.nextToken() instanceof Tokens.LeftParen) && (tokens.nextNextToken() instanceof Tokens.Cond)){
            tokens.popToken();  //popping LParen
            tokens.popToken();  //popping Cond
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                ruleStack.push("condition");
                Nodes.Cond.Condition condition = conditionTransition();
                if(condition == null) {
                    return null;
                }
                conditions.add(condition);
                if(tokens.nextToken() instanceof Tokens.Eof){
                    return null;
                }
            }
            tokens.popToken();  //popping RParen
        }
        
        if(conditions.size() > 0){
            cond = new Nodes.Cond(conditions);
            ruleStack.pop();
        }
        return cond;
    }

    /**
     * Helper transition function for expressionTransition() in case the next token is a Conditional statement cond
     * 
     * @return the condition expression being evaluated with the condition and list of expressions
     */
    public Nodes.Cond.Condition conditionTransition(){
        Nodes.Cond.Condition condition = null;
        Nodes.BaseNode test = null;
        ArrayList<Nodes.BaseNode> expressions = new ArrayList<Nodes.BaseNode>();
        boolean testDone = false;

        if(tokens.nextToken() instanceof Tokens.LeftParen){
            tokens.popToken();  //popping LParen
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                ruleStack.push("expression");
                Nodes.BaseNode expr = expressionTransition();
                if (expr == null) {
                    return null;
                }
                if (testDone){
                    expressions.add(expr);
                }
                else{
                    test = expr;
                }
                testDone = true;
                if(tokens.nextToken() instanceof Tokens.Eof){
                    return null;
                }
            }
            tokens.popToken();  //popping RParen
            
        }
        if(test != null){
            condition = new Nodes.Cond.Condition(test, expressions);
            ruleStack.pop();
        }
        return condition;
    }


    /**
     * This implementation is the rule:
     * <body> --> <definition>* <expression>+
     * 
     * Relies on all grammar rules using a <body> to be followed by RPAREN
     * @return List of BaseNodes making up a body
     */
    public List<Nodes.BaseNode> bodyTransition(){
        ArrayList<Nodes.BaseNode> body = new ArrayList<Nodes.BaseNode>();
        boolean parsingDefinitions = true;
        boolean oneOrMoreExpressions = false;
        
        while(parsingDefinitions){
            if (tokens.nextToken() instanceof Tokens.LeftParen){
                if(tokens.nextNextToken() instanceof Tokens.Define){
                    ruleStack.push("definition");
                    Nodes.Define definition = definitionTransition();
                    if(definition == null){

                        return null;
                    }
                    body.add(definition);
                }
                else{
                    parsingDefinitions = false;
                }   
            }
            else{
                break;
            }     
        }
        //in our grammar this function is always called preceding a RightParenToken
        //Though not popping the RParen in this function, leave that to the lambdaHelper
        while(!(tokens.nextToken() instanceof Tokens.RightParen)){
            
            ruleStack.push("expression");
            Nodes.BaseNode expression = expressionTransition();
            if(expression == null){
                return null;
            }
            body.add(expression);
            oneOrMoreExpressions = true;
            if (tokens.nextToken() instanceof Tokens.Eof) {
                return null;
            }
        }
        if(oneOrMoreExpressions){
            ruleStack.pop();
            return body;
        }
        else{
            return null;
        }
    }
    
    /**
     * Consumes Parens and possibly Identifer tokens
     * @return List of Identifier Nodes
     */
    public List<Nodes.Identifier> formalsTransition(){
        boolean enclosed = false;
        ArrayList<Nodes.Identifier> formals = new ArrayList<Nodes.Identifier>();
        if(tokens.nextToken() instanceof Tokens.LeftParen) {
            tokens.popToken();  //popping LParen
            while (tokens.nextToken() instanceof Tokens.Identifier) {
                ruleStack.push("identifier");
                formals.add(identifierTransition());
            }
            if(tokens.nextToken() instanceof Tokens.RightParen){
                enclosed = true;
                tokens.popToken(); //popping RParen
            }
        }
        if(enclosed){
            ruleStack.pop();
        }
        //if not enclosed throw an error?
        //Or just check that the ruleStack isn't empty when it should be later
        return formals;
    }

    /**
     * Consumes Parens and at least one expression Token.
     * @return Apply Node containing the application
     */
    public Nodes.Apply applicationTransition(){
        Nodes.Apply application = null;
        ArrayList<Nodes.BaseNode> expressions = new ArrayList<Nodes.BaseNode>();

        if(tokens.nextToken() instanceof Tokens.LeftParen){
            tokens.popToken();  //popping LParen
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                ruleStack.push("expression");

                Nodes.BaseNode expression = expressionTransition();
                if(expression == null){
                    return null;
                }
                expressions.add(expression);
                
                if (tokens.nextToken() instanceof Tokens.Eof) {
                    return null;
                }
            }
            tokens.popToken();  //popping RParen
        }
        if(expressions.size() > 0){
            application = new Nodes.Apply(expressions);
            ruleStack.pop();
        }
        return application;
    }

    /**
     * Consumes a constant Token
     * @return BaseNode that is an IValue
     */
    public Nodes.BaseNode constantTransition() {
        Nodes.BaseNode constant = null;
        if (tokens.nextToken() instanceof Tokens.Bool) {
            if(((Tokens.Bool)tokens.nextToken()).literal){
                constant = this._true;
            }
            else{
                constant = this._false;
            }
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.Int) {
            constant = new Nodes.Int(((Tokens.Int)tokens.nextToken()).literal);
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.Dbl){
            constant = new Nodes.Dbl(((Tokens.Dbl)tokens.nextToken()).literal);
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.Char){
            constant = new Nodes.Char(((Tokens.Char)tokens.nextToken()).literal);
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.Str){
            constant = new Nodes.Str(((Tokens.Str)tokens.nextToken()).literal);
            tokens.popToken();
        }
        if(constant != null){
            ruleStack.pop();
        }
        return constant;
        
    }

    /**
     * Consumes parens and datum token
     * @return datum node
     */
    public IValue datumTransition() {
        IValue datum = null;
        if (tokens.nextToken() instanceof Tokens.Bool) {
            if(((Tokens.Bool)tokens.nextToken()).literal){
                datum = this._true;
            }
            else{
                datum = this._false;
            }
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.Int) {
            datum = new Nodes.Int(((Tokens.Int)tokens.nextToken()).literal);
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.Dbl){
            datum = new Nodes.Dbl(((Tokens.Dbl)tokens.nextToken()).literal);
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.Char){
            datum = new Nodes.Char(((Tokens.Char)tokens.nextToken()).literal);
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.Str){
            datum = new Nodes.Str(((Tokens.Str)tokens.nextToken()).literal);
            tokens.popToken();
        }
        else if(tokens.nextToken() instanceof Tokens.LeftParen){
            ruleStack.push("list");
            datum = listTransition();
        }
        else if(tokens.nextToken() instanceof Tokens.Vec){
            ruleStack.push("vector");
            datum = vecTransition();
        }
        else{
            ruleStack.push("symbol");
            datum = symbolTransition();
        }
        if(datum != null){
            ruleStack.pop();
        }
        
        return datum;
    }

    /**
     * Consumes the Parens and at least 1 datum token
     * @return list of datum tokens
     */
    public Nodes.Cons listTransition(){
        Nodes.Cons pair = null;
        IValue car;
        IValue cdr;
        boolean boundedByParens = false;
        
        if(tokens.nextToken() instanceof Tokens.LeftParen){
            tokens.popToken();  //popping LParen
            boundedByParens = true;
        }
        //list has 0 arguments
        if(tokens.nextToken() instanceof Tokens.RightParen){
            pair = this._empty;
            tokens.popToken();   //popping RParen
        }
        //list has >0 arguments
        else{
            ruleStack.push("datum");
            car = datumTransition();
        
            //cdr of pair is empty (1 element in list)
            if(tokens.nextToken() instanceof Tokens.RightParen){
                cdr = this._empty;
                if(boundedByParens){
                    tokens.popToken();  //popping RParen
                }
                pair = new Nodes.Cons(car, cdr);
            }
            //cdr is not empty (>2 elements in list)
            else{
                ruleStack.push("list");
                cdr = listTransition();
                if(tokens.nextToken() instanceof Tokens.RightParen){
                    if(boundedByParens){
                        tokens.popToken();  //popping RParen
                    }
                    pair = new Nodes.Cons(car, cdr);
                }
            }
        }
        
        if(pair != null){
            ruleStack.pop();
        }
        return pair;
    }

    /**
     * Consumes Vector, datum tokens, and RParen
     * @return Vec Node containing a vector
     */
    public Nodes.Vec vecTransition() {
        Nodes.Vec vector = null;
        ArrayList<IValue> items = new ArrayList<IValue>();
        if(tokens.nextToken() instanceof Tokens.Vec){
            tokens.popToken();  //popping Vec
            
            while(!(tokens.nextToken() instanceof Tokens.RightParen)){
                ruleStack.push("datum");
                IValue datum = datumTransition();
                if(datum == null){
                    return null;
                }
                items.add(datum);
                
                if (tokens.nextToken() instanceof Tokens.Eof) {
                    return null;
                }
            }
            //after while loop exits, nextToken() is a RightParen
            tokens.popToken();  //popping RParen
        }
        vector = new Nodes.Vec(items);
        ruleStack.pop(); //this won't happen if we have an error, null is returned in the while loop
        return vector;
    }
    
    /**
     * Consumes an Identifier token. Returned node implements IValue
     * @return Symbol node 
     */
    public Nodes.Symbol symbolTransition(){
        Nodes.Symbol symbol = null;
        if(tokens.nextToken() instanceof Tokens.Identifier){
            symbol = new Nodes.Symbol(tokens.nextToken().tokenText);
            tokens.popToken();  //popping Identifier token
        }
        if(symbol != null){
            ruleStack.pop();
        }
        return symbol;
    }

    /**
     * Consumes an Indentifier token. Return does not implement IValue
     * @return Identifier node 
     */
    public Nodes.Identifier identifierTransition(){
        Nodes.Identifier id = null;
        if(tokens.nextToken() instanceof Tokens.Identifier){
            id = new Nodes.Identifier(tokens.nextToken().tokenText);
            tokens.popToken(); //popping Identifier token
        }
        if(id != null){
            ruleStack.pop();
        }
        return id;
    }
}