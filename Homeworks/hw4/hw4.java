/* Name: Jeffrey Jiang

   UID: 904255069

   Others With Whom I Discussed Things: Jonathan Hurwitz (test cases)

   Other Resources I Consulted:
   
*/

// import lists and other data structures from the Java standard library
import java.util.*;

// PROBLEM 1

// a type for arithmetic expressions
interface Exp {
    // returns the value that the expression evaluates to. 
    double eval(); 	                       // Problem 1a

    // converts an expression into a list of instructions. 
    List<Instr> compile(); 	               // Problem 1c
}

/* a subtype of the Expression type that simply represents
   that there our expression is a single double floating point 
   number. 
*/
class Num implements Exp {
    protected double val;

    // constructor that takes in a number and stores it. 
    public Num(double in)
    {
	val = in;
    }

    // equals and toString methods were provided. 
    public boolean equals(Object o) { 
	return (o instanceof Num) && ((Num)o).val == this.val;
    }

    public String toString() { return "" + val; }

    // since this expression is simply a Num, the value of this
    // expression is the saved value. 
    public double eval()
    {
	return val;
    }

    // compiling a single number just requires us to push
    // the number onto the stack. Therefore, we simply return
    // a list containing Push(val).
    public List<Instr> compile()
    {
	List<Instr> instrs = new LinkedList<Instr> ();
	instrs.add(new Push(val));
	return instrs;
    }
}

/* 
   a subtype of Exp that creates an expression of the form 
   (exp) op (exp). Where each of the exp represent an expression
   of some form, while the op can be any of the four binary
   operators on floating point numbers: +, -, *, /.
 */
class BinOp implements Exp {
    protected Exp left, right;
    protected Op op;

    // constructor that creates a BinOp Expression. 
    public BinOp (Exp leftExpr, Op oper, Exp rightExpr)
    {
	left = leftExpr;
	op = oper;
	right = rightExpr;
    }

    // equals and toString provided by the skeleton code. 
    public boolean equals(Object o) {
    	if(!(o instanceof BinOp))
    		return false;
    	BinOp b = (BinOp) o;
    	return this.left.equals(b.left) && this.op.equals(b.op) &&
		    	this.right.equals(b.right);
    }

    public String toString() {
	return "BinOp(" + left + ", " + op + ", " + right + ")";
    }

    // eval actually finds the value of the binary operation. 
    // in order to do that, it must also evaluate both the 
    // left and right expressions, and then operate on them. 
    public double eval() 
    {
	return op.calculate(left.eval(), right.eval());
    }

    // compile takes the binary operation and adds a new 
    // instruction to it. Because of the stack structure required, 
    // we would do all the instructions to evaluate the left
    // expression, then do all the instructions to evaluate the right
    // expression, then combine the two and add a Caclulate instruction.
    public List<Instr> compile()
    {
	List<Instr> left_list = left.compile();
	List<Instr> right_list = right.compile();
	left_list.addAll(right_list);
	left_list.add(new Calculate(op));
	return left_list;
    }
}

// a representation of four arithmetic operators
enum Op {
    PLUS { public double calculate(double a1, double a2) { return a1 + a2; } },
    MINUS { public double calculate(double a1, double a2) { return a1 - a2; } },
    TIMES { public double calculate(double a1, double a2) { return a1 * a2; } },
    DIVIDE { public double calculate(double a1, double a2) { return a1 / a2; } };

    abstract double calculate(double a1, double a2);
}

// a type for arithmetic instructions
interface Instr {
    // added a stackOp method which takes a Stack of Doubles as input. 
    // because of the fact that this is mutable, we can easily pass 
    // this stack between objects as a way to provide information. 
    void stackOp( Stack<Double> instrs );
}

// This Instruction only has the function of pushing a double onto a stack.
class Push implements Instr {
    protected double val;
    
    public Push(double num)
    {
	val = num;
    }

    // this is the implemented function to push the saved double onto stack.
    public void stackOp( Stack<Double> instrs )
    {
	instrs.push(val);
    }

    public boolean equals(Object o) { 
	return (o instanceof Push) && ((Push)o).val == this.val; 
    }

