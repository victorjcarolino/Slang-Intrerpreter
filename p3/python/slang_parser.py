import slang_scanner as scan
from enum import Enum

# [CSE 262] You will probably find it tedious to create a whole class hierarchy
# for your Python parser.  Instead, consider whether each node type could just
# be a hash table.  In that case, you could have a function for "constructing"
# each "type", by putting some values into a hash table.

class NodeType(Enum):
    AND, APPLY, BEGIN, BOOL, BUILTINFUNC, CHAR, COND, CONDITION, CONS, DOUBLE, DEFINE, IDENTIFIER, IF, INT, LAMBDADEF, LAMBDAVAL, OR, QUOTE, SET, STRING, SYMBOL, TICK, VEC = range(0, 23)
    """
    And: 0, field1 = list of Nodes (expressions)
    Apply: 1, field1 = list of Node (expressions)
    Begin: 2, field1 = list of Nodes (expressions)
    Bool: 3, field1 = Boolean
    BuiltInFunc: 4, Not used in P3
    Char: 5, field1 = char
    Cond: 6, field1 = list of Condition Nodes
    Condition: 7, field1 = Node (expression), field2 = list of Nodes (expressions)
    Cons: 8, field1 = car (Node, IValue), field2 = cdr (Cons Node, also an IValue. Unless the Token is the empty list)
    Double: 9, field1 = double
    Define: 10, field1 = Identifier Node, field2 = Node
    Identifier: 11, field1 = String
    If: 12, field1 = Condition Node, field2 = list of Nodes (expressions)
    Int: 13, field1 = int
    LambdaDef: 14, field1 = list of Identifier Nodes, field2 = list of Nodes (expressions)
    LamdbaVal: 15, Not used in P4
    Or: 16, field1 = list of Nodes (expressions)
    Quote: 17, field1 = Node (IValue)
    Set: 18, field1 = Identifier Node, field2 = Node (expression)
    String: 19, field1 = String
    Symbol: 20, field1 = String
    Tick: 21, field1 = Node (IValue)
    Vec: 22, field1 = list of Nodes (IValues)
    
    field2 = None unless otherwise specified
    """


class Node:
    def __init__(self, type, isIValue, field1, field2):
        self.type = type
        self.isIValue = isIValue
        self.field1 = field1
        self.field2 = field2

