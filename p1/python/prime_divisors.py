# prime_divisors: compute the prime factorization of a number

def prime_divisors(n): # expected input is an (integer???)
    n = int(n)
    prime_factors = [] # create an empty list to store the prime factors of n
    found_factors = False # set found_factors to False
    for i in range(2, int(n/2)): # for loop iterating from 2 to n/2
        if n%i == 0: # if i is a factor of n
            prime_factors.append(prime_divisors(i)) # recursively appending prime_divisors(i) to prime_factors
            prime_factors.append(prime_divisors(n/i)) # recursively appending prime_divisors(n/i) to prime_factors
            found_factors = True # set found_factors to True, indicating n is not prime
            break # exit for loop, two factors (i & n/i) have been found
    if not found_factors: # checking to see if the given n is prime
        prime_factors.append(n) # appending the prime n to prime_factors
    return prime_factors # returning the list of prime factors


# if __name__ == "__main__":
#     print(prime_divisors(3214324))