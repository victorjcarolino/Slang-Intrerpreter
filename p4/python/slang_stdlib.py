import slang_parser as parser
import operator as op
import math

def oneOrMoreArgsCheck(args, funcName):
    if len(args) == 0:
        raise Exception("'" + str(funcName) + "' expects at least one argument.")
    
def exactAmountArgsCheck(args, funcName, amount):
    if len(args) != amount:
        raise Exception("'" + str(funcName) + "' expects exactly " + str(amount) + " argument(s)")

def numbersOnlyArgCheck(args, funcName):
    for arg in args:
        if not arg["type"] == parser.DBL and not arg["type"] == parser.INT:
            raise Exception("'" + str(funcName) + "' expects only number arguments.")
            
def checkForDoubleArg(args):
    for arg in args:
        if arg["type"] == parser.DBL:
            return True
    return False

def typeChecker(args, funcName, desiredType):
    if type(args) is list:
        for arg in args:
            if arg is None or arg["type"] != desiredType:
                raise Exception("Type Error: '" + str(funcName) + "' recieved an argument of invalid type")
    else:
        if args is None or args["type"] != desiredType:
            raise Exception("Type Error: '" + str(funcName) + "' recieved an argument of invalid type")

def put(env, name, func):
    env.put(name, parser.BuiltInNode(name, func))