    public String toString() {
	return "Push " + val;
    }

}

// This instruction takes the information on the stack and pops the top 
// two elements off. Afterward, it computes the operation it contains
// and pushes the result back in. 
class Calculate implements Instr {
    protected Op op;
    
    public Calculate(Op oper)
    {
	op = oper;
    }

    public void stackOp( Stack<Double> instrs )
    {
	double left = 0.0;
	double right = 0.0;

	if ( !instrs.empty() )
	    right = instrs.pop();
	if ( !instrs.empty() )
	    left = instrs.pop(); 
	
	instrs.push(op.calculate(left, right));
    }
    
    public boolean equals(Object o) { 
	return (o instanceof Calculate) && 
	    ((Calculate)o).op.equals(this.op);
    }

    public String toString() {
	return "Calculate " + op;
    }    
}

class Instrs {
    protected List<Instr> instrs;

    public Instrs(List<Instr> instrs) { this.instrs = instrs; }

    // since we have a list of instructions, we simply execute the list
    // by doing the proper stack operation for each instruction until
    // we finish all instructions. 
    public double execute() // Problem 1b
    {
	Stack<Double> stack = new Stack<Double> ();
	for ( Instr i : instrs ) {
	    i.stackOp(stack);
	}
	
	// assumed no underflow and errors and stuff (like in HW2).

	return stack.pop();
    }
}


class CalcTest {
    public static void main(String[] args) {
	// a test for Problem 1a
	Exp exp =
	    new BinOp(new BinOp(new Num(1.0), Op.PLUS, new Num(2.0)),
		      Op.TIMES,
		      new Num(3.0));
	assert(exp.eval() == 9.0);

	// a test for Problem 1b
	List<Instr> is = new LinkedList<Instr>();
	is.add(new Push(1.0));
	is.add(new Push(2.0));
	is.add(new Calculate(Op.PLUS));
	is.add(new Push(3.0));
	is.add(new Calculate(Op.TIMES));
	Instrs instrs = new Instrs(is);
	assert(instrs.execute() == 9.0);
	
	// a test for Problem 1c
	assert(exp.compile().equals(is));

	// test with non-commutative operations. 
	exp = new BinOp(new BinOp(new Num(4.0), Op.PLUS, new Num(2.0)), 
			Op.DIVIDE, 
			new BinOp(new Num(4.0), Op.MINUS, new Num(2.0)));
	assert(exp.eval() == 3.0);

	// expected instruction list of above. 
	List<Instr> ins = new LinkedList<Instr>();
	ins.add(new Push(4.0));
	ins.add(new Push(2.0));
	ins.add(new Calculate(Op.PLUS));
	ins.add(new Push(4.0));
	ins.add(new Push(2.0));
	ins.add(new Calculate(Op.MINUS));
	ins.add(new Calculate(Op.DIVIDE));
	instrs = new Instrs(ins);
	assert(instrs.execute() == 3.0);

	assert(exp.compile().equals(ins));
    }
}


// PROBLEM 2

// the type for a set of strings
interface StringSet 
{
    int size();
    boolean contains(String s);
    void add(String s);
}

/* an implementation of StringSet using a linked list

   notice that all of the method declarations of this class are a single line.

   none of the actual work is done in this class. 
   all implementations are taken care of in the SNodes. 

   this is because each node will contain their own status for each of
   these functions. */ 
class ListStringSet implements StringSet {
    protected SNode head;

    public ListStringSet()
    {
	head = new SEmpty();
    }

    // size will return the size of the head. 
    public int size()
    {
	return head.size();
    }

    // contains will return the status of contains of the head. 
    public boolean contains(String s)
    {
	return head.contains(s);
    }

    // add will take the node that is returned from the add method 
    // and set it as the new head. 
    public void add(String s)
    {
	head = head.add(s);
    }

    public String toString()
    {
	return head.toString();
    }
}

