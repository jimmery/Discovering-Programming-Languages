// Exceptions in Java

// consider the following list interface and implementation for the purpose
// of showing exception usage in Java.

interface List {
    void add(String s);
    String get(int i) throws OutOfBoundsAccess, Exn2;
}

class OutOfBoundsAccess extends Exception {}
class Exn2 extends Exception {}

class MyList implements List {
    public void add(String s) {}
    public String get(int i) throws OutOfBoundsAccess, Exn2 {
	if ( i < 0 )
	    throw new OutOfBoundsAccess();
	if ( i > 20 )
	    throw new Exn2();
	return "hi";
    }

    // also needs the throws clause, unless it is handled. 
    public String callsGet(int i) 
	throws OutOfBoundsAccess, Exn2 {
	return this.get(i);
    }

    // handles Exn2, but still throws OutofBoundsAccess.
    public String callsGet2(int i)
	throws OutOfBoundsAccess {
	try {
	    return this.get(i);
	} catch(Exn2 e) {
	    return "exn2 error";
	}
    }

    // handles both of the exceptions.
    public String callsGet3(int i) {
	try {
	    return this.get(i);
	} catch (Exn2 e) {
	    return "exn2 error";
	} catch(OutOfBoundsAccess e2) {
	    return "negative number";
	}
    }
}

/*
  OCaml equivalent: 
  exception OutOfBoundsException;;
  let f = fun x -> if x<0 then raise OutOfBoundsException else "hi";;

  The Java handling of the exceptions is mostly the same as every
  other language. Therefore, we maintain all of the same pros as 
  OCaml: that it separates the error handling and the normal handling
  of the code. 
  Recall from our study of OCaml, one of the cons of OCaml is that
  we don't know what type of Exceptions are returned from any function. 
  the type of f does not tell you which exceptions the function might 
  throw. 

  If you want to avoid program crashing, you need to know which exceptions
  a function might throw. 
  The main thing that differs from Java is they attempt to fix this issue.
  You cannot compile this code above without the "throws OutOfBoundsAccess".
  This allows for a caller to see that a documentation of which exceptions
  are thrown is given. 
  The compiler forces the documentation of all exceptions. 
  (Remember: this means that the interface needs the throws clause too.)

  In addition, any uses of the function must either propagate the throw
  clause, or handle the use of exceptions. Therefore, this allows for
  static checking of exception handling, which helps make sure that
  all runtime errors through exceptions is known to the compiler at 
  compile time. 

  As a note, it is okay to throw fewer exceptions that the interface, 
  but no subclass should throw a new exception that is not acocunted for
  in the interface. 
*/

class Client {
    // we propated the exception up. 
    void m() throws OutOfBoundsAccess, Exn2 {
	List l = new MyList();
	String s = l.get(-1); // this, when compiled, will throw exception.

	int[] arr = new int[3];
	arr[-1] = 0;
    }

    // even in main, we have to throw any unhandled exceptions. 
    public static void main(String[] args) throws OutOfBoundsAccess {
	try {
	    new Client().m();
	} catch(Exn2 e) {}
    }
}

/* unchecked exceptions. 
   
   the exception to the exception bubbling. 
   system level exceptions are generally unchecked, meaning we 
   don't have to tag them with throws clauses / handle them. 

   this is confusing because it is different from the other exceptions. 
   it lowers our hard guarantee of the static check of runtime errors. 
 */

/* exceptions in an imperative language (vs. a functional language). 

   problem: 

   I call a function get(...);
   it throws an exception.

   if the language is functional, we know that get hasn't changed anything.
   therefore, we don't have to worry about the state of anything. 

   however, in a mutable language like Java, an exception could be thrown
   in the middle of execution, meaning that we have to worry about 
   the state of our objects. 

   How do I get back to a safe state of memory, to continue executing? 

   "exception safety"
 */

class X {}
class Y {}

class XException extends Exception {}
class YException extends Exception {}

class Example {
    X x; 
    Y y;

    public void updateX() throws XException  {
	// ... do some computation
	// update the value of this.x. 
    }

    public void updateY() throws YException {
	// ... do some computation. 
	// update the value of this.y. 
    }

    // we want to ensure that either both get updated. 
    // or neither get updated. 
    public void updateBoth() throws XException, YException {
	updateX(); // may succeed. 
	updateY(); // may throw. => unwanted state. 
    }

    // in order to ensure the safe return to a known state: 
    // note this may still be a bug. 
    // this is REALLY complicated. 
    public void updateBoth2() throws XException, YException {
	X oldx = this.x; // this only creates a reference. this is a bug. 
	                 // may need to make a deep copy. 
	updateX();
	try {
	    updateY();
	}
	// non-trivial catch block. 
	catch(YException ye) {
	    this.x = oldx;
	    throw ye; // tell the client we did nothing. 
	}
    }

