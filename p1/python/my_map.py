# my_map: apply a function to every element in a list, and return a list
# that holds the results.
#
# Your implementation of this function is not allowed to use the built-in
# `map` function.

def my_map(func, l): # l is expected to be a list, and func a function that takes elements of l as inputs
    outputs = [] # creating an empty list to store the outputs from func
    for i in range(len(l)): # for loop iterating fom 0 to the length of l
        outputs.append(func(l[i])) # appending the func-applied element in the l list to the outputs list
    return outputs # returning the list of outputs

# if __name__ == "__main__":
#     test_func = lambda a : 2*a
#     test_list = my_map(test_func,[1,2,3,4,5,6,7,8])
#     print(test_list)