/* a type for the nodes of the linked list
   here, we define the exact same methods, as to provide a clear indication
   that they operate in the way that the ListSet above wanted. 

   The way that they are implemented, an SNode represents a linked list
   in its own right. That is an SEmpty would be equivalent to [] in OCaml
   and an SElement would have h and t, so that we can use something 
   similar to the h::t of OCaml. Then, the only thing is we implement 
   each methods as the expression body of each case as we did in OCaml.

   We can clearly start noticing the difference between OCaml and Java though.
   Because of the power of pattern matching, we could implement this very
   easily in OCaml, but in Java, we have to use subtype polymorphism to 
   get the same functionality. (In other words the OCaml pattern matching
   actually forms a safe version of instanceof, it seems). 
*/
interface SNode
{
    /* returns the size of the linked list of this node. 
       while this can be a part of a linked list, this node does not know
       that. In other words, it returns the number of elements including
       and following this node. */
    int size();
    /* takes a String and attempts to add it into the linked list. 
       returns the SNode of the head of the list after the attempt is 
       complete. */
    SNode add(String s);
    /* takes a String and returns true if it is in this linked list. */
    boolean contains(String s);
}

/* represents an empty node (which ends a linked list). [] in OCaml. 
   as mentioned in class, SEmpty somewhat just acts like a "smart" null. 
   when asked about the size, it says there's nothing there. 
   when asked about contains, it says false. 
   when adding a new SNode, it makes a new SElement, which points to 
   this SEmpty as the following node and returns the SElement. 
*/
class SEmpty implements SNode 
{
    public int size()
    {
	return 0;
    }

    // when adding a new String in an empty list, we simply
    // just create a new Node containing the String. We allow it 
    // to point to this element, since this is the list terminator anyway.
    public SNode add(String s)
    {
	return new SElement( s, this );
    }

    public boolean contains(String s)
    {
	return false;
    }

    // for testing purposes only, but still follows a recursive form.
    public String toString()
    {
	return "";
    }
}


/* represents a non-empty node, which can be interpretted as 
   the h::t case of an OCaml pattern match. 
*/
class SElement implements SNode {
    protected String elem;
    protected SNode next;

    public SElement(String s, SNode tail)
    {
	elem = s;
	next = tail;
    }

    // this allows for the recursive call for any SElement node to
    // have the right size at all times. 
    public int size()
    {
	return 1 + next.size();
    }

    /* in this case, we compare the input String with the current String.
       if they are the same, because we are implementing a set, we simply
       ignore it, returning this as the head of the linked list. 

       if we have that the input string is less than the element in the 
       list, then we know that the s node has not been found (given
       that we know that these values are already sorted) and that the
       node containing s must come before this node. So we create a 
       new SElement node containing s and let it point to this node as
       its next. Then we return that node as the head of the new list. 

       finally, if s is still larger than the element in the list, then 
       we can continue looking for the value of s, as it should come
       after this node anyway. Since we have not added anything, we
       return this node, but the values after this node in the list
       may experience some changes, but it is not something that can be
       seen in this node or affect anything in this node. We set our 
       next node as whatever is returned from the add, since that 
       ensures that we have any intended changes to the list. 
    */
    public SNode add(String s)
    {
	if (s.compareTo(elem) == 0)
	    return this;
	else if (s.compareTo(elem) < 0)
	    return new SElement(s, this);
	else {
	    next = next.add(s);
	    return this;
	}
    }

    // similar to add, except with much easier return logic. 
    public boolean contains(String s)
    {
	if (s.compareTo(elem) == 0)
	    return true;
	else if (s.compareTo(elem) < 0)
	    return false;
	else
	    return next.contains(s);
    }

    // for debug purposes only. still follows a recursive form though. 
    public String toString()
    {
	return elem + " " + next.toString();
    }
}

