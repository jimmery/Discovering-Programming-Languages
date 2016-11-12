import java.util.*;

// subtyping, inheritance, dynamic dispatch

// two kinds of polymorphism: 
// parametric and subtype 

// parametric: type variables
// equivalent in OCaml 'a. 
interface PolyList<T> {
    void add(T t);
    boolean contains(T t);
    // ...

    // let's say we add another method. 
    // this guarantees the type to be the correct return value. 
    // this avoids casting in higher level programs. 
    T get(int i);
}

// subtype polymorphism: 
interface Animal {
    void eat();
    //...
}

interface Cow extends Animal {
    void moo();
    //...
}

// here, in the doSomething method, C can be either a class 
// implementing C or D. 
class Client {
    void doSomething(Animal c) {
	c.eat();
	// ...
    }
}

// Why do we need parametric polymorphism in Java? 
interface ObjectList {
    void add(Object o);
    boolean contain(Object o);

    // equivalent added method above
    Object get(int i);

    // since we are returning Object, we no longer know what type this
    // object is. We are then forced to use a typecast in order to further
    // use this, but we have no guarantees about this. 
}

// what's wrong with just using this? 
// what's the difference betweeen ObjectList and List<T>?

/*
  1. ObjectList can contain any kind of object: 
     - a single list can contain Strings, Integer, Doubles, Foos, etc.
     List<String> can only contain Strings. (enforced)

  2. Parametric polymorphism can correlate input and output types. 
     Subtyping can't. (i.e. Subtyping requires the use of type-casting
     in the output, which is bad because of the possibility of run-time
     errors. 

  However, parametric polymorphism cannot do the same hierarchy things 
  as subtype polymorphism. For example, in the example above, we cannot
  call eat() on the generic object. 
*/

class Main {
    void m(List<String> l1, ObjectList l2) {
	l1.add("hi");
	l1.add("bye");
	//l1.add(new Integer(43)); // this will produce a type error. (syntax)
	String s = l1.get(0);

	l2.add("hi");
	l2.add("bye");
	//l2.add(new Integer(43)); // this will work.
	//String s2 = l2.get(0); // static error. Object -/-> String. 
	
	// for the first ten years of Java's life, there was no 
	// parametric polymorphism, so we would always need to do 
	// this type check. 
	String s2 = (String)l2.get(0);

	// however, this is super annoying. In addition, this provides
	// a way to create runtime errors in the code, because we 
	// don't know what's going on to type cast. 
    }
}

// combining subtyping with parametric polymorphism. 
// the rest of this code is grabage, (for compilation), but
// this just shows that we can create a list of a specific 
// generic type, allowing for us to call the Animal methods. 
class AnimalList<T extends Animal> implements PolyList<T> {
    public void add(T t) { t.eat(); }

    public boolean contains(T t) {return false;}

    public T get(int i) {return null;}
}

// benefits: no need to downcast. 
// still get to use Animal methods. 

// dynamic dispatch and static overloading. 
// these can be confusing. 
class C0 {
    void m() {}
}

class C {
    void m(String s) {}
    void m(Integer i) {}

    // even more interesting: 
    void n(Animal a) {} // (m1 is called). 
    void n(Cow c) {} // (m2 is called). 
}

class D extends C0 {
    void m() {}
}

class Main2 {

    public void doit(Animal a, Cow cow) {
	C0 c0 = new D();
	D d = new D();

	c0.m(); // calls the m() in D, due to dynamic dispatch. 

	// static overloading. 
	C c = new C();
	c.m("hi"); // method determined at compile time. 
	c.m(34);   // method determined at compile time. 

	c.n(a);   // method statically determined. (m1 called)
	c.n(cow); // method statically determined. (m2 called)
	Animal a2 = cow;
	c.n(a2); // m1 will be called. 

	// this is EXTREMELY confusing. 
	// notice that this is the opposite of the dynamic dispatch
	// behavior from the method call. 

	// this is the dangers of static overloading. 
    }
}

