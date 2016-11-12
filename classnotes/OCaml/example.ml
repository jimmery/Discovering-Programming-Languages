let rec isEven =
  (function n -> 
      match n with 
         0 -> true
       | 1 -> false
       | _ -> if n>0 then isEven (n-2) else isEven (n+2))

(* handling negative numbers too *)    
let rec isEven2 =
  (function n -> 
      match n with 
         0 -> true
       | 1 -> false
       | _ when n>0 -> isEven2 (n-2)
       | _ -> isEven2 (n+2))


let rec sumLst =
 function lst ->
   match lst with
     [] -> 0
   | h::t -> h + sumLst t

(* pattern for a list of at least 2 elements:

   h1::(h2::t)

*)

(* everyOther [1;2;3;4] should return [1;3] *)
let rec everyOther =
   function lst ->
      match lst with
         [] -> []
      |  [h] -> [h]
      | h1::_::t -> h1::(everyOther t)

			  