def addMathFuncs(env):
    """Add standard math functions to the given environment"""
    
    def add(args):
        oneOrMoreArgsCheck(args, "+")
        numbersOnlyArgCheck(args, "+")
        result = 0
        for arg in args:
            result += arg["val"]
        if checkForDoubleArg(args):
            return parser.DblNode(result) 
        else:
            return parser.IntNode(result)
    put(env, "+", add)

    def subtract(args):
        oneOrMoreArgsCheck(args, "-")
        numbersOnlyArgCheck(args, "-")
        result = args[0]["val"]
        for arg in args[1:]:
            result -= arg["val"]
        if checkForDoubleArg(args):
            return parser.DblNode(result)
        else:
            return parser.IntNode(result)
    put(env, "-", subtract)
    
    def multiply(args):
        oneOrMoreArgsCheck(args, "*")
        numbersOnlyArgCheck(args, "*")
        result = 1
        for arg in args:
            result *= arg["val"]
        if checkForDoubleArg(args):
            return parser.DblNode(result) 
        else:
            return parser.IntNode(result)
    put(env, "*", multiply)


    def divide(args):
        oneOrMoreArgsCheck(args, "/")
        numbersOnlyArgCheck(args, "/")
        result = args[0]["val"]
        for arg in args[1:]:
            result /= arg["val"]
        if result != int(result) or checkForDoubleArg(args):
            return parser.DblNode(result)
        else:
            return parser.IntNode(result)
    put(env, "/", divide)

    def modulus(args):
        oneOrMoreArgsCheck(args, "%")
        numbersOnlyArgCheck(args, "%")
        result = int(args[0]["val"])
        for arg in args[1:]:
            result %= int(arg["val"])
        return parser.IntNode(result)
    put(env, "%", modulus)

    def equals(args):
        oneOrMoreArgsCheck(args, "==")
        numbersOnlyArgCheck(args, "==")
        first = args[0]["val"]
        for arg in args[1:]:
            if first != arg["val"]:
                return env.poundF
        return env.poundT
    put(env, "==", equals)

    def lessThanOrEqual(args):
        oneOrMoreArgsCheck(args, "<=")
        numbersOnlyArgCheck(args, "<=")
        if len(args) == 1:
           return env.poundT
        for i in range(len(args) - 1):
            if args[i]["val"] > args[i+1]["val"]:
                return env.poundF
        return env.poundT 
    put(env, "<=", lessThanOrEqual)
        
    def lessThan(args):
        oneOrMoreArgsCheck(args, "<")
        numbersOnlyArgCheck(args, "<")
        if len(args) == 1: return env.poundT
        for i in range(len(args)-1):
            prev = args[i]["val"]
            next = args[i+1]["val"]
            if prev >= next: return env.poundF
        return env.poundT
    put(env, "<", lessThan)

    def greaterThanOrEqual(args):
        oneOrMoreArgsCheck(args, ">=")
        numbersOnlyArgCheck(args, ">=")
        if len(args) == 1:
           return env.poundT
        for i in range(len(args) - 1):
            if args[i]["val"] < args[i+1]["val"]:
                return env.poundF
        return env.poundT 
    put(env, ">=", greaterThanOrEqual)
        
    def greaterThan(args):
        oneOrMoreArgsCheck(args, ">")
        numbersOnlyArgCheck(args, ">")
        if len(args) == 1: return env.poundT
        for i in range(len(args)-1):
            prev = args[i]["val"]
            next = args[i+1]["val"]
            if prev <= next: return env.poundF
        return env.poundT
    put(env, ">", greaterThan)

    def absoluteValue(args):
        exactAmountArgsCheck(args, "abs", 1)
        numbersOnlyArgCheck(args, "abs")
        result = abs(args[0]["val"])
        if checkForDoubleArg(args):
            return parser.DblNode(result)
        else:
            return parser.IntNode(result)
    put(env, "abs", absoluteValue)

    def squareRoot(args):
        exactAmountArgsCheck(args, "sqrt", 1)
        numbersOnlyArgCheck(args, "sqrt")
        result = math.sqrt(args[0]["val"])
        return parser.DblNode(result)
    put(env, "sqrt", squareRoot)

    def arccosine(args):
        exactAmountArgsCheck(args, "acos", 1)
        numbersOnlyArgCheck(args, "acos")
        result = math.acos(args[0]["val"])
        return parser.DblNode(result)
    put(env, "acos", arccosine)

    def arcsine(args):
        exactAmountArgsCheck(args, "asin", 1)
        numbersOnlyArgCheck(args, "asin")
        result = math.asin(args[0]["val"])
        return parser.DblNode(result)
    put(env, "asin", arcsine)

    def arctangent(args):
        exactAmountArgsCheck(args, "atan", 1)
        numbersOnlyArgCheck(args, "atan")
        result = math.atan(args[0]["val"])
        return parser.DblNode(result)
    put(env, "atan", arctangent)

    def cosine(args):
        exactAmountArgsCheck(args, "cos", 1)
        numbersOnlyArgCheck(args, "cos")
        result = math.cos(args[0]["val"])
        return parser.DblNode(result)
    put(env, "cos", cosine)

    def hyperbolicCosine(args):
        exactAmountArgsCheck(args, "cosh", 1)
        numbersOnlyArgCheck(args, "cosh")
        result = math.cosh(args[0]["val"])
        return parser.DblNode(result)
    put(env, "cosh", hyperbolicCosine)

    def sine(args):
        exactAmountArgsCheck(args, "sin", 1)
        numbersOnlyArgCheck(args, "sin")
        result = math.sin(args[0]["val"])
        return parser.DblNode(result)
    put(env, "sin", sine)
    
    def hyperbolicSine(args):
        exactAmountArgsCheck(args, "sinh", 1)
        numbersOnlyArgCheck(args, "sinh")
        result = math.sinh(args[0]["val"])
        return parser.DblNode(result)
    put(env, "sinh", hyperbolicSine)

    def tangent(args):
        exactAmountArgsCheck(args, "tan", 1)
        numbersOnlyArgCheck(args, "tan")
        result = math.tan(args[0]["val"])
        return parser.DblNode(result)
    put(env, "tan", tangent)
    
    def hyperbolicTangent(args):
        exactAmountArgsCheck(args, "tanh", 1)
        numbersOnlyArgCheck(args, "tanh")
        result = math.tanh(args[0]["val"])
        return parser.DblNode(result)
    put(env, "tanh", hyperbolicTangent)

    def isInteger(args):
        exactAmountArgsCheck(args, "integer?", 1)
        if args[0]["type"] == parser.INT:
            return env.poundT
        return env.poundF
    put(env, "integer?", isInteger)

    def isDouble(args):
        exactAmountArgsCheck(args, "double?", 1)
        if args[0]["type"] == parser.DBL:
            return env.poundT
        return env.poundF
    put(env, "double?", isDouble)

    def isNumber(args):
        exactAmountArgsCheck(args, "procedure?", 1)
        if args[0]["type"] == parser.INT or args[0]["type"] == parser.DBL:
            return env.poundT
        return env.poundF
    put(env, "number?", isNumber)

    def isSymbol(args):
        exactAmountArgsCheck(args, "symbol?", 1)
        if args[0]["type"] == parser.SYMBOL:
            return env.poundT
        return env.poundF
    put(env, "symbol?", isSymbol)

    def isProcedure(args):
        exactAmountArgsCheck(args, "procedure?", 1)
        if args[0]["type"] == parser.BUILTIN or args[0]["type"] == parser.LAMBDAVAL:
            return env.poundT
        return env.poundF
    put(env, "procedure?", isProcedure)

    def logBase10(args):
        exactAmountArgsCheck(args, "log10", 1)
        numbersOnlyArgCheck(args, "log10")
        result = math.log10(args[0]["val"])
        return parser.DblNode(result)
    put(env, "log10", logBase10)
        
    def logBaseE(args):
        exactAmountArgsCheck(args, "loge", 1)
        numbersOnlyArgCheck(args, "loge")
        result = math.log(args[0]["val"])
        return parser.DblNode(result)
    put(env, "loge", logBaseE)

    def pow(args):
        exactAmountArgsCheck(args, "pow", 2)
        numbersOnlyArgCheck(args, "pow")
        result = math.pow(args[0]["val"], args[1]["val"])
        return parser.DblNode(result)
    put(env, "pow", pow)
    
    def notFunc(args):
        exactAmountArgsCheck(args, "not", 1)
        if (args[0] == env.poundF):
            return env.poundT
        return env.poundF
    put(env, "not", notFunc)

    def intToDbl(args):
        exactAmountArgsCheck(args, "integer->double", 1)
        numbersOnlyArgCheck(args, "integer->double")
        return parser.DblNode(args[0]["val"])
    put(env, "integer->double", intToDbl)

    def dblToInt(args):
        exactAmountArgsCheck(args, "double->integer", 1)
        numbersOnlyArgCheck(args, "double->integer")
        return parser.IntNode(int(args[0]["val"]))
    put(env, "double->integer", dblToInt)

    def isNull(args):
        exactAmountArgsCheck(args, "null?", 1)
        if (args[0] == env.empty):
            return env.poundT
        return env.poundF
    put(env, "null?", isNull)

    def andFunc(args):
        for arg in args:
            if arg[0]["val"] == env.poundF:
                return env.poundF
        return env.poundT
    put (env, "and", andFunc)

    def orFunc(args):
        for arg in args:
            if arg[0]["val"] != env.poundF:
                return env.poundT
        return env.pound
    put (env, "or", orFunc)

    pi = parser.DblNode(math.pi)
    env.put("pi", pi)

    e = parser.DblNode(math.e)
    env.put("e", e)

    tau = parser.DblNode(math.tau)
    env.put("tau", tau)

    infPos = parser.DblNode(math.inf)
    env.put("inf+", infPos)
        
    infNeg = parser.DblNode(-math.inf)
    env.put("inf-", infNeg)

    nan = parser.DblNode(math.nan)
    env.put("nan", nan)