    public void readFileAndCompute(File f) {
	String s = f.read();
	try {
	    compute(s);
	    f.close();
	} catch(Exception e) {
	    f.close();
	    throw e;
	}

	//alternatively. this is the exact same. 
	try {
	    compute(s);
	} catch(Exn2 e) {
	    //... 
	} finally {
	    // finally block is always executed. 
	    f.close();
	}
    }
}

// here, prof showed us some C code. 

/*

// stacksmash.c

// run with gcc -fno-stack-protector stacksmash.c

#include <stdio.h>

void function(char* username, char* password) {
    char buffer[8];
    int i;
    int* xAddr;

    i = 0; 
    printf("i was %d\n", i);

    buffer[-4] = 5; // totally not good thing to do. changes i to 5. 
    printf("i is now %d\n", i);

    xAddr = (int*) &buffer + 8;
    *xAddr += 16; // this doesn't work. 
}

int main() {
    int x = 34;
    function();
    printf("x is %d\n", x); // changes x to 50. 
}

this is more detrimental with return addresses. 
someone who can access these values allows us to return to code
that they control. 

this is done by buffer overflows. 
if you write a password that's too long (longer than 8), with 
specific characters, they can control the return address. 

what does the stack protector do? 
it prevents writing to part of the stack. 
how? 1. canary value - random value that a function cannot go past. 

in Java, if you wrote the equivalent of this code, buffer[-4] would still 
compile, but we would get a runtime error of ArrayOutOfBoundsException. 
array object carries around at runtime. 

The bottom code would not be allowed, since we cannot control the memory
accesses in Java. using &buffer would not be allowed in Java. 

The main way to attack a safe language is to exploit some known bugs. 
We cannot use the same attacks as they are used on C programs. The 
reason we need C is still simply for embedded systems and operating 
systems. 

Professor hopes that Rust will kill C. 
Rust is a language from Mozilla, which is a C-like language, but with
a fancy type-system that keeps track of memory usage / access. 
Currently trying to build an Operating System in Rust. 

*/

/* memory allocation. 

   stack: local variables. 
          knows when it goes out of scope when function returns. 
	  names are on the stack are pointers to the heap. 
   heap: memory that potentially lives forever. 
         all objects live on the heap. 
         requires garbage collector. 

   therefore, the . operator in java (like list.add()) is always a dereference.
   (as in the -> operator in C++). 

   Let's consider the following: 

   Set s = new ListSet(); // s belongs to the stack
                          // pointing to a newly created object, o,  on heap. 
   s.add("hi"); // dereferencing s and updating object on heap. 
                // updating object o to contain the "hi" value. 

   Set s2 = s; // the actual value of s is the value on the stack. 
               // therefore, when copying this value, we only copy the 
	       // pointer through, so it's simply a shallow copy. 
	       // a new reference to the same object. 
	       // s and s2 are called *aliases*

   s2.remove("hi"); // o no longer contains "hi"

   // assuming Set implements Cloneable. 
   Set s2 = s.clone(); // actually clones a new object. 
*/

/* parameter passing: 

In Java, parameter passing is by value: 
  the value of the actual parameter is copied
  into the formal parameter. (always copied by value)

But the values are always pointers in non-primitive parameters. 

key property: value of the actual parameter cannot be changed by the
              function call. 

examples. 

// 1. primitives only: 
int plus(int a, int b) {
    a += b;
    return a;
}

void f() {
    int x = 3;
    int y = 4;
    int z = plus(x,y);
}

since we only have primitives, when we pass by value, the 
parameters can never change. Therefore, x and z in f will never
change because it is passed into z. 

---- 

class Integer {
    public int i;
    Integer(int val) {this.i = val; }
}

Ingeger plus(Integer a, Integer b) {
    a = new Integer(a.i + b.i);
    return a;
}

void f() {
    Integer x = new Integer(3);
    Integer y = new Integer(4);
    Integer z = plus(x,y);
}

This can be very confusing. in the plus function, 
even though x and a were aliases when the function was called, 
when we call a = new Integer(a.i + b.i), this call shifts a to a new
object, but x remains at the same location. 

--------

Integer plus(Integer a, Integer b) {
    a.i += b.i;
    return a;
}

Finally, this case will produce a case where the contents of x have
changed. However, the VALUE of x has not changed because of this function. 
Now x and z point to the same object. 

--------

Call by reference (in C++, not Java): 

you make the actual and formal parameters be aliases of each other. 
this means that a and x will have the same STACK location. which is 
different from Java in that a and x have different stack locations, but
point to the same heap location. 

int plus( int& a, int& b ) {
    a += b; 
    return a;
}

void f() {
    int x = 3;
    int y = 4;
    int z = plus(x,y);
}

This is overly complicated because we have two different ways 
to call functions. We can do the exact same things with call by reference
just by using the heap. So Java only allows for call by value, to 
simplify things. 

The main con is that allocating on the heap is more expensive. However, 
the small performance gain adds a lot of complexity to the language. 
*/