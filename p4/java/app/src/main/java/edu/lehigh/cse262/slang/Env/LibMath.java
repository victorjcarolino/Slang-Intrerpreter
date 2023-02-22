package edu.lehigh.cse262.slang.Env;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * The purpose of LibMath is to implement all of the standard library functions
 * that we can do on numbers (Integer or Double)
 */
public class LibMath {
    /**
     * Populate the provided `map` with a standard set of mathematical functions
     */
    public static void populate(HashMap<String, IValue> map, Nodes.Bool poundT, Nodes.Bool poundF) {
        // As a starting point, let's go ahead and put the addition function
        // into the map. This will make it **much** easier to test `apply`, and
        // should provide some useful guidance for making other functions.
        //
        // Note that this code is **very** tedious. Making some helper
        // functions would probably be wise, but it's up to you to figure out
        // how.

        // var add = new Nodes.BuiltInFunc("+", (List<IValue> args) -> {
        //     // Type checking: make sure we only have int and dbl arguments. We also will use
        //     // this to know if we should be returning an Int or a Dbl
        //     int intCount = 0;
        //     int dblCount = 0;
        //     for (var arg : args) {
        //         if (arg instanceof Nodes.Int)
        //             intCount++;
        //         if (arg instanceof Nodes.Dbl)
        //             dblCount++;
        //     }
        //     if (args.size() > (intCount + dblCount))
        //         throw new Exception("+ can only handle Int and Dbl arguments");
        //     // Semantic analysis: make sure there are arguments!
        //     if (args.size() == 0)
        //         throw new Exception("+ expects at least one argument");
        //     // Compute, making sure to know the return type
        //     if (dblCount > 0) {
        //         double result = 0;
        //         for (var arg : args) {
        //             if (arg instanceof Nodes.Int)
        //                 result += ((Nodes.Int) arg).val;
        //             else
        //                 result += ((Nodes.Dbl) arg).val;
        //         }
        //         return new Nodes.Dbl(result);
        //     } else {
        //         int result = 0;
        //         for (var arg : args) {
        //             result += ((Nodes.Int) arg).val;
        //         }
        //         return new Nodes.Int(result);
        //     }
        // });
        
        var add = new Nodes.BuiltInFunc("+", (List<IValue> args) ->{
            //check number of arguments
            LibHelpers.oneOrMoreArgsCheck(args, "+");
            //check type of arguments
            LibHelpers.numbersOnlyArgCheck(args, "+");
            double result = 0;
            //add the element
            for (var arg : args) {
                result += numVal(arg);
            }
            //returning double or int?
            if (LibHelpers.checkForDoubleArg(args)){
                return new Nodes.Dbl(result);
            }
            else {
                return new Nodes.Int((int)result);
            }
        });
        map.put(add.name, add);

        var subtract = new Nodes.BuiltInFunc("-", (List<IValue> args) ->{
            LibHelpers.oneOrMoreArgsCheck(args, "-");   //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "-");   //check type of arguments
            double result = 0;
            //add the first element
            var arg = args.get(0);
            result += numVal(arg);
            //subtract the subsequent elements
            for(int i=1; i<args.size(); i++){
                arg = args.get(i);
                result -= numVal(arg);
            }
            //returning double or int?
            if (LibHelpers.checkForDoubleArg(args)){
                return new Nodes.Dbl(result);
            }
            else {
                return new Nodes.Int((int)result);
            }
        });
        map.put(subtract.name, subtract);

        var multiply = new Nodes.BuiltInFunc("*", (List<IValue> args) ->{
            //check number of arguments
            LibHelpers.oneOrMoreArgsCheck(args, "*");
            //check type of arguments
            LibHelpers.numbersOnlyArgCheck(args, "*");
            double result = 1;
            for (var arg : args) {
                result *= numVal(arg);
            }
            //returning double or int?
            if (LibHelpers.checkForDoubleArg(args)){
                return new Nodes.Dbl(result);
            }
            else {
                return new Nodes.Int((int)result);
            }
        });
        map.put(multiply.name, multiply);

        var divide = new Nodes.BuiltInFunc("/", (List<IValue> args) ->{
            LibHelpers.oneOrMoreArgsCheck(args, "/");   //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "/");   //check type of arguments
            double result = 1;
            //multiply the first element
            var arg = args.get(0);
            result *= numVal(arg);
            //divide the subsequent elements
            for(int i=1; i<args.size(); i++){
                arg = args.get(i);
                result /= numVal(arg);
            }
            //returning double or int?
            if (result != (int)result || LibHelpers.checkForDoubleArg(args)){
                return new Nodes.Dbl(result);
            }
            else {
                return new Nodes.Int((int)result);
            }
        });
        map.put(divide.name, divide);

        var modulus = new Nodes.BuiltInFunc("%", (List<IValue> args) ->{
            LibHelpers.oneOrMoreArgsCheck(args, "%");   //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "%");   //check type of arguments
            int result = 1;
            //multiply the first element
            var arg = args.get(0);
            result *= (int)numVal(arg);
            //modulo the subsequent elements
            for(int i=1; i<args.size(); i++){
                arg = args.get(i);
                result %= (int)numVal(arg);
            }
            return new Nodes.Int((int)result);
        });
        map.put(modulus.name, modulus);

        var equals = new Nodes.BuiltInFunc("==", (List<IValue> args) ->{
            LibHelpers.oneOrMoreArgsCheck(args, "==");  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "==");    //check type of arguments
            if (args.size() == 1) {
                return poundT;
            }
            for (int i = 0; i < args.size()-1; i++){
                if (numVal(args.get(i)) != numVal(args.get(i+1)))
                    return poundF;
            }
            return poundT;
        });
        map.put(equals.name, equals);

