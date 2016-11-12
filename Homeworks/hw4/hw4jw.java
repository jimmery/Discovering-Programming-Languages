/* Name: Jonathan Hurwitz

   UID: 804258351
 
   Others With Whom I Discussed Things:

   Other Resources I Consulted:
   
*/

// import lists and other data structures from the Java standard library
import java.util.*;

// PROBLEM 1

// a type for arithmetic expressions
interface Exp {
     double eval(); 	                       // Problem 1a
     List<Instr> compile(); 	               // Problem 1c
}


/*
Compile takes in an expression like:

BinOp(BinOp(Num 1.0, Plus, Num 2.0), Times, Num 3.0)

and outputs a sequence of stack instructions representing the same computation:

Push 1.0; Push 2.0, Calculate Plus; Push 3.0; Calculate Times.

*/
class Num implements Exp {
    protected double val;

    //Num constructor
    public Num(double v){
        val = v;
    }


    public double eval(){
        return val;
    }

    public List<Instr> compile(){
        List<Instr> nlist = new ArrayList<Instr>();
        nlist.add(new Push(val));

        return nlist;
    }

    public boolean equals(Object o) { return (o instanceof Num) && ((Num)o).val == this.val; }

    public String toString() { return "" + val; }
}

class BinOp implements Exp {
    protected Exp left, right;
    protected Op op;

    //Set member variables
    public BinOp (Exp l_exp , Op oper, Exp r_exp){
        left = l_exp;
        right = r_exp;
        op = oper;
    }

    //Eval function implementation
    public double eval(){
        return op.calculate(left.eval(), right.eval());
    }

    public List<Instr> compile(){
        List<Instr> blist = new ArrayList<Instr>();



        blist.addAll(left.compile());
        blist.addAll(right.compile());
        blist.add(new Calculate(op));

        return blist;
    }

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
    public double execute(Stack <Instr> inst_stack);
}

class Push implements Instr {
    protected double val;

    //Push constructor
    public Push(double val_to_push){
        val = val_to_push;
    }


    //1b
    public double execute(Stack <Instr> s){
        return val;
    }

	public boolean equals(Object o) { return (o instanceof Push) && ((Push)o).val == this.val; }

    public String toString() {
		return "Push " + val;
    }

}

class Calculate implements Instr {
    protected Op op;

    //Calculate constructor
    public Calculate(Op oper){
        op = oper;
    }


    //1b
    public double execute(Stack <Instr> s){
        double res = -1;
        if(!s.empty()){
            double n1 = (s.pop()).execute(s); //second element pushed onto stack is at top
            double n2 = (s.pop()).execute(s); //first element pushed onto stack
            res = op.calculate(n2, n1);


        }else{
            System.out.println("Attempting to evaluate stack with too few operands.");

        }
        return res;

    }

    public boolean equals(Object o) { return (o instanceof Calculate) && 
    						  ((Calculate)o).op.equals(this.op); }

    public String toString() {
		return "Calculate " + op;
    }    
}

class Instrs {
    protected List<Instr> instrs;

    public Instrs(List<Instr> instrs) { this.instrs = instrs; }

     public double execute() {
        //Put everything in the list onto the stack
        Stack <Instr> instr_stack = new Stack <Instr>();
        for(Instr instr : instrs){
            instr_stack.push(instr);
        }

        return (instr_stack.pop()).execute(instr_stack);



     }  // Problem 1b
}


class CalcTest {
    public static void main(String[] args) {
	 //    // a test for Problem 1a
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

         //Problem 2 tests
        SNode three = new SElement("third", new SEmpty());
        SNode two = new SElement("second", three);
        SNode first = new SElement("first", two);
        //System.out.println(first.size());
        assert(first.size() == 3);

        ListStringSet test = new ListStringSet();
        assert(test.contains("hello") == false);
        test.add("hello");
        test.add("it's");
        test.add("m");

        assert(test.contains("m") == true);
        assert(test.size() == 3);
        test.add("cs is terrible");
        assert(test.size() == 4);

        test.add("hello");
        assert(test.size() == 4);
        test.add("I really like pizza. It's great.");
        assert(test.size() == 5);
        assert(test.contains("Jeffrey") == false);

        test.add("Jeffrey");
        test.add("jeffrey");

        test.print();
        System.out.println(test.size());

        //2b tests
        System.out.println("Testing 2b integer ListStringSet...");
        ListSet<Integer> sList = new ListSet<Integer>((Integer s1, Integer s2) -> s2 - s1);
        sList.add(-4);
        sList.add(1);
        sList.add(3);
        sList.add(-6);
        sList.add(100);
        sList.print();
        assert(sList.size() == 5);
        assert(sList.contains(3) == true);
        assert(sList.contains(4) == false);

        //Greatest to least
        System.out.println("Testing 2b string ListStringSet...");

        ListSet<String> sList2 = new ListSet<String>((String s1, String s2) -> s2.length() - s1.length());
        assert(sList2.contains("12fsae") == false);

        assert(sList2.size() == 0);
        sList2.add("Guess who is back");
        assert(sList2.size() == 1);

        sList2.add("backagain");
        sList2.add("pizza is back");
        sList2.add("aNotHer one");
        assert(sList2.contains("hi") == false);
        sList2.add("hi");
        assert(sList2.contains("hi") == true);


        sList2.add("bye");
        sList2.add("BIGGGGG");
        sList2.add("Bye");
        sList2.add("two");
        assert(sList2.size() == 7);
        assert(sList2.contains("12fsae") == false);
        sList2.print();

        ListSet<Integer> sList3 = new ListSet<Integer>((Integer s1, Integer s2) -> s2 - s1);
        assert(sList3.size() == 0);
        assert(sList3.contains(1) == false);
        assert(sList3.contains(0) == false);
        System.out.println("Successfully passed all tests.");
    }
}


