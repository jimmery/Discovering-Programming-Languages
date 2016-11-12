exception Funsies

let rec inc x = x + 1;;

let rec fact n = 
  match n with
    0 -> 1
  | x when x>0 -> x*fact(x-1)
  | _ -> raise Funsies;;