// test class for problem 2. 
class SetTest
{
    public static void main (String[] args)
    {
	StringSet s = new ListStringSet();
	assert(!s.contains("hello"));
	assert(s.size() == 0);
	assert(s.toString().length() == 0);

	s.add("hello");
	s.add("world");

	assert(!s.contains("hi"));
	assert(s.contains("world"));
	assert(s.size() == 2);
	System.out.println(s);
	assert(s.toString().equals("hello world "));

	s.add("Abcdefghijklmnopqrstuvwxyz");
	s.add("the");
	s.add("cake");
	s.add("is");
	s.add("a");
	s.add("lie");
	s.add("a");

	assert(s.contains("a"));
	assert(s.size() == 8);
	assert(s.toString().equals("Abcdefghijklmnopqrstuvwxyz a cake " + 
				   "hello is lie the world "));

	Set<String> ls = new ListSet<String>((s1, s2) -> s1.compareTo(s2));

	ls.add("hello");
	ls.add("world");

	assert(!ls.contains("hi"));
	assert(ls.contains("world"));
	assert(ls.size() == 2);
	assert(ls.toString().equals("hello world "));

	ls.add("Abcdefghijklmnopqrstuvwxyz");
	ls.add("the");
	ls.add("cake");
	ls.add("is");
	ls.add("a");
	ls.add("lie");
	ls.add("a");

	assert(ls.contains("a"));
	assert(ls.size() == 8);
	assert(ls.toString().equals("Abcdefghijklmnopqrstuvwxyz a cake " +
                                   "hello is lie the world "));

	// test with a different compare function. 
	ls = new ListSet<String>((s1, s2) -> s1.length() - s2.length());
	
	ls.add("hello");
	ls.add("world");

	assert(ls.contains("hello"));
	assert(ls.contains("world"));
	assert(ls.contains("Earth"));
	assert(!ls.contains("hi"));
	assert(ls.size() == 1);
	assert(ls.toString().equals("hello "));

	ls.add("Abcdefghijklmnopqrstuvwxyz");
	ls.add("the");
	ls.add("cake");
	ls.add("is");
	ls.add("a");
	ls.add("lie");
	ls.add("b");

	assert(ls.contains("a"));
	assert(ls.size() == 6);
	assert(ls.toString().equals("a is the cake hello " + 
				   "Abcdefghijklmnopqrstuvwxyz "));

	// test with a different type. 
	Set<Integer> is = new ListSet<Integer>((i1,i2) -> i1*i1 - i2*i2);
	is.add(0);
	is.add(-1);
	is.add(4);
	is.add(34);

	assert(is.size() == 4);
	assert(is.contains(1));
	assert(is.toString().equals("0 -1 4 34 "));

	is.add(1);
	is.add(-4);
	is.add(2);

	assert(is.size() == 5);
	assert(!is.contains(3));
	assert(is.toString().equals("0 -1 2 4 34 "));

	// one more different type test. 
	// strange comparison function. does a positive mod by 5. 
	// since the answer is always >= 0, it always appends at end. 
	// however, it only does it if it's a new equivalence class 
	// modulo 5. 
	is = new ListSet<Integer>((i1,i2) -> ((i1-i2)%5 + 5)%5);
	is.add(0);
	is.add(1000);

	assert(is.size() == 1);
	assert(is.contains(5));
	assert(is.toString().equals("0 "));

	is.add(12);
	is.add(34);
	is.add(42);

	System.out.println(is);

	assert(is.size() == 3);
	assert(is.contains(2));
	assert(is.contains(9));
	assert(is.toString().equals("0 12 34 "));

	is.add(314);
	is.add(3141);
	is.add(31415);
	is.add(314159);
	is.add(3141592);
	is.add(31415926);
	is.add(314159265);
	is.add(161);
	is.add(1618);
	is.add(16180);
	is.add(161803);

	System.out.println(is);

	assert(is.size() == 5);
	assert(is.contains(1));
	assert(is.toString().equals("0 12 34 3141 1618 "));

	// Jonny's test cases. 
	//Problem 2 tests
        SNode three = new SElement("third", new SEmpty());
        SNode two = new SElement("second", three);
        SNode first = new SElement("first", two);
        //System.out.println(first.size());
        assert(first.size() == 3);

        ListStringSet test = new ListStringSet();
        assert(!test.contains("hello"));
        test.add("hello");
        test.add("it's");
        test.add("m");

        assert(test.contains("m"));
        assert(test.size() == 3);
        test.add("cs is terrible");
        assert(test.size() == 4);

        test.add("hello");
        assert(test.size() == 4);
        test.add("I really like pizza. It's great.");
        assert(test.size() == 5);
        assert(!test.contains("Jeffrey"));

        test.add("Jeffrey");
        test.add("jeffrey");

        System.out.println(test);
        System.out.println(test.size());

        //2b tests
        System.out.println("Testing 2b integer ListStringSet...");
        ListSet<Integer> sList = 
	    new ListSet<Integer>((Integer s1, Integer s2) -> s2 - s1);
        sList.add(-4);
        sList.add(1);
        sList.add(3);
        sList.add(-6);
        sList.add(100);
        System.out.println(sList);
        assert(sList.size() == 5);
        assert(sList.contains(3));
        assert(!sList.contains(4));

        //Greatest to least
        System.out.println("Testing 2b string ListStringSet...");

        ListSet<String> sList2 = 
	    new ListSet<String>((s1, s2) -> s2.length() - s1.length());
        assert(!sList2.contains("12fsae"));

        assert(sList2.size() == 0);
        sList2.add("Guess who is back");
        assert(sList2.size() == 1);

        sList2.add("backagain");
        sList2.add("pizza is back");
        sList2.add("aNotHer one");
        assert(!sList2.contains("hi"));
        sList2.add("hi");
        assert(sList2.contains("hi"));

        sList2.add("bye");
        sList2.add("BIGGGGG");
        sList2.add("Bye");
        sList2.add("two");
        assert(sList2.size() == 7);
        assert(!sList2.contains("12fsae"));
        System.out.println(sList2);

        ListSet<Integer> sList3 = 
	    new ListSet<Integer>((Integer s1, Integer s2) -> s2 - s1);
        assert(sList3.size() == 0);
        assert(!sList3.contains(1));
        assert(!sList3.contains(0));
        System.out.println("Successfully passed all tests.");
    }
}

