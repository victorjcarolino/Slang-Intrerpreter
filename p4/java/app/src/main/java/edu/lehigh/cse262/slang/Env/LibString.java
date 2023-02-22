package edu.lehigh.cse262.slang.Env;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.IValue;
import edu.lehigh.cse262.slang.Parser.Nodes;

/**
 * The purpose of LibString is to implement all of the standard library
 * functions that we can do on Strings
 */
public class LibString {
    /**
     * Populate the provided `map` with a standard set of string functions
     */
    public static void populate(HashMap<String, IValue> map, Nodes.Bool poundT, Nodes.Bool poundF) {

        var stringAppend = new Nodes.BuiltInFunc("string-append", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "string-append", 2);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Str && args.get(1) instanceof Nodes.Str))      //check type of first argument
                throw new Exception("string-append expects String arguments");
            String string1 = ((Nodes.Str)args.get(0)).val;
            String string2 = ((Nodes.Str)args.get(1)).val;
            IValue result = new Nodes.Str(string1 + string2);
            return result;
        });
        map.put(stringAppend.name, stringAppend);

        var stringLength = new Nodes.BuiltInFunc("string-length", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "string-length", 1);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Str))      //check type of first argument
                throw new Exception("string-length expects String argument");
            int length = ((Nodes.Str)args.get(0)).val.length();
            IValue result = new Nodes.Int(length);
            return result;
        });
        map.put(stringLength.name, stringLength);

        var substring = new Nodes.BuiltInFunc("substring", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "substring", 3);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Str))      //check type of first argument
                throw new Exception("substring expects a String for its first argument");
            //check type of second and third arguments
            if (!(args.get(1) instanceof Nodes.Int && args.get(2) instanceof Nodes.Int))      
                throw new Exception("substring expects int arguments for indices");
            String original = ((Nodes.Str)args.get(0)).val;
            int length = original.length();
            int startIndex = ((Nodes.Int)args.get(1)).val;
            int endIndex = ((Nodes.Int)args.get(2)).val;
            if (startIndex < 0 || startIndex > endIndex || endIndex > length){
                throw new Exception("substring given invalid indices");
            }
            IValue result = new Nodes.Str(original.substring(startIndex, endIndex));
            return result;
        });
        map.put(substring.name, substring);

        var isString = new Nodes.BuiltInFunc("string?", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "string?", 1);     //check number of arguments
            if (args.get(0) instanceof Nodes.Str)    
                return poundT;
            return poundF;
        });
        map.put(isString.name, isString);

        var stringRef = new Nodes.BuiltInFunc("string-ref", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "string-ref", 2);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Str))      //check type of first argument
                throw new Exception("string-ref expects a String for its first argument");
            if (!(args.get(1) instanceof Nodes.Int))    //check type of second argument  
                throw new Exception("string-ref expects an int for its second argument");
            String original = ((Nodes.Str)args.get(0)).val;
            int length = original.length();
            int index = ((Nodes.Int)args.get(1)).val;
            if (index < 0 || index >= length){
                throw new Exception("string-ref given invalid indices");
            }
            IValue result = new Nodes.Char(original.charAt(index));
            return result;
        });
        map.put(stringRef.name, stringRef);
        
        var stringEqual = new Nodes.BuiltInFunc("string-equal?", (List<IValue> args) ->{
            LibHelpers.exactAmountOfArgsCheck(args, "string-equal?", 2);     //check number of arguments
            if (!(args.get(0) instanceof Nodes.Str && args.get(1) instanceof Nodes.Str))      //check type of arguments 
                throw new Exception("string-equal? expects String arguments");
            String string1 = ((Nodes.Str)args.get(0)).val;
            String string2 = ((Nodes.Str)args.get(1)).val;
            if (string1.equals(string2)){
                return poundT;
            }
            return poundF;
        });
        map.put(stringEqual.name, stringEqual);

        var stringFromChars = new Nodes.BuiltInFunc("string", (List<IValue> args) ->{
            //number of arguments could be 0
            String value = "";
            for(int i=0; i<args.size(); i++){
                if(!(args.get(i) instanceof Nodes.Char))
                    throw new Exception("string expects char arguments");
                value += ((Nodes.Char)args.get(i)).val; 
            }
            IValue result = new Nodes.Str(value);
            return result;
        });
        map.put(stringFromChars.name, stringFromChars);

    }
}
