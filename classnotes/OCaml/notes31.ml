
(* Higher-order functions *)

(* a function that takes another function as an argument *)

(* map, filter, fold_right *)

(* reverse [1;2;3;4] is [4;3;2;1] *)
let reverse l =
  List.fold_right
      (* 1    [4;3;2] *)
    (fun elem reversedRest -> reversedRest@[elem]) l []

let contains x l =
  List.fold_right
    (fun elem isInRestOfList -> isInRestOfList || elem=x ) l false

let rec twoToN n =
  match n with
    1 -> []
  | _ -> (twoToN (n-1)) @ [n] 
    
(* checks if n is prime *)    
let isPrime n =
  let l = twoToN (n-1) in
  (* (List.filter (fun elem -> n mod elem = 0) l) = [] *)
  List.fold_right
    (fun elem nothingDividesN -> (n mod elem != 0) && nothingDividesN)
    l true
    
(* return a list of all elements of l that are prime *)
let primes l =
  List.filter isPrime l 

  
              
(* Datatypes *)

(* “tagged union”:
   - union because it's an OR of several cases
   - tagged because each case has label/tag *)              
type optInt = Null | Nonnull of int
                             
let addOptInt (oi1 : optInt) (oi2 : optInt) =
  match (oi1, oi2) with
    (Nonnull i1, Nonnull i2) -> Nonnull (i1 + i2)
  | _ -> Null
           
let fOptInt f oi1 oi2 =
  match (oi1, oi2) with
    (Nonnull i1, Nonnull i2) -> Nonnull (f i1 i2)
  | _ -> Null
           

type 'a option = None | Some of 'a

(* return an int option with the index of the first occurrence
   of x in l *)                                  
let find x l =
  let rec aux index l =
    match l with
      [] -> None
    | h::t -> if h=x then Some index else aux (index+1) t
  in aux 0 l

(* recursive datatypes *)

(* lists *)
type 'a mylist = Empty | Node of 'a * 'a mylist

(* binary trees with data in the internal nodes *)
type btree = Leaf | TNode of btree * btree * int

let rec treeSize t =
  match t with
    Leaf -> 0
  | TNode(left, right, d) -> 1 + treeSize left + treeSize right

(* binary trees with data in the leaves *)
type btree2 = Leaf2 of int | TNode2 of btree2 * btree2                                              