// PROBLEM 2

// the type for a set of strings
interface StringSet {
     int size();
     boolean contains(String s);
     void add(String s);

     //for debugging
     void print();
}

// an implementation of StringSet using a linked list
class ListStringSet implements StringSet {
    protected SNode head;

    public ListStringSet(){
        this.head = new SEmpty();
    }

    public int size(){
        return head.size();
    }
    public boolean contains(String s){
        return head.contains(s);
    }
    public void add(String s){
        SNode temp = head.add(s);
        head = temp;
    }

    public void print(){
        head.print();
    }

}

// a type for the nodes of the linked list
interface SNode {
    //#Elements in list (including and after this node)
    int size();

    //Check to see if string is in linked list
    boolean contains(String s);

    /* Take string and add it to linked list. Returns an
    SNode. */
    SNode add(String s);
    void print();
}

// represents an empty node (which ends a linked list)
class SEmpty implements SNode {
    public SEmpty() {}

    public int size(){ return 0; }
    public boolean contains(String s){ return false; }
    public SNode add(String s){
        //Add an element before the SEmpty
        return (new SElement(s, this));
    }

    public void print(){ 
        System.out.println("<empty node>");
    }

}

// represents a non-empty node
class SElement implements SNode {
    protected String elem;
    protected SNode next;
    
    public SElement(String element, SNode next_node){
        this.elem = element;
        this.next = next_node;
    }

    public int size(){ 
        return (1 + next.size()); 
    }
    public boolean contains(String s){ 
        if(elem == s)
            return true;
        else
            return next.contains(s); 
    }

    public SNode add(String s){
        //Check to see if already in list

        /*  strname.compareTo(argument)

        0       -> argument == strname
        -int    -> argument > strname
        int     -> argument < strname

        */

        

        int res = s.compareTo(elem);
        if(res == 0){      //these are the same
            return this;
        }
        else if(res < 0){   //strname comes before arugment
            return (new SElement(s, this));

        }else if(res > 0){   //this gets added after the element
            return (new SElement(elem, next.add(s)));

        }
        return this;
    }

    public void print(){
        System.out.println(elem);
        next.print();
    }
}

interface Set<T>{
     int size();
     boolean contains(T item);
     void add(T item);

     //for debugging
     void print();
}

class ListSet<T> implements Set<T>{
    protected Comparator<T> comp;
    protected Node<T> head;

    ListSet(Comparator<T> c){
        this.comp = c;
        this.head = new Empty<T>(c);
    }
    public int size(){
        return head.size();
    }
    public boolean contains(T item){
        return head.contains(item);
    }
    public void add(T item){
        Node<T> temp = head.add(item);
        head = temp;

    }

    public void print(){
        head.print();
    }

}

interface Node<T>{
    //#Elements in list (including and after this node)
    int size();

    //Check to see if item is in linked list
    boolean contains(T item);

    /* Take item and add it to linked list. Returns an
    SNode. */
    Node<T> add(T item);
    void print();
}

//empty node (ending a linked list)
class Empty<T> implements Node<T>{
    protected Comparator<T> comp;

    public Empty(Comparator<T> c) {
        this.comp = c;
    }

    public int size(){ return 0; }
    public boolean contains(T item){ return false; }
    public Node<T> add(T item){
        return (new Element<T>(item, this, comp));
    }

    public void print(){
        System.out.println("<empty>");
    }

}

//a non-empty node
class Element<T> implements Node<T>{
    protected T item;
    protected Node<T> next;
    protected Comparator<T> comp;

    public Element(T item, Node<T> next_node, Comparator<T> c){
        this.item = item;
        this.next = next_node;
        this.comp = c;
    }

    public int size(){
        return (1 + next.size());
    }
    public boolean contains(T val){
        /*  compare(T o1, T o2)

        0       -> o1 == o2
        -int    -> o1 > o2
        int     -> o1 < o2

        */

        if(comp.compare(val, item) == 0){ //match
            return true;
        }else
            return next.contains(val);
    }

    public Node<T> add(T val){
        int res = comp.compare(val, item);
        if(res == 0){
            return this;
        }else if(res < 0){
            return (new Element<T>(val, this, comp));
        }else if(res > 0){
            return (new Element<T>(item, next.add(val), comp));
        }
        return this;
    }

    public void print(){
        System.out.println(item);
        next.print();
    }
}
