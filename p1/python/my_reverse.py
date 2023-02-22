# my_reverse: reverse a list without using the python `reverse` function

def my_reverse(l): # input is expected to be a list, l
    reversed = [] # creating an empty list for reversed items
    for i in range(len(l), 0, -1): # for loop iterating i from the length of l down to 0
        reversed.append(l[i-1]) # appending the elements from "l" to "reversed" in backwards order
    return reversed #returning the reversed list

# if __name__ == "__main__":
#     test_list = [1,2,3,4, "Dfijdsoijfoidsf",5,6]
#     print(test_list)
#     test_list = my_reverse(test_list)
#     print(test_list)
    
