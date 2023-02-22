package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.ArrayList;

/**
 * App is the entry point into our program. You are allowed to add fields and
 * methods to App. You may also add `import` statements.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("CSE 262 Project 1");
        // [CSE 262] You should write code here to help you test your
        // implementations


        System.out.println("\n-----Testing MyTree--------");
        String[] nodes = {"foo", "foobar", "bar", "pow", "wow", "apple", "mango", "cat", "dog"};
        MyTree<String> testTree = new MyTree<String>();
        for(int i=0; i<nodes.length; i++){
            testTree.insert(nodes[i]);
        }
        testTree.inorder((x)->(x+"!")); System.out.println();
        ArrayList<String> nodes2 = new ArrayList<String>();
        //testTree.clear();
        nodes2.add("pop"); nodes2.add("shop"); nodes2.add("hop"); nodes2.add("tree"); nodes2.add("bee");
        testTree.inslist(nodes2);
        testTree.preorder((x)->(x));



        System.out.println("\n\n-----Testing PrimeDivisors--------");
        PrimeDivisors testFact = new PrimeDivisors();
        List<Integer> factors = new ArrayList<Integer>();
        int number = 36;
        factors = testFact.computeDivisors(number);
        System.out.println("Factoring " + number);
        System.out.println(factors);



        System.out.println("\n------Testing ReadList (Integers)--------");
        ReadList<Integer> testRead = new ReadList<Integer>();
        List<Integer> vals = testRead.read((x)-> Integer.parseInt(x));
        System.out.println(vals.toString());



        System.out.println("\n-------Testing Map--------");
        MyMap<Integer> intsTestMap = new MyMap<Integer>();
        System.out.println("Original List: " + vals.toString());
        List<Integer> map1 = intsTestMap.map(vals, (x)->(2*x+1));
        System.out.println("*2+1 to each:  " + map1.toString());

        MyMap<String> strTestMap = new MyMap<String>();
        System.out.println("Original List: " + nodes2.toString());
        List<String> map2 = strTestMap.map(nodes2, (x)->("Dr. "+x));
        System.out.println("Adding 'Dr.':  " + map2.toString());



        System.out.println("\n-------Testing Map--------");
        MyReverse<Integer> intTestRev = new MyReverse<Integer>();
        MyReverse<String> strTestRev = new MyReverse<String>();
        System.out.println("Reversing mapped lists");
        System.out.println(intTestRev.reverse(map1));
        System.out.println(strTestRev.reverse(map2));

        System.out.println("\n---------test end---------");
    }
}
