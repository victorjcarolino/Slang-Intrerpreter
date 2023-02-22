# read_list: Read from the keyboard and put the results into a list.  The code
# should keep reading until EOF (control-d) is input by the user.
#
# The order of elements in the list returned by read_list should the reverse of
# the order in which they were entered.

def read_list():
    my_list = [] #declare an empty list named my_list
    EOF_reached = False
    while not EOF_reached:
        try: # if anything other than EOF is found, append the input to my_list
            my_list.append(input())
        except EOFError as e: # catching an EOFError, then exiting the while loop
            EOF_reached = True
    my_list.reverse() #reverses the order or my_list
    return my_list #return elements stroed in reverse order of input


# if __name__ == "__main__":
#     print(read_list())
