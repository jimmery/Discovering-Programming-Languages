interface Bool {
    Bool logicalAnd(Bool b);
    void ifThenElse(CodeBlock b1, CodeBlock b2);
}

interface CodeBlock { void execute(); }

class True implements Bool {
    public Bool logicalAnd(Bool b) {
	return b;
    }

    public void ifThenElse(CodeBlock b1, CodeBlock b2) {
	b1.execute();
    }
}

class False implements Bool {
    public Bool logicalAnd(Bool b) {
	return this;
    }

    public void ifThenElse(CodeBlock b1, CodeBlock b2) {
	b2.execute();
    }
}

class BoolTest {
    public static void main(String[] args) {
	Bool b1 = new False();
	Bool b2 = new True();
	Bool b3 = b1.logicalAnd(b2);
	b3.ifThenElse(
		      new CodeBlock() {
			  public void execute() {System.out.println("hi!");}},
		      new CodeBlock() {
			  public void execute() {System.out.println("bye!");}}
		      );
    }
}

interface LoopGuard {
    Bool evaluate();
}

class Loops {
    public void whileTrue(LoopGuard guard, CodeBlock body) {
	Bool b = guard.evaluate();
	/*b.ifThenElse
	    (new CodeBlock() {
		    public void execute() {
			body.execute();
			new Loops().whileTrue(guard, body);
		    }
		},
		new CodeBlock() {
		    public void execute() {}
		}
		);*/

	b.ifThenElse( () -> {
		body.execute();
		new Loops().whileTrue(guard, body);
	    },
	    () -> {});
    }
}

class LoopTest {
    public static void main (String[] args) {
	Loops l = new Loops();
	l.whileTrue(
		    ()-> new False(),
		    () -> System.out.println("hi") );
    }
}
