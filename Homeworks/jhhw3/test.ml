
(* A simple test harness for the MOCaml interpreter. *)

(* put your tests here:
   each test is a pair of a MOCaml declaration and the expected
   result:
     - the MOCaml declaration is a string of exactly what you would type into the interpreter prompt,
       without the trailing ";;"
     - the expected result is a string of exactly what you expect the interpreter to print as a result
   use the string "dynamic type error" as the result if a DynamicTypeError is expected to be raised.
   use the string "match failure" as the result if a MatchFailure is expected to be raised.
   use the string "implement me" as the result if an ImplementMe exception is expected to be raised

   call the function runtests() to run these tests
*)
let tests = [
    (* YOU NEED TO ADD A LOT MORE TESTS! *)
		("3", "3"); 
		("false", "false");
                ("-3", "-3");
		("let x = 34", "val x = 34");
                ("let b = true", "val b = true");
		(*("y", "dynamic type error");*)
		("x + 4", "38");
                ("b + 4", "dynamic type error");
                ("-b", "dynamic type error");

                ("let y = 3", "val y = 3");
                ("let add3 = function x -> x + y", "val add3 = <fun>");
                ("add3 2", "5");
                ("let y = 5", "val y = 5");
                ("add3 1", "4");
                ("x", "34");
 
                ("if x>0 then x-1 else x+1", "33");
                ("if x then x-1 else x+1", "dynamic type error");

                ("(function x -> (function y -> x*y) x) 5", "25");
                ("let square = (function x -> x*x)", "val square = <fun>");
                ("(function x -> (square x)) 5", "25");

                ("let rec double i = i*2", "val double = <fun>");
                ("let rec twice f = function x -> f (f x)", "val twice = <fun>");
                ("(twice double) 10", "40");

                ("let rec fact n = match n with 0 -> 1 | _ -> n*fact(n-1)", "val fact = <fun>"); 
                ("fact 0", "1"); ("fact 1", "1"); ("fact 4", "24"); 
                ("let rec fact n = if (n = 0) then 1 else (n*fact (n-1))", "val fact = <fun>"); 
                ("fact 0", "1"); ("fact 1", "1"); ("fact 4", "24");

                ("let add = function (x,y) -> x+y", "val add = <fun>");
                ("add (1,2)", "3"); 
                
                ("let getOpt = function x -> match x with None -> 0 | Some v -> v", "val getOpt = <fun>");
                ("getOpt None", "0"); ("getOpt (Some 2)", "2");
                
                
                
                
                
                
                ("let rec f ((x,y),(z,a)) = x+y+z+a", "val f = <fun>");
                ("f ((1,2),(4,5))", "12");
                
                
                
                
                
                
                
                
                
                ("let two = 2", "val two = 2");
                ("let addTwo = function x -> x + two", "val addTwo = <fun>");
                ("addTwo 5", "7");
                ("let two = 3", "val two = 3");
                ("addTwo 5", "7");
                ("let add = function a -> function b -> a + b", "val add = <fun>");
                ("add 10 (-3)", "7");
                ("let p = (1, 2)", "val p = (1, 2)");
                ("let leaf = Leaf", "val leaf = Leaf");
                ("let node = Node(Leaf, 1, Leaf)", "val node = Node (Leaf, 1, Leaf)");
                ("match x with 34 -> true | _ -> false", "true");
                ("match x with 35 -> true | _ -> false", "false");
                ("if true then 1 else 0", "1");
                ("match p with (a, b) -> a + b", "3");
                ("match p with (a, b, c) -> a + b + c", "match failure");
                ("match node with Node(l, v, r) -> (l, r)", "(Leaf, Leaf)");
                ("let iffPositive = function x -> if x > 0 then x else false", "val iffPositive = <fun>");
                ("iffPositive 3", "3");
                ("iffPositive (-3)", "false");
                ("let rec sumTree n = match n with Leaf -> 0 | Node(l, v, r) -> v + (sumTree l) + (sumTree r)", "val sumTree = <fun>");
                ("let a = Node(Leaf, 2, Leaf)", "val a = Node (Leaf, 2, Leaf)");
                ("let b = Node(Leaf, 3, Leaf)", "val b = Node (Leaf, 3, Leaf)");
                ("let c = Node(a, 11, b)", "val c = Node (Node (Leaf, 2, Leaf), 11, Node (Leaf, 3, Leaf))");
                ("let d = Node(Leaf, 5, Leaf)", "val d = Node (Leaf, 5, Leaf)");
                ("let root = Node(c, 100, d)", "val root = Node (Node (Node (Leaf, 2, Leaf), 11, Node (Leaf, 3, Leaf)), 100, Node (Leaf, 5, Leaf))");
                ("sumTree root", "121");
                ("let rec fib x = match x with 0 -> 0 | 1 -> 1 | n -> (fib (n-1)) + (fib (n-2))", "val fib = <fun>");
                ("fib 19", "4181");
                ("let rec fibIt a = function b -> function n -> if n > 0 then (fibIt b (a+b) (n-1)) else a", "val fibIt = <fun>");
                ("fibIt 0 1 19", "4181")
		]

(* The Test Harness
   You don't need to understand the code below.
*)
  
let testOne test env =
  let decl = main token (Lexing.from_string (test^";;")) in
  let res = evalDecl decl env in
  let str = print_result res in
  match res with
      (None,v) -> (str,env)
    | (Some x,v) -> (str, Env.add_binding x v env)
      
let test tests =
  let (results, finalEnv) =
    List.fold_left
      (fun (resultStrings, env) (test,expected) ->
	let (res,newenv) =
	  try testOne test env with
	      Parsing.Parse_error -> ("parse error",env)
	    | DynamicTypeError _ -> ("dynamic type error",env)
	    | MatchFailure -> ("match failure",env)
	    | ImplementMe s -> ("implement me",env) in
	(resultStrings@[res], newenv)
      )
      ([], Env.empty_env()) tests
  in
  List.iter2
    (fun (t,er) r ->
      let out = if er=r then "ok" else "expected " ^ er ^ " but got " ^ r in
      print_endline
	(t ^ "....................." ^ out))
      tests results

(* CALL THIS FUNCTION TO RUN THE TESTS *)
let runtests() = test tests
  