// the type for a set of any type. 
interface Set<T>
{
    int size();
    boolean contains(T t);
    void add(T t);
}

// an implementation of Set using a linked list
// the main difference here is that we are using parametric polymorphism. 
// this allows for more general use of this set. 
// in addition, there is a need to consider a Comparator class, since 
// we may have different comparison needs, for different classes or uses. 
// other than that, the implementation of these classes is the exact same
// as the StringSet.
class ListSet<T> implements Set<T> {
    protected Node<T> head;
    protected Comparator<T> comp;

    public ListSet(Comparator<T> c)
    {
	comp = c;
	head = new Empty<T>(comp);
    }

    public int size()
    {
        return head.size();
    }

    public boolean contains(T t)
    {
        return head.contains(t);
    }

    public void add(T t)
    {
        head = head.add(t);
    }

    public String toString()
    {
	return head.toString();
    }
}

interface  Node<T>
{
    int size();
    Node<T> add(T t);
    boolean contains(T t);
}

class Empty<T> implements Node<T>
{
    protected Comparator<T> comp;

    public Empty(Comparator<T> c) {
	comp = c;
    }

    public int size()
    {
	return 0;
    }
    
    public Node<T> add(T t)
    {
	return new Element<T>(t, this, comp);
    }

    public boolean contains(T t)
    {
	return false;
    }

    public String toString()
    {
	return "";
    }
}

class Element<T> implements Node<T>
{
    protected T elem;
    protected Node<T> tail;
    protected Comparator<T> comp;

    public Element(T t, Node<T> next, Comparator<T> c) {
	elem = t;
	tail = next;
	comp = c;
    }

    public int size()
    {
	return 1 + tail.size();
    }

    public Node<T> add(T t)
    {
	if ( comp.compare(t, elem) == 0 )
	    return this;
	else if ( comp.compare(t, elem) <= 0 )
	    return new Element<T>(t, this, comp);
	else {
	    tail = tail.add(t);
	    return this;
	}

    }

    public boolean contains(T t)
    {
	if ( comp.compare(t, elem) == 0 )
	    return true;
	else if ( comp.compare(t, elem) < 0 )
	    return false;
	else
	    return tail.contains(t);
    }

    public String toString()
    {
	return elem.toString() + " " + tail.toString();
    }

}