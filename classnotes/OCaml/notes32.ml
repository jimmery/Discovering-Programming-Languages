
(* binary trees *)

type btree = Leaf | Node of btree * int * btree

let rec height t =
  match t with
    Leaf -> 1
  | Node(left, _, right) ->
     1 + (max (height left) (height right))
(* alternate implementation: *)
     (* let hl = height left in *)
     (* let hr = height right in *)
     (* if hl > hr *)
     (* then 1 + hl *)
     (* else 1 + hr *)

(* insert into a binary search tree *)           
let rec insert (n:int) (t:btree) =
  match t with
    Leaf -> Node(Leaf, n, Leaf)
  | Node(left, value, right) ->
     if n < value then
       Node(insert n left, value, right)
     else if n = value then t
     else Node(left, value, insert n right)

