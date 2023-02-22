package edu.lehigh.cse262.slang.Env;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * The purpose of LibVector is to implement all of the standard library
 * functions that we can do on vectors
 */
public class LibVector {
    /**
     * Populate the provided `map` with a standard set of vector functions
     */
    public static void populate(HashMap<String, IValue> map, Nodes.Bool poundT, Nodes.Bool poundF) {

        var vectorLength = new Nodes.BuiltInFunc("vector-length", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "vector-length", 1);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Vec))      //check type of first argument
                throw new Exception("vector-length expects a vector argument");
            int result = ((Nodes.Vec)args.get(0)).items.length;
            return new Nodes.Int(result);
        });
        map.put(vectorLength.name, vectorLength);

        var vectorGet = new Nodes.BuiltInFunc("vector-get", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "vector-get", 2);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Vec))      //check type of first argument
                throw new Exception("vector-get expects a vector for its first argument");
            if (!(args.get(1) instanceof Nodes.Int))      //check type of second argument
                throw new Exception("vector-get expects an integer for its second argument");
            int index = ((Nodes.Int)args.get(1)).val;
            //check that index is valid
            if(index < 0) 
                throw new Exception("vector-get expects a nonnegative index");
            if(index >= ((Nodes.Vec)args.get(0)).items.length)
                throw new Exception("vector-get: invalid index");
            IValue result = ((Nodes.Vec)args.get(0)).items[index];
            return result;
        });
        map.put(vectorGet.name, vectorGet);

        var vectorSet = new Nodes.BuiltInFunc("vector-set!", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "vector-set!", 3);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Vec))      //check type of first argument
                throw new Exception("vector-set! expects a vector for its first argument");
            if (!(args.get(1) instanceof Nodes.Int))     //check type of second argument
                throw new Exception("vector-set! expects an integer for its second argument");
            int index = ((Nodes.Int)args.get(1)).val;
            //check that index is valid
            if(index < 0) 
                throw new Exception("vector-set! expects a nonnegative index");
            if(index >= ((Nodes.Vec)args.get(0)).items.length)
                throw new Exception("vector-set!: invalid index");
            ((Nodes.Vec)args.get(0)).items[index] = args.get(2);
            return null;
        });
        map.put(vectorSet.name, vectorSet);

        var vector = new Nodes.BuiltInFunc("vector", (List<IValue> args) ->{
            //number of arguments could be 0
            ArrayList<IValue> items = new ArrayList<IValue>();
            for(int i=0; i<args.size(); i++){
                items.add(args.get(i));                
            }
            return new Nodes.Vec(items);
        });
        map.put(vector.name, vector);

        var isVector = new Nodes.BuiltInFunc("vector?", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "vector?", 1);     //check number of arguments
            if (args.get(0) instanceof Nodes.Vec)      //check is argument is a vector
                return poundT;
            return poundF;
        });
        map.put(isVector.name, isVector);

        var makeVector = new Nodes.BuiltInFunc("make-vector", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "make-vector", 1);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Int))      //check argument is an integer
                throw new Exception("make-vector expects an integer argument");
            int size = ((Nodes.Int)args.get(0)).val;
            if(size < 0) 
                throw new Exception("make-vector expects a nonnegative size");
            ArrayList<IValue> items = new ArrayList<IValue>();
            for(int i=0; i<size; i++){
                items.add(poundF);
            }
            return new Nodes.Vec(items);
        });
        map.put(makeVector.name, makeVector);
    }
}
