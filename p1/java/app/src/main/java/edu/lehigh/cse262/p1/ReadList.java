package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * ReadList is a wrapper class around the function `read`
 */
public class ReadList<T>{

  Scanner scan = new Scanner(System.in);
  public interface Converter<T> {
    public T convert(String s);
  }
  
  /**
   * Read from stdin until EOF is encountered, and put all of the values into a
   * list. The order in the list should be the reverse of the order in which the
   * elements were added.
   * 
   * @return A list with the values that were read
   */
  List<T> read(Converter<T> converter) {
    // [CSE 262] Implement Me!
    ArrayList<T> readingList = new ArrayList<T>(); //create readingList to store the values in order of reading
    while(scan.hasNext()){ //True if there is another value to be read, False if EOF is reached
        readingList.add(converter.convert(scan.next()));
    }
    ArrayList<T> readingListReverse = new ArrayList<T>(); //create readingListReverse to store the values in reverse order of reading.
    for(int i=readingList.size()-1; i>=0; i--){
        readingListReverse.add(readingList.get(i));
    }
    return readingListReverse;
  }
}