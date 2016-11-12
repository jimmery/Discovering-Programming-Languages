class MyInt {
    int i;
    MyInt(int k) { this.i = k; }
    void swap1(MyInt j) {
	int tmp = j.i;
	j = new MyInt(this.i); // reassigns the pointer, input is unchanged.
	this.i = tmp; // changes this i.
    }

    void swap2(MyInt j) {
	MyInt tmp = j; // sets tmp as the pointer in j. 
	j.i = this.i; // changes j's AND tmp's integer. 
	this.i = tmp.i; // this does not change. 
    }

    void swap3(int j) {
	int tmp = j;
	j = this.i; // this does not change the input. 
	this.i = tmp; // changes this. 
    }
}

class MyIntTest {
    public static void main(String[] args) {
	MyInt m1 = new MyInt(3);
	MyInt m2 = new MyInt(4);

	// a. 
	m1.swap1(m2); 
	int a1 = m1.i; // expected: 4
	int b1 = m2.i; // expected: 4
	System.out.println("new m1: " + a1 + " new m2: " + b1);

	// b.
	m1.i = 3; // just to reset m1

	m1.swap2(m2);
	int a2 = m1.i; // expected: 3
	int b2 = m2.i; // expected: 3
	System.out.println("new m1: " + a2 + " new m2: " + b2);

	// c.
	m2.i = 4; // just to reset m2. 
	
	m1.swap3(m2.i);
	int a3 = m1.i; // expected: 4
	int b3 = m2.i; // expected: 4
	System.out.println("new m1: " + a3 + " new m2: " + b3);
    }
}

/* Java is always pass by value. 
   This value is a pointer in the case of objects, though. 
   So changing the object that is pointed at will change value, but it still
   points to the same object. 
   If we try to set it to a different object, this will not work. */
