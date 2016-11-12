
exception ImplementMe

(* Problem 1 *)

(* member searches for any one value in a set *)
(* as based on talk on Piazza, the only thing that we need to
   ensure is that the '=' operator is valid for anything that
   will be put in the set *)
let rec (member : 'a -> 'a list -> bool) = fun x s ->
  match s with
    [] -> false
  | h::t when h = x -> true
  | _::t -> member x t;;

(* this function is simple to implement given the member function.
   clearly, if an element is already member of a set, we should not
   add it. otherwise we should. clearly this keeps *)
let (add : 'a -> 'a list -> 'a list) = fun x s ->
  match (member x s) with
    false -> x::s
  | _ -> s;;

(* union takes two sets and adds all the elements of one set onto
   the other set. As mentioned in the specs, this would take 
   O(mn) time since add takes O(n) time (due to member) while the recursion 
   repeats m times. *)
let rec (union : 'a list -> 'a list -> 'a list) = fun s1 s2 ->
  match s2 with
    [] -> s1
  | h::t -> add h (union s1 t);;

(* fastUnion takes the union assuming sets are ordered.
   this utilizes the fact that we will put one of the two heads onto
   the head of the new set. => we only have to check O(m+n) times.*)
let rec (fastUnion : 'a list -> 'a list -> 'a list) = fun s1 s2 ->
  match (s1, s2) with
    ([], s2) -> s2
  | (s1, []) -> s1
  | (h1::t1, h2::t2) when h1 = h2 -> h1::fastUnion t1 t2
  | (h1::t1, h2::t2) when h1 < h2 -> h1::fastUnion t1 s2
  | (h1::t1, h2::t2) -> h2::fastUnion s1 t2;;

(* intersection uses List.filter and member to generate an intersection
   between two sets *)
(* according to Piazza, the fact that I defined 'fun x -> member x s1' is
   alright. As kindly mentioned by a TA, if member were defined to be
   member s x, we would have been able to do this without the creation
   of a new function by just using 'member s1' *)
let (intersection : 'a list -> 'a list -> 'a list) = fun s1 s2 ->
  List.filter (fun x -> member x s1) s2;;

(* converts any list into a set by removing duplicates.
   alternatively: create a new list and add all non-duplicates in. *)
let rec (setify : 'a list -> 'a list) = fun l ->
  match l with
    [] -> []
  | h::t -> add h (setify t);;

(* takes any set and returns its power set - set of all subsets *)
let rec (powerset : 'a list -> 'a list list) = fun s ->
  match s with
    [] -> [[]]
  | h::t -> let t' = powerset t in
    union (List.map (fun l -> h::l) t') t';;

(* Problem 2 *)        

(* splits any list into a tuple of lists, first satisfying the predicate, and the rest in the second *)
let rec (partition : ('a -> bool) -> 'a list -> 'a list * 'a list) = fun f l ->
  match l with
    [] -> ([], [])
  | h::t -> let (l1, l2) = partition f t in
    if f h then (h::l1, l2) else (l1, h::l2);;

(* while (p) { f x } *)
let rec (whle : ('a -> bool) -> ('a -> 'a) -> 'a -> 'a) = fun p f x ->
  match p x with
    false -> x
  | _ -> whle p f (f x);;

(* f ^ n (x) *)
(* similar to the previous instance, on Piazza, the professor allowed for us to return functions,
   defining auxiliary functions as functions we used. I could not find the solution that a TA
   suggested that apparently allows for this to be solved without the use of any function
   declaration *)
let rec (pow : int -> ('a -> 'a) -> ('a -> 'a)) = fun e f ->
  match e with
    0 -> fun x -> x
  | n -> fun x -> f ((pow (n-1) f) x);;
                     
