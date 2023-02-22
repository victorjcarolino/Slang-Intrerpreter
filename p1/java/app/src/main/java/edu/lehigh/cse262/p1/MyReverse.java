package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

/** MyReverse is a wrapper class around the function `reverse` */

// has to work with an iterator and not .get()

public class MyReverse<T> {
  /**
   * Return a list that has all of the elements of `in`, but in reverse order
   * 
   * @param in The list to reverse
   * @return A list that is the reverse of `in`
   */
  List<T> reverse(List<T> in) {
    List<T> reversedList = new ArrayList<T>(); //initialize an empty ArrayList<T>
    ListIterator<T> iter = in.listIterator(in.size());

    while(iter.hasPrevious()){ // loop through all elements of "in" in reverse order
        reversedList.add(iter.previous()); // append all elements of "in" to "reversedList" in reverse order
    }
    return reversedList; //return the new list
  }
}
