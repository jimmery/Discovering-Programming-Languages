/*
Object Oriented Programming (In Java)

Java version 8
SEAS machines: /usr/local/cs/bin/{java, javac}

Three Key Concepts: 

sub typing
inheritance
dynamic dispatch

Key idea: 

everything is an object.
objects only communicate by sending messages. 
you can imagine that each of these objects as little computers. 

objects are encapsulated - complete separation of interface and implementation
 */


import java.util.*;

// a type for set of strings
// interface. 
interface Set
{
    boolean contains(String s);
    void add(String s);  // notice that void means Set will be mutated. 
                         // => no longer functional. 
}

// implementation of the Set type
class ListSet implements Set
{
    protected List<String> elems = new LinkedList<String>();

    public boolean contains (String s)
    {
	return elems.contains(s);
    }

    public void add(String s)
    {
	if(this.contains(s))
	    return;
	else
	    elems.add(s);
    }
}

/* 

Two forms of polymorphism: 

- parametric polymorphism: 
  some type is pareterized by one or more type variables. 

  client can choose how to intstantiate type variables. 

  example: see PolySet below. 
 */

interface PolySet<T>
{
    boolean contains(T t);
    void add(T t); 
}

/*
  Java has a second kind of polymorphism: 

  - subtype polymorphism

  there's a *subtype* relation on types

  if S is a subtype of T, then you can pass 
  an object of type S where an object of 
  type T is expected. 

  Subtype polymorphism is a property of interfaces. 

  example: RemovableSet is a subtype of Set. 
 */

interface RemovableSet extends Set
{
    void remove(String s);
}

/* Inheritance

   a mechanism for code reuse in implementations
 */
class RemovableListSet extends ListSet implements RemovableSet
{
    // notice that elems, since protected, can be used in this class.
    public void remove(String s)
    {
	elems.remove(s);
    }
}

class Client
{
    void m(Set s)
    {
	if (s.contains("hi"))
	    s.add("bye");
    }

    void n(RemovableSet rs)
    {
	this.m(rs);
    }
}

/* Inheritance versus Subtyping */


// want subtyping without inheritance: 

/*class Rectangle
{
    double width;
    double length;

    // ...
}*/

// goal: Squares should be able to be passed wherever Rectangles are expected.
// it doesn't necessarily make sense to have Square inherit Rectangle. 

// one solution: 

interface Shape
{
    double area();
    double perimeter();
}

class Rectangle implements Shape
{
    private double width;
    private double length;

    public double area()
    {
	return width * length;
    }
    
    public double perimeter()
    {
	return 2 * (length + width);
    }
}
class Square implements Shape
{
    private double length;

    public double area()
    {
	return length * length;
    }

    public double perimeter()
    {
	return 4 * length;
    }
}

class MyClient
{
    void m(Shape s)
    {
	s.area();
    }
}

/* we want to implement bags 
   - sets that can contain duplicates
*/

/* claim: we want ListBag to inherit code from ListSet, but ListBag
   should not be a subtype of ListSet */

// solution: shared super class. 

abstract class Collection // implements Set
{
    protected List<String> elems = new LinkedList<String>();

    public boolean contains(String s)
    {
	return elems.contains(s);
    }

    abstract void add(String s);
    // ... other method implementations

    public void addAll(String[] a)
    {
	for (String s : a)
	    this.add(s);
    }
}

class ListSet2 extends Collection
{
    public void add(String s)
    {
        if(this.contains(s))
            return;
        else
            elems.add(s);
    }
}

class ListBag extends Collection
{
    public void add(String s)
    {
	elems.add(s);
    }
}

/*
  every single method call is dynamically dispatched. 
  invokes the "best" method implementation based on the class
  of the reciever object. 
 */

class C 
{
    void n() { this.m(); }
    void m() { System.out.println("C"); }
}

class D extends C
{
    void m() { System.out.println("D"); }
    void foo() {}
}

class Main
{
    public static void main(String[] args)
    {
	C c = new C();
	D d = new D();
	C c2 = new D();
	c.n();
	d.n();
	c2.n();
	// c2.foo() produces an error. 
        ((D)c2).foo();

	((C)d).m(); // no change in functionality. 
    }
}