def addListFuncs(env):
    """Add standard list functions to the given environment"""
    
    def car(args):
        exactAmountArgsCheck(args, "car", 1)
        typeChecker(args, "car", parser.CONS)
        return args[0]["car"]   
    put(env, "car", car)

    def cdr(args):
        exactAmountArgsCheck(args, "cdr", 1)
        typeChecker(args, "cdr", parser.CONS)
        return args[0]["cdr"]
    put(env, "cdr", cdr)

    def cons(args):
        exactAmountArgsCheck(args, "cons", 2)
        return parser.ConsNode(args[0], args[1])
    put(env, "cons", cons)

    def listFromValues(args):
        highestLevelCons = env.empty
        for arg in reversed(args):
            highestLevelCons = parser.ConsNode(arg, highestLevelCons)
        return highestLevelCons
    put(env, "list", listFromValues)

    def isList(args):
        exactAmountArgsCheck(args, "list?", 1)
        currentCdr = args[0]
        while currentCdr["type"] == parser.CONS:
            if currentCdr == env.empty:
                return env.poundT
            currentCdr = currentCdr["cdr"]
        return env.poundF
    put(env, "list?", isList)

    def setCar(args):
        exactAmountArgsCheck(args, "set-car!", 2)
        typeChecker(args[0], "set-car!", parser.CONS)
        args[0]["car"] = args[1]
        return None
    put(env, "set-car!", setCar)

    def setCdr(args):
        exactAmountArgsCheck(args, "set-cdr!", 2)
        typeChecker(args[0], "set-cdr!", parser.CONS)
        args[0]["cdr"] = args[1]
        return None
    put(env, "set-cdr!", setCdr)
    


