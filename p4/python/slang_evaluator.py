import slang_parser as parser
import slang_env as environment

def evaluate(expr, env):
    """Evaluate is responsible for visiting an expression and producing a
    value"""
    poundT = env.poundT
    poundF = env.poundF
   
    type = expr["type"] 
    #IValue equivalents
    IValues = [parser.BOOL, parser.BUILTIN, parser.CHAR, parser.CONS, parser.DBL, parser.INT, parser.LAMBDAVAL, parser.STR, parser.SYMBOL, parser.VEC]
    if type in IValues:
        return expr

    elif type == parser.IDENTIFIER:
        return env.get(expr["name"])
    
    elif type == parser.DEFINE:
        env.put(expr["id"]["name"], evaluate(expr["expr"], env))
        return None

    elif type == parser.LAMBDADEF:
        return parser.LambdaValNode(env, expr)

    elif type == parser.IF:
        if evaluate(expr["cond"], env) != poundF:
            return evaluate(expr["true"], env)
        return evaluate(expr["false"], env)
    
    elif type == parser.SET:
        env.update(expr["id"]["name"], evaluate(expr["expr"], env))
        return None
    
    elif type == parser.AND:
        for arg in expr["exprs"]:
            if evaluate(arg, env) == poundF:
                return poundF
        return poundT
    
    elif type == parser.OR:
        for arg in expr["exprs"]:
            if evaluate(arg, env) != poundF:
                return poundT
        return poundF
    
    elif type == parser.BEGIN:
        list = expr["exprs"]
        for e in list[0:len(list)-1]:
            evaluate(e, env)
        return evaluate(list[len(list)-1], env)
    
    elif type == parser.APPLY:
        function = evaluate(expr["exprs"][0], env)
        preArguments = expr["exprs"][1:]
        arguments = []
        for arg in preArguments:
            arguments.append(evaluate(arg, env))
        if function["type"] == parser.BUILTIN:
            return function["func"](arguments)
        elif function["type"] == parser.LAMBDAVAL:
            lambdaDef = function["lambda"]
            expected = len(lambdaDef["formals"])
            given = len(arguments)
            if expected != given:
                raise Exception("Given unexpected number of arguments in APPLY")
            inner = environment.makeInnerEnv(function["env"])
            for (formal, arg) in zip(lambdaDef["formals"], arguments):
                inner.put(formal["name"], arg)
            body = lambdaDef["exprs"]
            for bodyExpr in body[0:len(body)-1]:
                evaluate(bodyExpr, inner)
            return evaluate(body[len(body)-1], inner)
        else:
            raise Exception("Can only Apply a procedure")

    elif type == parser.QUOTE:
        return expr["datum"]
    
    elif type == parser.TICK:
        return expr["datum"]

    elif type == parser.COND:
        #pair is a 'pair' of test condition BoolNode and a list of expressions
        for pair in expr["conditions"]:
            if evaluate(pair["test"], env) != poundF:
                list = pair["exprs"]
                for e in list[0:len(list)-1]:
                    evaluate(e, env)
                return evaluate(list[len(list)-1], env)
        return None

    raise Exception("Invalid Node type")
