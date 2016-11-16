let repl1 c =
  match c with
    'e' -> 'r'
  | 'g' -> 'd'
  | _ -> c;;

let rec parse s =
  try
    let l = String.length s in
    let i = String.index s ' ' in
    let word = String.sub s 0 i in
    let rest = String.sub s (i+1) (l-i-1) in
    word::(parse rest)
  with
    Not_found -> [s];;

let repl2 c =
  match c with
    'a' -> 'd'
  | 'd' -> 'a'
  | _ -> c;;
  
let flop word =
  try
    let _ = String.index word 'a' in
    let _ = String.index word 'd' in
    String.map repl2 word
  with
    Not_found -> word;;
  
let convert s =
  let words = parse s in
  let s2 = List.fold_right (fun w1 s -> w1^" "^s) (List.map flop words) "" in
  String.trim(String.map repl1 s2);;

let sexify s =
  let l = String.length s in
  if l < 2 then s else 
    let start = String.sub s 0 2 in
    match start with
      "ex" -> "s"^s
    | _ -> s;;
  
let sexify_all s =
  let words = parse s in
  String.trim (
      List.fold_right
	(fun w1 s -> w1^" "^s) (List.map sexify words) "");;

let rec dec_to_bin n =
  match n with
    0 -> 0
  | 1 -> 1
  | _ -> n mod 2 + 10 * dec_to_bin (n/2);;
