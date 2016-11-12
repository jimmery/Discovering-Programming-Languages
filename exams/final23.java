interface Greeter {
    void greet();
}

class Person implements Greeter {
    public void greet() { this.hello(new Integer(3)); }
    public void hello(Object o) { System.out.println("hello object"); }
    //public void hello(String s) { System.out.println("hello " + s); }
}

class CSPerson extends Person {
    public void hello(Object o) { System.out.println("hello world!"); }
}

class FrenchPerson extends Person {
    public void hello(Object o) { System.out.println("bonjour object"); }
    public void hello(String s) { System.out.println("bonjour " + s); }
}

class greetTest {
    public static void main ( String[] args ) {
	/* a - expected solution: no error. 'hello world!'
	   this will call Person's greet method, which will 
	   call CSPerson's hello, resulting a print of 'hello world!'
	   this checks out with what is printed. */
	Person pa = new CSPerson();
	pa.greet();

	/* b - expected solution: no error. 'hello world!' */
	CSPerson pb = new CSPerson();
	pb.greet();

	/* c - expected solution: compile error. 
	   Greeter has no hello method. */
	Greeter gc = new FrenchPerson();
	//gc.hello("joe");

	/* d - expected solution: no error. 'bonjour joe' */
	/* okay the actual answer here was 'bonjour object. 
	   so note to self, a Person only sees the hello(Object) method.
	   so it does not see the overloaded hello method with the String. 
	   testing it with other cases, we see that if I add 
	   hello(String) to Person, it is able to use FrenchPerson's 
	   hello(String) method. 
	   Therefore, I should remember that even though these functions
	   have the same name, they actually do not have any relation 
	   to each other, other than the fact that they have the same name.
	*/
	
	Person pd = new FrenchPerson();
	pd.hello("joe");

	FrenchPerson pf = new FrenchPerson();
	pf.hello("joe");
    }
}

/* Problem 3: uses pb above.
   CSPerson p = new CSPerson();
   p.greet();

   a. Suppose Java lacked support for inheritance, but everything else
      remains intact / unchanged. That is, extends declares a subtype 
      relation, but not an inheritance relationship. 

      Here, the problem with this would be that CSPerson would no longer
      have a greet method, meaning that CSPerson would not be able to 
      properly implement Greeter. This means that CSPerson class would not
      pass the static type checker. 

   b. Suppose Java lacked support for dynamic dispatch, but everything 
      else about the language remains unchanged. What effect? 

      So here I thought that there would be no change, simply because of
      the fact that p was declared as a CSPerson, so all functions would
      know that their own functions are declared as CSPerson's, so 
      everything would operate as intended. 

      However, what I did not realize was the greet was a method that is 
      in Person, meaning that at compile time, the function call to 
      hello would also be Person's hello. This is because, if every
      function call is known at compile time, there is no way that 
      the Person's hello call can be anything other than that of the 
      Person's. Thus, this will run Person's hello method. 

   c. Suppose Java lacked support for static overloading, but everything
      else is kept the same. 

      Here, the main point is that there is only a single hello invocation. 
      So static overloading does not do anything. 

      I feel as though if this were FrenchPerson, this would result in
      a compiler error, since we have two functions of the same name, 
      but of different types. 
*/
