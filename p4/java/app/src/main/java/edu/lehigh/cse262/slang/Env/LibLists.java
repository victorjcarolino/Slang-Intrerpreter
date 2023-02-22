package edu.lehigh.cse262.slang.Env;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * The purpose of LibLists is to implement all of the standard library functions
 * that we can do on Cons nodes
 */
public class LibLists {
    /**
     * Populate the provided `map` with a standard set of list functions
     */
    public static void populate(HashMap<String, IValue> map, Nodes.Bool poundT, Nodes.Bool poundF, Nodes.Cons empty) {

        var car = new Nodes.BuiltInFunc("car", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "car", 1);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Cons))      //check type of first argument
                throw new Exception("vector-length expects a Cons argument");
            IValue result = ((Nodes.Cons)args.get(0)).car;
            return result;
        });
        map.put(car.name, car);

        var cdr = new Nodes.BuiltInFunc("cdr", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "cdr", 1);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Cons))      //check type of first argument
                throw new Exception("vector-length expects a Cons argument");
            IValue result = ((Nodes.Cons)args.get(0)).cdr;
            return result;
        });
        map.put(cdr.name, cdr);

        var cons = new Nodes.BuiltInFunc("cons", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "cons", 2);     //check number of arguments
            Nodes.Cons result = new Nodes.Cons(args.get(0), args.get(1));
            return result;
        });
        map.put(cons.name, cons);

        var list = new Nodes.BuiltInFunc("list", (List<IValue> args) ->{
            //list can take any amount of arguments
            if(args.size() == 0)    //if 0 arguments are given, return the empty list
                return empty;
            Nodes.Cons result = new Nodes.Cons(args, empty);
            return result;
        });
        map.put(list.name, list);

        var isList = new Nodes.BuiltInFunc("list?", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "list?", 1);     //check number of arguments
            IValue currentCdr = args.get(0);
            while (currentCdr instanceof Nodes.Cons){
                if(currentCdr == empty){
                    return poundT;
                }
                currentCdr = ((Nodes.Cons)currentCdr).cdr;
            }
            return poundF;
        });
        map.put(isList.name, isList);

        var setCar = new Nodes.BuiltInFunc("set-car!", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "set-car!", 2);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Cons))      //check type of first argument
                throw new Exception("set-car! expects a Cons for its first argument");
            ((Nodes.Cons)args.get(0)).car = args.get(1);
            return null;
        });
        map.put(setCar.name, setCar);

        var setCdr = new Nodes.BuiltInFunc("set-cdr!", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "set-cdr!", 2);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Cons))      //check type of first argument
                throw new Exception("set-cdr! expects a Cons for its first argument");
            ((Nodes.Cons)args.get(0)).cdr = args.get(1);
            return null;
        });
        map.put(setCdr.name, setCdr);
    }
}
