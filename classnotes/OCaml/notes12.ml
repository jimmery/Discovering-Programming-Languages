
(* zip([1;2;3], [4;5;6])
    returns [(1,4);(2,5);(3,6)] *)
let rec zip = 
  (function (l1,l2) ->
	    match (l1,l2) with
	      ([],[]) -> []
	    | (h1::t1, h2::t2) ->
	       (h1,h2)::zip(t1,t2)
  )


let rec unzip =
  function lst ->
    match lst with
      [] -> ([], [])
   |  (h1,h2)::t ->
       let (l1,l2) = unzip t in
       (h1::l1, h2::l2)

		       