class Parser:
    """The parser class is responsible for parsing a stream of tokens to produce
    an AST"""

    def __init__(self, true, false, empty):
        """Construct a parser by caching the environmental constants true,
        false, and empty"""
        self.true = true
        self.false = false
        self.empty = empty
        self.tokens = scan.TokenStream([])

    def parse(self, tokens):
        """parse() is the main routine of the parser.  It parses the token
        stream into an AST."""
        self.tokens = tokens #tokens is set as a Parser field
        result = []
        
        #Program is a list of 0 or more forms
        while not self.nextIs(scan.EOFTOKEN):
            result.append(self.formTransition())
        return result

    # 4 Helper methods
    def nextIs(self, tokenType):
        return (self.tokens.nextToken()).type == tokenType

    def nextIsOption(self, tokenTypes):
        for tokenType in tokenTypes:
            if self.nextIs(tokenType):
                return True
        return False

    def nextNextIs(self, tokenType):
        return self.tokens.nextNextToken().type == tokenType

    def pop(self):
        self.tokens.popToken()

    """Here there is a series of 'transition' functions. Form is the top level, which calls either definition or expression,
     and those subsequently call other transitions. Each function pops tokens from the tokenstream according to the Slang grammar.
     If an unexpected token is encountered, the function will raise an error."""
    def formTransition(self):
        if self.nextIs(scan.LEFT_PAREN) and self.nextNextIs(scan.DEFINE):
            return self.definitionTransition()
        else:
            return self.expressionTransition()
            
            
    def definitionTransition(self):
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In DefinitionTransition: Missing leading LParen")
        self.pop() #Consume LParen

        if not self.nextIs(scan.DEFINE) :
            raise Exception("In DefinitionTransition: Token after LParen is not Define")
        self.pop() #Consume Define

        idNode = self.identifierTransition()

        expression = self.expressionTransition()

        if not self.nextIs(scan.RIGHT_PAREN):
            raise Exception("In DefinitionTransition: Missing closing RParen")
        self.pop() #Consume RParen

        return Node(NodeType.DEFINE, False, idNode, expression)


    def expressionTransition(self):
        if self.nextIs(scan.LEFT_PAREN) :
            if self.nextNextIs(scan.QUOTE) :
                return self.quoteHelperTransition()
            elif self.nextNextIs(scan.LAMBDA):
                return self.lambdaHelperTransition()
            elif self.nextNextIs(scan.IF):
                return self.ifHelperTransition()
            elif self.nextNextIs(scan.SET):
                return self.setHelperTransition()
            elif self.nextNextIs(scan.AND):
                return self.andHelperTransition()
            elif self.nextNextIs(scan.OR):
                return self.orHelperTransition()
            elif self.nextNextIs(scan.BEGIN):
                return self.beginHelperTransition()
            elif self.nextNextIs(scan.COND):
                return self.condHelperTransition()
            else:
                return self.applicationTransition()
        elif self.nextIs(scan.ABBREV):
            self.pop() #popping ABBREV
            datum = self.datumTransition()
            return Node(NodeType.TICK, False, datum, None)
        elif self.nextIs(scan.IDENTIFIER):
            return self.identifierTransition()
        elif self.nextIsOption([scan.BOOL, scan.INT, scan.DBL, scan.CHAR, scan.STR]):
            return self.constantTransition()
        else:
            print(self.tokens.nextToken().tokenText)
            raise Exception("Malformed Expression")
    
    
    def quoteHelperTransition(self):
        if (not self.nextIs(scan.LEFT_PAREN)):
            raise Exception("In QuoteHelperTransition: Missing Left Paren")
        self.pop() #poppping LParen
        if (not self.nextIs(scan.QUOTE)):
            raise Exception("In QuoteHelperTransition: Missing Quote Token")
        self.pop() #popping quote
        datum = self.datumTransition()
        if (not self.nextIs(scan.RIGHT_PAREN)):
            raise Exception("In QuoteHelperTransition: Missing RParen")
        self.pop()
        return Node(NodeType.QUOTE, True, datum, None)


    def lambdaHelperTransition(self):
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In LambdaHelperTransition: Missing LParen")
        self.pop() #popping LParen
        if not self.nextIs(scan.LAMBDA):
            raise Exception("In LambdaHelperTransition: Missing Lambda Token")
        self.pop() #popping Lambda
        formals = self.formalsTransition()
        body = self.bodyTransition()
        if not self.nextIs(scan.RIGHT_PAREN):
            raise Exception("In LambdaHelperTransition: Missing RParen")
        self.pop() #popping RParen
        return Node(NodeType.LAMBDADEF, False, formals, body)
    
    def ifHelperTransition(self):
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In IfHelperTransition: Missing LParen")
        self.pop() #popping LParen
        if not self.nextIs(scan.IF):
            raise Exception("In IfHelperTransition: Missing If Token")
        self.pop() #popping If
        cond = self.expressionTransition()
        true_expr = self.expressionTransition()
        false_expr = self.expressionTransition()
        if not self.nextIs(scan.RIGHT_PAREN):
            print(self.tokens.nextToken().tokenText)
            raise Exception("In IfHelperTransition: Missing RParen")
        self.pop()
        return Node(NodeType.IF, False, cond, [true_expr, false_expr])
        

    def setHelperTransition(self):
        if not (self.nextIs(scan.LEFT_PAREN)):
            raise Exception("In setHelperTransition: Missing LParen")
        self.pop() #popping LParen
        if not (self.nextIs(scan.SET)):
            raise Exception("In setHelperTransition: Missing Set Token")
        self.pop() #popping Set token
        iden = self.identifierTransition()
        expr = self.expressionTransition()
        if not self.nextIs(scan.RIGHT_PAREN):
            raise Exception("In setHelperTransition: Missing RParen")
        self.pop() # popping rightParen
        return Node(NodeType.SET, False, iden, expr)


    def andHelperTransition(self):
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In AndHelperTransition: Missing LParen")
        self.pop() #popping LParen
        if not self.nextIs(scan.AND):
            raise Exception("In AndHelperTransition: Missing And Token")
        self.pop() #popping And
        expressions = []
        while not self.nextIs(scan.RIGHT_PAREN):
            expressions.append(self.expressionTransition())
            if self.nextIs(scan.EOFTOKEN):
                raise Exception("In AndHelperTransition: Unexpected EOF")
        self.pop() #popping RParen
        if len(expressions) == 0:
            raise Exception("In AndHelperTransition: No expressions")
        return Node(NodeType.AND, False, expressions, None)

    def orHelperTransition(self):
        expressions = []
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In orHelperTransition: Missing LParen")
        self.pop() #popping LParen
        if not self.nextIs(scan.OR):
            raise Exception("In orHelperTransition: Missing Or")
        self.pop() #popping Or
        while not self.nextIs(scan.RIGHT_PAREN):
            expressions.append(self.expressionTransition())
            if self.nextIs(scan.EOFTOKEN):
                raise Exception("In orHelperTransition: Unexpected EOF")
        self.pop() #popping RParen
        if len(expressions) == 0:
            raise Exception("In orHelperTransition: No expressions")
        return Node(NodeType.OR, False, expressions, None)



    def beginHelperTransition(self):
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In BeginTransition: Missing LParen")
        self.pop() #popping LParen
        if not self.nextIs(scan.BEGIN):
            raise Exception("In BeginTransition: Missing Begin Token")
        self.pop() #popping
        expressions = []
        while not self.nextIs(scan.RIGHT_PAREN):
            expressions.append(self.expressionTransition())
            if self.nextIs(scan.EOFTOKEN):
                raise Exception("In BeginHelperTransition: Unexpected EOF")
        self.pop() #popping RParen
        if len(expressions) == 0:
            raise Exception("In BeginHelperTransition: No expressions")
        return Node(NodeType.AND, False, expressions, None)


    def condHelperTransition(self):
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In CondHelperTransition: Missing LParen")
        self.pop() #popping LParen
        if not self.nextIs(scan.COND):
            raise Exception("In CondHelperTransition: Missing Cond Token")
        self.pop() #popping
        conditions = []
        while not self.nextIs(scan.RIGHT_PAREN):
            conditions.append(self.conditionTransition())
            if self.nextIs(scan.EOFTOKEN):
                raise Exception("In CondHelperTransition: Unexpected EOF")
        self.pop() #popping RParen
        if len(conditions) == 0:
            raise Exception("In CondHelperTransition: No expressions")
        return Node(NodeType.COND, False, conditions, None)
        

    def conditionTransition(self):
        expressions = []
        testDone = False
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In conditionTransition: Missing LParen")
        self.pop() #popp        
        while not self.nextIs(scan.RIGHT_PAREN):
            expr = self.expressionTransition()
            if testDone:
                expressions.append(expr)
            else:
                test = expr
            testDone = True
        self.pop() #popping RParen
        return Node(NodeType.CONDITION, False, test, expressions)
                
        
    def bodyTransition(self):
        doneParsingDefinitions = False
        bodyNodes = []
        atLeastOneExpression = False
        while not doneParsingDefinitions:
            if self.nextIs(scan.LEFT_PAREN) and self.nextNextIs(scan.DEFINE):
                bodyNodes.append(self.definitionTransition())
                if self.nextIs(scan.EOFTOKEN):
                    raise Exception("In bodyTransition: Unexpected EOF (loop1)")
            else:
                doneParsingDefinitions = True
        while not self.nextIs(scan.RIGHT_PAREN):
            bodyNodes.append(self.expressionTransition())
            atLeastOneExpression = True
            if self.nextIs(scan.EOFTOKEN):
                raise Exception("In bodyTransition: Unexpected EOF (loop2)")
        if not atLeastOneExpression:
            raise Exception("In bodyTransition: Zero expressions parsed")
        return bodyNodes

    
    def formalsTransition(self):
        formalsList = []
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In formalsTransition: Missing LParen")
        self.pop() #popping LParen
        while self.nextIs(scan.IDENTIFIER):
            formalsList.append(self.identifierTransition())
        if not self.nextIs(scan.RIGHT_PAREN):
            print(self.tokens.nextToken().tokenText)
            raise Exception("In formalsTransition: Missing RParen")
        self.pop() #popping RParen
        return formalsList

    
    def applicationTransition(self):
        if not self.nextIs(scan.LEFT_PAREN):
            raise Exception("In applicationTransition: Missing LParen")
        self.pop() #popping LParen
        expressions = []
        while not self.nextIs(scan.RIGHT_PAREN):
            expressions.append(self.expressionTransition())
            if self.nextIs(scan.EOFTOKEN):
                raise Exception("In applicationTransition: Unexpected EOF")
        self.pop() #popping RParen
        if len(expressions) == 0:
            raise Exception("In applicationTransition: Zero expressions")
        return Node(NodeType.APPLY, False, expressions, None)


    def constantTransition(self):
        if self.nextIs(scan.BOOL):
            if self.tokens.nextToken().literal:
               constant = self.true
               self.pop()
            else:
                constant = self.false
                self.pop()
        elif self.nextIs(scan.INT):
            constant = Node(NodeType.INT, False, self.tokens.nextToken().literal, None)
            self.pop()
        elif self.nextIs(scan.DBL):
            constant = Node(NodeType.DOUBLE, False, self.tokens.nextToken().literal, None)
            self.pop()
        elif self.nextIs(scan.CHAR):
            constant = Node(NodeType.CHAR, False, self.tokens.nextToken().literal, None)
            self.pop()
        elif self.nextIs(scan.STR):
            constant = Node(NodeType.STRING, False, self.tokens.nextToken().literal, None)
            self.pop()
        else:
            raise Exception("In constantTransition: Not a constant type")
        return constant


    def datumTransition(self):
        if self.nextIs(scan.BOOL):
            if self.tokens.nextToken().literal:
               datum = self.true
               self.pop()
            else:
                datum = self.false
                self.pop()
        elif self.nextIs(scan.INT):
            datum = Node(NodeType.INT, False, self.tokens.nextToken().literal, None)
            self.pop()
        elif self.nextIs(scan.DBL):
            datum = Node(NodeType.DOUBLE, False, self.tokens.nextToken().literal, None)
            self.pop()
        elif self.nextIs(scan.CHAR):
            datum = Node(NodeType.CHAR, False, self.tokens.nextToken().literal, None)
            self.pop()
        elif self.nextIs(scan.STR):
            datum = Node(NodeType.STRING, False, self.tokens.nextToken().literal, None)
            self.pop()
        elif self.nextIs(scan.LEFT_PAREN):
            datum = self.listTransition()
        elif self.nextIs(scan.VECTOR):
            datum = self.vecTransition()
        elif self.nextIs(scan.IDENTIFIER):
            datum = self.symbolTransition()
        else:
            raise Exception("In datumTransition: not a datum")
        return datum
        
    def listTransition(self):
        boundedByParens = False
        if self.nextIs(scan.LEFT_PAREN):
            self.pop() #popping LParen
            boundedByParens = True
        if self.nextIs(scan.RIGHT_PAREN):   #list has 0 arguments
            self.pop() #popping RParen
            return self.empty       #preset Cons Node for empty pair
        else:   #list has >0 arguments
            car = self.datumTransition()
            if self.nextIs(scan.RIGHT_PAREN):   #cdr is empty (1 arg total)
                cdr = self.empty
                if boundedByParens:
                    self.pop() #pop RParen, only if this call to listTransition started with popping a LParen
                return Node(NodeType.CONS, True, car, cdr)
            else:   #list has >1 args total
                cdr = self.listTransition()
                if not self.nextIs(scan.RIGHT_PAREN):
                    raise Exception("In listTransition: Missing RParen")
                if boundedByParens:
                    self.pop() #popping RParen
                return Node(NodeType.CONS, True, car, cdr)
        
        
    def vecTransition(self):
        items = []
        if not self.nextIs(scan.VECTOR):
            raise Exception("In vecTransition: Missing Vector")
        self.pop() #popping LParen
        while not self.nextIs(scan.RIGHT_PAREN):
            datum = self.datumTransition()
            items.append(datum)
            if self.nextIs(scan.EOFTOKEN):
                raise Exception("In vecTransition: Unexpected EOF")
        self.pop() #popping RParen
        return Node(NodeType.VEC, False, items, None)


    def symbolTransition(self):
        if not self.nextIs(scan.IDENTIFIER):
            raise Exception("In symbolTransition: Missing Identifier token")
        symbol = Node(NodeType.SYMBOL, True, self.tokens.nextToken().tokenText, None)
        self.pop() #popping Identifier
        return symbol

    def identifierTransition(self):
        if not self.nextIs(scan.IDENTIFIER):
            raise Exception("In identifierTransition: Missing Identifier Token")
        id = Node(NodeType.IDENTIFIER, False, self.tokens.nextToken().tokenText, None)
        self.pop() #popping Identifier
        return id