class A {}
class B extends A {}

class X {
    public void m(A a) { System.out.println("X,A"); }
}

class Y extends X {
    public void m(A a) { System.out.println("Y,A"); }
    public void m(B b) { System.out.println("Y,B"); }
}

class Stuff {
    public static void main (String[] args) {
	X x = new Y();
	x.m(new A()); // prints out "Y,A"
    }
}

/* however, if we changed the parameter of m(A a) to m(B b), we will
   call X's m, since Y's m has a completely different type. 
   the idea is that if we have different parameters, the method 
   has a completely different name. 

   key point: 
   if two methods have the same name but either different 
   number of arguments or different parameter types, they are
   treated as if they have different names. 

   another way of looking at it: 
   two phases of method lookup: 

   1. static phase: based on the static types of the parameters, 
      we find the right "method family"
      - method name plus argument types. 

      in our case: statically, we choose whether to choose the m(a) 
      or m(b) family. We choose the m(a) family because the input is 
      of static type A. 

   2. dynamic phase: dynamic dispatch on the receiver object, 
      over the methods from the statically chosen family. 

      afterward, from the method family chosen at compile time, 
      we can now use dynamic dispatch to choose which method, 
      specifically that we choose. 

   this is complicated. Todd thinks that static overloading should not
   have been implemented along with dynamic dispatch. 
*/


/*

OO style: each object knows how to do certain things. 
          specified by some kind of interface. 

	  clients should be able to manipulate the object
	  just through that interface. 

	  loops are not good OO style, usually. 
	  arrays and nulls are bad OO style. 
	  Empty class in the HW is a "smart" null. 
*/

// imperative sets. BAD OO STYLE. 
class Set {
    int[] arr;
    // arrays are not object oriented at all: 
    // they have no methods. they are fundamentally stupid. 

    // this code is imperative, not object oriented. 
    // if we change the implementation, we have to change the code.

    // in good OO code, we change change parts of the implementation
    // in a way that does not cross interface boundaries. 
    boolean contains(int i) {
	for (int j : arr)
	    if (i == j)
		return true;
	return false;
    }    
}

// Comparators and sorting. 

// this is bulky. 
// the only thing we use this entire class for is for the compare function.
// so java created a 
class MyComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
	return s1.length() - s2.length();
    }
}

class Sorting {
    public static void main(String[] args) {
	List<String> l = new LinkedList<String>();
	for(String s : args)
	    l.add(s);

	Collections.sort(l); // sorts based on "natural order", (lexical)
	Collections.sort(l, new MyComparator());
	
	// creates the comparator above using "anonymous classes". 
	// super bulky / ugly, but at least we don't create a whole class.
	Collections.sort(l, 
			 new Comparator<String>() {
			     public int compare(String s1, String s2) {
				 return s1.length() - s2.length();
			     }
			 });

	// same thing as before, but even less code. 
	// creates a lambda. :D
	Collections.sort(l, (String s1, String s2) -> 
			 s1.length() - s2.length());
	
	// type inference also included!
	Collections.sort(l, (s1, s2) -> s1.length() - s2.length());

	// this is new in java this year. :D
	// adding lambdas / functional programming extensions. 
	// are really useful for bigdata. 

	System.out.println(l);
    }
}

/* java 8 introduces Stream<T>. 

int sum = widgets.stream()
    .filter(w -> w.getColor() == RED)
    .mapToInt(w -> w.getWeight())
    .sum();

this is the same format as the functional programming. 
we can view this data as streaming into boxes. 
==> filter => map => sum. 

Importance: 1. pipelining. 
            2. parallizing.

by default, the code above is not parallel, but all 
we need to do in order to parallize it is...
change stream() to parallelstream() 

this is why they added functional programming to java last year. 
in the next assignment, we will be building streams like this. 
*/