type nat = Z | S of nat;;

let rec convert n =
  match n with
    Z -> 0
  | S n' -> 1 + convert n';;
  
let rec add m n =
  match m with
    Z -> n
  | S m' -> add m' (S n);;

let rec leq m n =
  match (m,n) with
    (Z,_) -> true
  | (_,Z) -> false
  | (S m', S n') -> leq m' n';;
