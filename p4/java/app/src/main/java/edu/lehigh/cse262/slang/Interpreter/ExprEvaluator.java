package edu.lehigh.cse262.slang.Interpreter;

import edu.lehigh.cse262.slang.Env.Env;
import edu.lehigh.cse262.slang.Parser.IAstVisitor;
import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

import java.util.List;
import java.util.ArrayList;

/**
 * ExprEvaluator evaluates an AST node. It is the heart of the evaluation
 * portion of our interpreter.
 */
public class ExprEvaluator implements IAstVisitor<IValue> {
    /** The environment in which to do the evaluation */
    private Env env;

    /** Construct an ExprEvaluator by providing an environment */
    public ExprEvaluator(Env env) {
        this.env = env;
    }

    /** Interpret an Identifier */
    @Override
    public IValue visitIdentifier(Nodes.Identifier expr) throws Exception {
        return env.get(expr.name);
    }

    /**
     * Interpret a Define special-form
     *
     * NB: it's OK for this to return null, because definitions aren't
     * expressions
     */
    @Override
    public IValue visitDefine(Nodes.Define expr) throws Exception {
        env.put(expr.identifier.name, expr.expression.visitValue(this));
        return null;
    }

    /** Interpret a Bool value */
    @Override
    public IValue visitBool(Nodes.Bool expr) throws Exception {
        return expr;
    }

    /** Interpret an Int value */
    @Override
    public IValue visitInt(Nodes.Int expr) throws Exception {
        return expr;
    }

    /** Interpret a Dbl value */
    @Override
    public IValue visitDbl(Nodes.Dbl expr) throws Exception {
        return expr;
    }

    /** Interpret a Lambda value */
    @Override
    public IValue visitLambdaVal(Nodes.LambdaVal expr) throws Exception {
        return expr; //returns itself because functions are first class
    }

    /**
     * Interpret a Lambda definition by creating a Lambda value from it in the
     * current environment
     */
    @Override
    public IValue visitLambdaDef(Nodes.LambdaDef expr) throws Exception {
        return new Nodes.LambdaVal(env, expr);  //returns a LambdaVal, which is an IValue
    }

    /** Interpret an If expression */
    @Override
    public IValue visitIf(Nodes.If expr) throws Exception {
        if(expr.cond.visitValue(this) != env.poundF){
            return expr.ifTrue.visitValue(this);
        }
        else{
            return expr.ifFalse.visitValue(this);
        }
    }

    /**
     * Interpret a set! special form. As with Define, this isn't an expression,
     * so it can return null
     */
    @Override
    public IValue visitSet(Nodes.Set expr) throws Exception {
        env.update(expr.identifier.name, expr.expression.visitValue(this));
        return null;
    }

    /** Interpret an And expression */
    @Override
    public IValue visitAnd(Nodes.And expr) throws Exception {
        for (Nodes.BaseNode ex : expr.expressions){
            if (ex.visitValue(this) == env.poundF)
                return env.poundF;
        }
        return env.poundT;
    }

    /** Interpret an Or expression */
    @Override
    public IValue visitOr(Nodes.Or expr) throws Exception {
        for (Nodes.BaseNode ex : expr.expressions){
            if (ex.visitValue(this) != env.poundF)
                return env.poundT;
        }
        return env.poundF;
    }

    /** Interpret a Begin expression */
    @Override
    public IValue visitBegin(Nodes.Begin expr) throws Exception {
        List<Nodes.BaseNode> list = expr.expressions;
        for (int i=0; i<list.size()-1; i++){
            list.get(i).visitValue(this);
        }
        return list.get(list.size()-1).visitValue(this);

    }

    /** Interpret a "not special form" expression */
    @Override
    public IValue visitApply(Nodes.Apply expr) throws Exception {
        IValue function = expr.expressions.get(0).visitValue(this);
        List<IValue> argValues = new ArrayList<IValue>();
        for(int i=1; i<expr.expressions.size(); i++) {
            Nodes.BaseNode argNode = expr.expressions.get(i);
            argValues.add(argNode.visitValue(this));
        }
        if(function instanceof Nodes.BuiltInFunc){
            return ((Nodes.BuiltInFunc)function).func.execute(argValues);
        }
        else if(function instanceof Nodes.LambdaVal){
            Nodes.LambdaVal func = (Nodes.LambdaVal)function;
            //check #arg specified == #args given
            int expected = func.lambda.formals.size();
            int given = argValues.size();
            if(expected != given)  
                throw new Exception(expected +" arguments expected but " + given + " provided.");
            //Create inner environment
            Env inner = Env.makeInner(func.env);
            //Add arguments to the new environment
            for(int i=0; i<argValues.size(); i++){
                inner.put(func.lambda.formals.get(i).name, argValues.get(i));
            }
            List<Nodes.BaseNode> list = func.lambda.body;
            ExprEvaluator lambdaEvaluator = new ExprEvaluator(inner);
            for (int i=0; i<list.size()-1; i++){
                list.get(i).visitValue(lambdaEvaluator);
            }
            return list.get(list.size()-1).visitValue(lambdaEvaluator);
        }
        else {
            throw new Exception("Can only Apply procedures");
        }
    }

    /** Interpret a Cons value */
    @Override
    public IValue visitCons(Nodes.Cons expr) throws Exception {
        return expr;
    }

    /** Interpret a Vec value */
    @Override
    public IValue visitVec(Nodes.Vec expr) throws Exception {
        return expr;
    }

    /** Interpret a Symbol value */
    @Override
    public IValue visitSymbol(Nodes.Symbol expr) throws Exception {
        return expr;
    }

    /** Interpret a Quote expression */
    @Override
    public IValue visitQuote(Nodes.Quote expr) throws Exception {
        return expr.datum;
    }

    /** Interpret a quoted datum expression */
    @Override
    public IValue visitTick(Nodes.Tick expr) throws Exception {
        return expr.datum;
    }

    /** Interpret a Char value */
    @Override
    public IValue visitChar(Nodes.Char expr) throws Exception {
        return expr;
    }

    /** Interpret a Str value */
    @Override
    public IValue visitStr(Nodes.Str expr) throws Exception {
        return expr;
    }

    /** Interpret a Built-In Function value */
    @Override
    public IValue visitBuiltInFunc(Nodes.BuiltInFunc expr) throws Exception {
        return expr;    //returns itself because functions are first class
    }
    
    /** Interpret a Cons expression */
    @Override
    public IValue visitCond(Nodes.Cond expr) throws Exception {
        for(Nodes.Cond.Condition con : expr.conditions){
            if(con.test.visitValue(this) != env.poundF){
                List<Nodes.BaseNode> list = con.expressions;
                for (int i=0; i<list.size()-1; i++){
                    list.get(i).visitValue(this);
                }
                return list.get(list.size()-1).visitValue(this); 
            }
        }
        return null;
    }
}