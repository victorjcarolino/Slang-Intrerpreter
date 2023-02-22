package edu.lehigh.cse262.slang.Env;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;
import java.util.List;

/**
 * LibHelpers has a few static methods that are useful when defining standard
 * library functions.
 *
 * [CSE 262] You may decide not to put any common code in this file. If not,
 * that's fine.
 */
public class LibHelpers {
    /** This is like Function<>, except it can throw... */
    @FunctionalInterface
    public static interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    /**
     * Checks if a list of arguments has a size of at least one.
     * If not, throws an Exception.
     */
    public static void oneOrMoreArgsCheck(List<IValue> args, String functionName) throws Exception{
        if (args.size() == 0)
            throw new Exception("'" + functionName + "' expects at least one argument");
    }

    /**
     * Checks if a list of arguments has a specified size.
     * If not, throws an Exception.
     */
    public static void exactAmountOfArgsCheck(List<IValue> args, String functionName, int amount) throws Exception{
        if(args.size() != amount){
            throw new Exception("'" + functionName + "' expects exactly " + amount + "argument(s)");
        }
    }

    /**
     * Checks if a list of arguments contains only numbers (in the form of Nodes.Int or Nodes.Dbl)
     * If not, throws an Exception.
     */
    public static void numbersOnlyArgCheck(List<IValue> args, String functionName) throws Exception{
       for (IValue arg : args)
       {
            if (!(arg instanceof Nodes.Dbl || arg instanceof Nodes.Int))
                throw new Exception("'" + functionName + "' expects only Int and Dbl arguments.");    
       }
    }

    /**
     * Checks if a list of arguments contains at least one Nodes.Dbl.
     * If it does, return true. Otherwise return false.
     * Checking for numbers must still be done separately.
     */
    public static boolean checkForDoubleArg(List<IValue> args){
        for (IValue arg : args)
        {
            if (arg instanceof Nodes.Dbl)
                return true;
        }
        return false;
    }

}