def addStringFuncs(env):
    """Add standard string functions to the given environment"""
    def stringAppend(args):
        exactAmountArgsCheck(args, "string-append", 2)
        typeChecker(args, "string-append", parser.STR)
        string1 = args[0]["val"]
        string2 = args[1]["val"]
        result = string1 + string2
        return parser.StrNode(result)
    put(env, "string-append", stringAppend)
    
    def stringLength(args):
        exactAmountArgsCheck(args, "string-length", 1)
        typeChecker(args, "string-length", parser.STR)
        length = len(args[0]["val"])
        result = parser.IntNode(length)
        return result
    put(env, "string-length", stringLength)
    
    def substring(args):
        exactAmountArgsCheck(args, "substring", 3)
        typeChecker(args[0], "substring", parser.STR)
        typeChecker(args[1:3], "substring", parser.INT)
        original = args[0]["val"]
        length = len(original)
        startIndex = args[1]["val"]
        endIndex = args[2]["val"]
        if startIndex < 0 or startIndex > endIndex or endIndex > length:
            raise Exception("substring given invalid indices")
        result = original[startIndex:endIndex]
        return parser.StrNode(result)
    put(env, "substring", substring)

    def isString(args):
        exactAmountArgsCheck(args, "string?", 1)
        if args[0]["type"] == parser.STR:
            return env.poundT
        return env.poundF
    put(env, "string?", isString)

    def stringRef(args):
        exactAmountArgsCheck(args, "string-ref", 2)
        typeChecker(args[0], "string-ref", parser.STR)
        typeChecker(args[1], "string-ref", parser.INT)
        index = args[1]["val"]
        result = args[0]["val"][index]
        return parser.CharNode(result)
    put(env, "string-ref", stringRef)
    
    def stringEqual(args):
        exactAmountArgsCheck(args, "string-equal?", 2)
        typeChecker(args, "string-equal?", parser.STR)
        string1 = args[0]["val"]
        string2 = args[1]["val"]
        if string1 == string2:
            return env.poundT
        return env.poundF
    put(env, "string-equal?", stringEqual)

    def stringFromChars(args):
        typeChecker(args, "string", parser.CHAR)
        value = ""
        for arg in args:
            value += arg["val"]
        result = parser.StrNode(value)
        return result
    put(env, "string", stringFromChars)


def addVectorFuncs(env):
    """Add standard vector functions to the given environment"""
    def vectorLength(args):
        exactAmountArgsCheck(args, "vector-length", 1)
        typeChecker(args[0], "vector-length", parser.VEC)
        length = len(args[0]["items"])
        return parser.IntNode(length)
    put(env, "vector-length", vectorLength)

    def vectorGet(args):
        exactAmountArgsCheck(args, "vector-get", 2)
        typeChecker(args[0], "vector-get", parser.VEC)
        typeChecker(args[1], "vector-get", parser.INT)
        index = args[1]["val"]
        result = args[0]["items"][index]
        return result
    put(env, "vector-get", vectorGet)

    def vectorSet(args):
        exactAmountArgsCheck(args, "vector-set!", 3)
        typeChecker(args[0], "vector-set!", parser.VEC)
        typeChecker(args[1], "vector-set!", parser.INT)
        if args[2] is None:
            raise Exception("vector expects expression arguments only")
        index = args[1]["val"]
        args[0]["items"][index] = args[2]
        return None
    put(env, "vector-set!", vectorSet)

    def vector(args):
        for arg in args:
            if arg is None:
                raise Exception("vector expects expression arguments only")
        return parser.VecNode(args)
    put(env, "vector", vector)

    def isVector(args):
        exactAmountArgsCheck(args, "vector?", 1)
        if args[0]["type"] == parser.VEC:
            return env.poundT
        return env.poundF
    put(env, "vector?", isVector)

    def makeVector(args):
        exactAmountArgsCheck(args, "make-vector", 1)
        typeChecker(args, "make-vector", parser.INT)
        items = [env.poundF] * args[0]["val"]
        return parser.VecNode(items)
    put(env, "make-vector", makeVector)

