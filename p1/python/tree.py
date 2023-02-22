# tree: A binary tree, implemented as a class
#
# The tree should support the following methods:
# - ins(x)      - Insert the value x into the tree
# - clear()     - Reset the tree to empty
# - inslist(l)  - Insert all the elements from list `l` into the tree
# - display()   - Use `display` to print the tree
# - inorder(f)  - Traverse the tree using an in-order traversal, applying
#                 function `f` to the value in each non-null position
# - preorder(f) - Traverse the tree using a pre-order traversal, applying
#                 function `f` to the value in each non-null position

class tree_node:
    def __init__(self, val): # constructor
        self.value = val
        self.left = None # self.left is less than val
        self.right = None # self.right is greater than or equal to val
        
class tree:
    
    def __init__(self): # constructor
        self.root = None
        
    def ins(self, x):
        if self.root is None: # first node to be inserted
            self.root = tree_node(x)
        elif isinstance(x, type(self.root.value)): # checking if types are consistent. Only insert x if it is the same type as the values in the tree
            parent = None
            node = self.root
            while not node is None: # looking for a leaf node
                parent = node
                if(x < node.value):
                    node = node.left
                else:
                    node = node.right
            if (x < parent.value): # checking to see if the element being added should go in the left subtree or right subtree
                parent.left = tree_node(x)
            else:
                parent.right = tree_node(x)

    def clear(self): # clearing the tree
        self.root = None
        
    def inslist(self, l):
        for i in range(len(l)): # iterate through elements in list l
            tree.ins(self, l[i])  # insert current element to the tree
            
    def __inorder(self, f, node): # Helper method for recursive inorder
        if not node is None: # Base case when node is None
            self.__inorder(f, node.left) # recursive call to traverse left child
            f(node.value) # applying function f to node's value
            print(node.value, end = " ") # display the current node
            self.__inorder(f, node.right) # recursive call to traverse right child
            
        
    def inorder(self, f):
        self.__inorder(f, self.root) # start inorder traversal at the root
    
    def __preorder(self, f, node): # Helper method for recursice preorder
        if not node is None: #base case when node is None
            f(node.value) #apply function f to node's value
            print(node.value, end = " ") #display the current node
            self.__preorder(f, node.left) #preorder traversal on left child
            self.__preorder(f, node.right) #preorder traversal on right child
        

    def preorder(self, f):
        self.__preorder(f, self.root) # start preorder traversal at the root
        
        
# Testing all functions in tree.py        
# if __name__ == "__main__":
#     test_func = lambda a : 2*a
#     my_tree = tree()
#     my_tree.ins(3)
#     my_tree.ins(7)
#     my_tree.ins(14)
#     my_tree.ins(6)
#     my_tree.ins(0)
#     my_tree.preorder(test_func)
#     print()
#     test_list = [4, 5, 55, 67657, 866, 1, 2, 20, 4]
#     my_tree.inslist(test_list)
#     my_tree.inorder(test_func)
#     print()
#     my_tree.clear()
#     my_tree.inorder(test_func)
#     print()
#     my_tree.ins("AB")
#     my_tree.ins("BA")
#     my_tree.ins("CBC")
#     my_tree.ins("ADSFG")
#     my_tree.inorder(test_func)
    

        