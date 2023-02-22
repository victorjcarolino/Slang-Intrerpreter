package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.function.Function;
import java.util.ArrayList;

/** MyMap is a wrapper class around the function `map` */
public class MyMap<T> {
  /**
   * Apply `func` to every element in `list`, and return a list containing the
   * results
   * 
   * @param list The list of elements that should be passed to func
   * @param func The function to apply to each element in the list
   * @return A list of the results
   */
  List<T> map(List<T> list, Function<T, T> func) {
    ArrayList<T> outputList = new ArrayList<T>(); //create outputList to store mapping from the input list
    for(int i = 0; i < list.size(); i++) { // looping through all elements in list
        outputList.add(func.apply(list.get(i))); //adding the func'ed list element to the outputList
    }
    return outputList;
  }
}