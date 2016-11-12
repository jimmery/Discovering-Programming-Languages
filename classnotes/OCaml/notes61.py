

# Brief Interlude: Python

'''
Scripting Language:

- today any dynamically typed programming language
  is considered a scripting language
  - Python, JavaScript, Perl, PHP

- features:
  - easy manipulation of text data (e.g., regular expression matching)
  - easy interaction with the system (files, processes, etc.)
  - convenient syntax for lots of common operations

- disadvantages:
  - dynamically typed
  - kitchen sink approach
    - functional core
    - objects
    - imperative features

- key point:
  - the core of Python is MOCaml      

'''


def prodList(l):
	result = 1
	for i in l:
		result *= i
	return result


# given a list of ints, make a dictionary mapping each
# element to its frequency
def frequency(l):
	d = {}
	for i in l:
		if i in d:
			d[i] += 1
		else:
			d[i] = 1
	return d

