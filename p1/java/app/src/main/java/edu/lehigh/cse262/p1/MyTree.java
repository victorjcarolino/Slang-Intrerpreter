package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.function.Function;

public class MyTree<T extends Comparable<T>> { //values have an order, must be Comparable

    private TreeNode root = null; 

    private class TreeNode{ //
        T value;
        TreeNode left; //left.value is less than value
        TreeNode right; //right.value is greater than or equal to value

        TreeNode(T val){
            value = val;
            left = right = null;
        }
    }

    /**
     * Insert a value into the tree
     * 
     * @param value The value to insert
     */
    void insert(T value) {
        // [CSE 262] Implement Me!
        if (root == null) // first node to be inserted
            root = new TreeNode(value);
        else {
            TreeNode parent, node;
            parent = null; node = root;
            while (node != null) { // looking for a leaf node
                parent = node;
                if(value.compareTo(node.value) < 0) { // checking to see if the element being added should go in the left subtree
                    node = node.left; 
                }
                else if (value.compareTo(node.value) >= 0) { // checking to see if the element being added should go in the right subtree
                    node = node.right; 
                }
            }
            if (value.compareTo(parent.value)< 0) 
                parent.left = new TreeNode(value);
            else
                parent.right = new TreeNode(value);
        }
    }

    /**
     *  Clear the tree 
     */
    void clear() {
        // [CSE 262] Implement Me!
        root = null;
    }

    /**
     * Insert all of the elements from some list `l` into the tree
     *
     * @param l The list of elements to insert into the tree
     */
    void inslist(List<T> l) {
        // [CSE 262] Implement Me!
        for(int i=0; i<l.size(); i++){ // iterate through elements in list l
            insert(l.get(i)); // insert current element to the tree
        }
    }

    /**
     * Perform an in-order traversal, applying `func` to every element that is
     * visited
     * 
     * @param func A function to apply to each item
     */
    void inorder(Function<T, T> func) {
        // [CSE 262] Implement Me!
        inorder(func, root); // recursive inorder method, starting at the root
    }

    /**
     * Helper method for recursive inorder
     * 
     * @param func function from the main inorder method
     * @param node current Node being traversed
     */
    private void inorder(Function<T,T> func, TreeNode node){
        if(node != null) {
            inorder(func, node.left); // recursive call to traverse left child
            node.value = func.apply(node.value); // applying func to the value at the current node and storing the output
            System.out.print(node.value + " ");
            inorder(func, node.right); //recursive call to traverse to the right child
        }
    }

    /**
     * Perform a pre-order traversal, applying `func` to every element that is
     * visited
     * 
     * @param func A function to apply to each item
     */
    void preorder(Function<T, T> func) {
        // [CSE 262] Implement Me!
        preorder(func, root); // recursive preorder method, starting at the root
    }

    /**
     * Helper method for recursive preorder
     * 
     * @param func function from the main preorder method
     * @param node current Node being traversed
     */
    private void preorder(Function<T,T> func, TreeNode node){
        if(node != null) { //base case is when node == null
            node.value = func.apply(node.value); // applying func to value at the current node and storing the output
            System.out.print(node.value + " ");
            preorder(func, node.left); // recursive call to traverse left child
            preorder(func, node.right); // recursive call to traverse right child
        }
    }
}