        var lessThan = new Nodes.BuiltInFunc("<", (List<IValue> args) -> {
            LibHelpers.oneOrMoreArgsCheck(args, "<");  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "<");    //check type of arguments
            if (args.size() == 1) {
                return poundT;
            }
            for (int i = 0; i < args.size()-1; i++){
                if (numVal(args.get(i)) >= numVal(args.get(i+1)))
                    return poundF;
            }
            return poundT;
        });
        map.put(lessThan.name, lessThan);

        var lessThanOrEqual = new Nodes.BuiltInFunc("<=", (List<IValue> args) -> {
            LibHelpers.oneOrMoreArgsCheck(args, "<=");  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "<=");    //check type of arguments
            if (args.size() == 1) {
                return poundT;
            }
            for (int i = 0; i < args.size()-1; i++){
                if (numVal(args.get(i)) > numVal(args.get(i+1)))
                    return poundF;
            }
            return poundT;
        });
        map.put(lessThanOrEqual.name, lessThanOrEqual);

        var greaterThan = new Nodes.BuiltInFunc(">", (List<IValue> args) -> {
            LibHelpers.oneOrMoreArgsCheck(args, ">");  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, ">");    //check type of arguments
            if (args.size() == 1) {
                return poundT;
            }
            for (int i = 0; i < args.size()-1; i++){
                if (numVal(args.get(i)) <= numVal(args.get(i+1)))
                    return poundF;
            }
            return poundT;
        });
        map.put(greaterThan.name, greaterThan);

        var greaterThanOrEqual = new Nodes.BuiltInFunc(">=", (List<IValue> args) -> {
            LibHelpers.oneOrMoreArgsCheck(args, ">=");  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, ">=");    //check type of arguments
            if (args.size() == 1) {
                return poundT;
            }
            for (int i = 0; i < args.size()-1; i++){
                if (numVal(args.get(i)) < numVal(args.get(i+1)))
                    return poundF;
            }
            return poundT;
        });
        map.put(greaterThanOrEqual.name, greaterThanOrEqual);

        var absoluteValue = new Nodes.BuiltInFunc("abs", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "abs", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "abs");      //check type of arguments
            double result =  Math.abs(numVal(args.get(0)));
            //returning double or int?
            if (LibHelpers.checkForDoubleArg(args)){
                return new Nodes.Dbl(result);
            }
            else {
                return new Nodes.Int((int)result);
            }
        });
        map.put(absoluteValue.name, absoluteValue);

        var squareRoot = new Nodes.BuiltInFunc("sqrt", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "sqrt", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "sqrt");      //check type of arguments
            double result =  Math.sqrt(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(squareRoot.name, squareRoot);

        var arccosine = new Nodes.BuiltInFunc("acos", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "acos", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "acos");      //check type of arguments
            double result =  Math.acos(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(arccosine.name, arccosine);

        var arcsine = new Nodes.BuiltInFunc("asin", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "asin", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "asin");      //check type of arguments
            double result =  Math.asin(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(arcsine.name, arcsine);

        var arctangent = new Nodes.BuiltInFunc("atan", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "atan", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "atan");      //check type of arguments
            double result =  Math.atan(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(arctangent.name, arctangent);

        var cosine = new Nodes.BuiltInFunc("cos", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "cos", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "cos");      //check type of arguments
            double result =  Math.cos(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(cosine.name, cosine);

        var hyperbolicCosine = new Nodes.BuiltInFunc("cosh", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "cosh", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "cosh");      //check type of arguments
            double result =  Math.cosh(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(hyperbolicCosine.name, hyperbolicCosine);

        var sine = new Nodes.BuiltInFunc("sin", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "sin", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "sin");      //check type of arguments
            double result =  Math.sin(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(sine.name, sine);

        var hyperbolicSine = new Nodes.BuiltInFunc("sinh", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "sinh", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "sinh");      //check type of arguments
            double result =  Math.sinh(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(hyperbolicSine.name, hyperbolicSine);

        var tangent = new Nodes.BuiltInFunc("tan", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "tan", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "tan");      //check type of arguments
            double result =  Math.tan(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(tangent.name, tangent);

        var hyperbolicTangent = new Nodes.BuiltInFunc("tanh", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "tanh", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "tanh");      //check type of arguments
            double result =  Math.tanh(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(hyperbolicTangent.name, hyperbolicTangent);

        var isInteger = new Nodes.BuiltInFunc("integer?", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "integer?", 1);  //check number of arguments
            if (args.get(0) instanceof Nodes.Int)
                return poundT;
            return poundF;
        });
        map.put(isInteger.name, isInteger);

        var isDouble = new Nodes.BuiltInFunc("double?", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "double?", 1);  //check number of arguments
            if (args.get(0) instanceof Nodes.Dbl)
                return poundT;
            return poundF;
        });
        map.put(isDouble.name, isDouble);

        var isNumber = new Nodes.BuiltInFunc("number?", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "number?", 1);  //check number of arguments
            if (args.get(0) instanceof Nodes.Int || args.get(0) instanceof Nodes.Dbl)
                return poundT;
            return poundF;
        });
        map.put(isNumber.name, isNumber);

        var isSymbol = new Nodes.BuiltInFunc("symbol?", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "symbol?", 1);  //check number of arguments
            if (args.get(0) instanceof Nodes.Symbol)
                return poundT;
            return poundF;
        });
        map.put(isSymbol.name, isSymbol);

       var isProcedure = new Nodes.BuiltInFunc("procedure?", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "procedure?", 1);  //check number of arguments
            if (args.get(0) instanceof Nodes.BuiltInFunc || args.get(0) instanceof Nodes.LambdaVal)
                return poundT;
            return poundF;
        });
        map.put(isProcedure.name, isProcedure); 

        var logBase10 = new Nodes.BuiltInFunc("log10", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "log10", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "log10");      //check type of arguments
            double result =  Math.log10(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(logBase10.name, logBase10);
       
        var logBaseE = new Nodes.BuiltInFunc("loge", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "loge", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "loge");      //check type of arguments
            double result =  Math.log(numVal(args.get(0)));
            return new Nodes.Dbl(result);
        });
        map.put(logBaseE.name, logBaseE);

        var pow = new Nodes.BuiltInFunc("pow", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "pow", 2);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "pow");      //check type of arguments
            double result =  Math.pow(numVal(args.get(0)), numVal(args.get(1)));
            return new Nodes.Dbl(result);
        });
        map.put(pow.name, pow);

        var not = new Nodes.BuiltInFunc("not", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "not", 1);  //check number of arguments          
            if(args.get(0) == poundF){
                return poundT;
            }
            return poundF;
        });
        map.put(not.name, not);

        var intToDbl = new Nodes.BuiltInFunc("integer->double", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "integer->double", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "integer->double");      //check type of arguments
            return new Nodes.Dbl(numVal(args.get(0)));
        });
        map.put(intToDbl.name, intToDbl);

        var DblToInt = new Nodes.BuiltInFunc("double->integer", (List<IValue> args) -> {
            LibHelpers.exactAmountOfArgsCheck(args, "double->integer", 1);  //check number of arguments
            LibHelpers.numbersOnlyArgCheck(args, "double->integer");      //check type of arguments
            return new Nodes.Int((int)numVal(args.get(0)));
        });
        map.put(DblToInt.name, DblToInt);
        
        var isNull = new Nodes.BuiltInFunc("null?", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "null?", 1);  //check number of arguments          
            if(args.get(0) instanceof Nodes.Cons){
                if(((Nodes.Cons)args.get(0)).car == null)
                    return poundT;
            }
            return poundF;
        });
        map.put(isNull.name, isNull);

        var and = new Nodes.BuiltInFunc("and", (List<IValue> args) ->{
            for(IValue arg : args){
                if(arg == poundF)
                    return poundF;
            }
            return poundT;
        });
        map.put(and.name, and);

        var or = new Nodes.BuiltInFunc("or", (List<IValue> args) ->{
            for(IValue arg : args){
                if(arg != poundF)
                    return poundT;
            }
            return poundF;
        });
        map.put(or.name, or);

        var pi = new Nodes.Dbl(Math.PI);
        map.put("pi", pi);
        
        var e = new Nodes.Dbl(Math.E);
        map.put("e", e);

        var tau = new Nodes.Dbl(2*Math.PI);
        map.put("tau", tau);
    
        var infPos = new Nodes.Dbl(Double.POSITIVE_INFINITY);
        map.put("inf+", infPos);
        
        var infNeg = new Nodes.Dbl(Double.NEGATIVE_INFINITY);
        map.put("inf-", infNeg);

        var notANumber = new Nodes.Dbl(Double.NaN);
        map.put("nan", notANumber);
    }

    private static double numVal(IValue arg) throws Exception {
        if (arg instanceof Nodes.Int)
            return ((Nodes.Int) arg).val;
        else if (arg instanceof Nodes.Dbl)
            return ((Nodes.Dbl) arg).val;
        else
            throw new Exception("Math operation on non-number IValue");
    }


}
