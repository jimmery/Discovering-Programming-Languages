let rec genMap l =
  let rec generator v m = 
    match m with
      [] -> [(v, 1)]
    | (value, reps)::t -> 
        if v=value then (value, reps+1)::t
               else (value, reps)::(generator v t)
  in
  List.fold_right (generator) l [];;
  
  (*match l with 
    [] -> []
  | h::t -> generator h (genMap t);; *)

let rec getScoreMap m =
  List.fold_right (fun elem sum -> (match elem with (_, r) -> r*r + sum)) m 0;;
  
  (*match m with
    [] -> 0
  | (_, r)::t -> r*r + getScoreMap t;; *)

let getScoreList l = getScoreMap (genMap l);;

exception NotEnoughNumbers

let removeMax m = 
  match m with 
    [] -> raise NotEnoughNumbers
  | (v, r)::t -> 0;;


let rec zZz n =
  match n with
    0 -> ""
  | _ -> "zZ" ^ (zZz (n-1));;
