
(* Tail Recursion *)

let rec fact n =
	match n with
	0 -> 1
	| _ -> n * fact(n-1)

(*

(fact 3)                   ==> 6
   [(n,3)]
   n * fact(n-1)           ==> 6
       [(n,2)]
       n * fact(n-1)       ==> 2
           [(n,1)]
           n * fact(n-1)   ==> 1
               [(n,0)]


factorial in C:

int fact(int n) {
	int res = 1;
	while(n > 0) {
	   res *= n;
	   n--;
	}
	return res;
}

*)	

let factTR n =
	let rec helper n res =
	match n with
	0 -> res
	| _ -> helper (n-1) (res * n)
in helper n 1

(*

(fact 3)            ==> 6  
   helper 3 1
   [(n,3);(res,1)]  ==> 6
   helper 2 3
   [(n,2);(res;3)]  ==> 6
   helper 1 6
   [(n,1);(res,6)]  ==> 6
   helper 0 6
   [(n,1);(res,6)]  ==> 6


*)

(* Tail call optimization 

A *tail call* is a function call that is the last operation
done dynamically inside some function body.

A function is *tail recursive* if all of its recursive calls
are tail calls.

If a function is tail recursive, then it is safe for the compiler
to reuse the existing stack space upon a recursive call, by overwriting
the old values of the variables with new values, instead of growing the stack.
The upshot is that we are guaranteed to have constant stack space usage, and we
save the time that would be spent on allocating and deallocating stack frames.

*)


let rec sumLst l =
	match l with
	[] -> 0
	| h::t -> h + sumLst t

let sumLstTR l =
	let rec helper l acc =
		match l with
		[] -> acc
		| h::t -> helper t (h+acc)
	in helper l 0

(* this function is equivalent to fold_right; it is not tail recursive *)
let rec fold f l b =
	match l with
		[] -> b
		| h::t -> f h (fold f t b)	 	


(* the two functions below are equivalent to fold_left 
   except fold_left takes l and b in the opposite order

   they are tail recursive
*)

let foldTR f l b =
	let rec helper l acc =
		match l with
		[] -> acc
		| h::t -> helper t (f acc h)
	in helper l b

let rec foldTR2 f l b =
	match l with
	  	[] -> b
	  	| h::t -> foldTR2 f t (f b h)











