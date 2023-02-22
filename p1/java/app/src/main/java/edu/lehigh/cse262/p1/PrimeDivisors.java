package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.ArrayList;

/** PrimeDivisors is a wrapper class around the function `computeDivisors` */
public class PrimeDivisors {
  /**
   * Compute the prime divisors of `value` and return them as a list
   *
   * @param value The value whose prime divisors are to be computed
   * @return A list of the prime divisors of `value`
   */
  List<Integer> computeDivisors(int value) {
    // [CSE 262] Implement Me!
    ArrayList<Integer> primeFactors = new ArrayList<Integer>(); //list of the divisors, or prime factors, of value
    boolean factorFound = false; //track if a divisor of value is found; if not, value is prime.

    for(int i = 2; i <= value/2; i ++){
        //i is a factor of value:
        if(value%i == 0) { // check to see if int value is evenly divisible by i
            primeFactors.addAll(computeDivisors(i)); //add prime factors of i to the list
            primeFactors.addAll(computeDivisors(value/i)); // add prime factors of value/i to the list
            factorFound = true; //value is not prime
            break; //exit for loop, two factors (i, value/i) have been found
        }
    }
    //base case (value is prime)
    if(!factorFound)
        primeFactors.add(value); //add value to the list of its divisors (value is prime and is its own only divisor)
    return primeFactors;
  }

}

