(* Problem 1: Vectors and Matrices *)

(* type aliases for vectors and matrices *)            
type vector = float list                                 
type matrix = vector list

let (vplus : vector -> vector -> vector) = fun v1 v2 ->
  List.map2 (+.) v1 v2;;

let (mplus : matrix -> matrix -> matrix) = fun m1 m2 ->
  List.map2 (vplus) m1 m2;;

let (dotprod : vector -> vector -> float) = fun v1 v2 ->
  List.fold_right (+.) (List.map2 ( *. ) v1 v2) 0.0;;

let (transpose : matrix -> matrix) = fun m ->
  (* initMatrix essentially generates an initial matrix that contains 
     an empty list for each column of m. That is, in an mxn matrix, 
     it generates n empty lists, for future use. *)
  let initMatrix m = 
    match m with
      [] -> []
    | h::_ -> List.fold_right (fun elem initMat -> []::initMat) h [] in

  (* rowTranspose is essentially the transpose function for a single 
     row vector. 
     It turns an 1xn vector into n 1x1 row vectors, which represents
     a single nx1 column vector. *)
  let rowTranspose vec = 
    List.fold_right (fun elem transp -> [elem]::transp) vec [] in

  (* merge takes two list of lists of the same size and merges them *)
  let merge v1 v2 = 
    List.map2 (@) v1 v2 in

  (* we rowTranspose each row and 
     merge them onto the init matrix for the transpose 
   *)
  List.fold_right (fun elem transp -> merge (rowTranspose elem) transp) 
    m (initMatrix m);;

(* matrix multiplication *)
(* done using the fact that we know that the (i,j)th element 
   of a matrix multiplication is the dot product of the 
   ith row of the first matrix and the jth column of the second
   matrix. We use the transpose to get the column vector. *)
let (mmult : matrix -> matrix -> matrix) = fun m1 m2 ->
  let m2' = transpose m2 in
  let rowmult = fun v1 mat2 -> 
    List.fold_right (fun v2 rf -> (dotprod v1 v2)::rf) mat2 [] in
  List.fold_right (fun v1 mf -> (rowmult v1 m2')::mf) m1 [];;

        
(* Problem 2: Calculators *)           
           
(* a type for arithmetic expressions *)
type op = Plus | Minus | Times | Divide
type exp = Num of float | BinOp of exp * op * exp

(* helper function to operate on two floats *)
let operate f1 op f2 =
    match op with
      Plus -> f1 +. f2
    | Minus -> f1 -. f2
    | Times -> f1 *. f2
    | Divide -> f1 /. f2;;

(* just uses the operate helper function above to determine the 
   value of any expression *)
let rec (evalExp : exp -> float) = fun e -> 
  match e with 
    Num n -> n
  | BinOp (e1, op, e2) -> operate (evalExp e1) op (evalExp e2);;

(* a type for stack instructions *)	  
type instr = Push of float | Swap | Calculate of op

(* utilizes a stack to operate on a list of instructions. *)
let (execute : instr list -> float) = fun l ->
  (* takes a stack and returns a tuple containing the top of the stack and the 
     remainder of the stack after popping the top off. *)
  let popStack stack = 
    match stack with
      (* based on no stack underflow, this case SHOULD never run *)
      [] -> (0.0, [])
    | h::t -> (h, t)
  in
  
  (* takes an instruction and executes the single instruction. 
     Push just pushes onto the top of the stack. 
     Swap pops the top two off the stack and pushes them back in reverse order.
     Calculate pops the top two off the stack and pushes their 
     result on the stack.
   *)
  let stackOp i stack = 
    match i with
      Push f -> f::stack
    | Swap -> let (f1, s1) = popStack stack in
        let (f2, s2) = popStack s1 in 
        f2::f1::s2
    | Calculate op -> let (f1, s1) = popStack stack in 
        let (f2, s2) = popStack s1 in 
        (operate f2 op f1)::s2 
  in 
  
  (* an auxiliary function that contains the main function body of execute. 
     essentially just recursively calls stackOp on instructions
     in the correct order, returning the top of the stack when
     finishing instructions. *)
  let rec aux l stack = 
    match l with
      [] -> let (f1, _) = popStack stack in f1
    | h::t -> let s = (stackOp h stack) in (aux t s)
  in
  aux l [];;

(* takes a Binary Op expression and creates an instruction list onto a stack
   essentially just does a post-order reordering of the expression. 
   i.e. Exp1 Op Exp2 -> Exp1 Exp2 Op. *)
let rec (compile : exp -> instr list) = fun e ->
  match e with
    Num f -> [Push f]
  | BinOp (e1, op, e2) -> (compile e1)@(compile e2)@[Calculate op];;

(* takes an instruction list and creates an expression of Binary Operations.
   Since this follows a very similar construction to execute, all the helper
   functions are named in the exact same way 
   (but do slightly different things). *)
let (decompile : instr list -> exp) = fun l ->
  (* does almost the same thing as popStack in execute. 
     the main difference is the "base case" which should never be run, 
     exhibiting that the stack will contain expressions, instead of floats. *)
  let popStack stack = 
    match stack with
      [] -> (Num 0.0, []) (* this case should be replaced by an error.  *)
    | h::t -> (h, t) 
  in

  (* in this version of stackOp, the Push and the Swap cases are the same, 
     but the calculate case stores an expression with the BinOp in 
     question, instead of evaluating the expression. *)
  let stackOp i stack = 
    match i with 
      Push f -> (Num f)::stack
    | Swap -> let (e1, s1) = popStack stack in 
        let (e2, s2) = popStack s1 in 
        e2::e1::s2
    | Calculate op -> let (e1, s1) = popStack stack in 
        let (e2, s2) = popStack s1 in 
        BinOp(e2, op, e1)::s2
  in

  (* the exact same function as with execute. *)
  let rec aux l stack = 
    match l with
      [] -> let (e, _) = popStack stack in e
    | h::t -> let s = (stackOp h stack) in (aux t s) in 
  aux l [];;

(* EXTRA CREDIT *)        
let rec (compileOpt : exp -> (instr list * int)) = fun e ->
  match e with
    Num f -> ([Push f], 1)
  | BinOp (e1, op, e2) -> 
      (* s1, s2 determine how much of a stack each expression needs. *)
      let (c1, s1) = compileOpt e1 in
      let (c2, s2) = compileOpt e2 in
      
      (* the optimal compiler decision between two expressions in an operation 
         is to choose the maximum sized stack and evaluate that first, since
         after evaluating it, it just becomes a single value on the stack. 
         therefore, the eventual stack usage is either the stack usage of the
         largest sized stack usage of the expressions, 
         or 1 (result of max stack op)
         + the minimum (remaining operation after the max stack op is done). 
         the only case where 1 + min > max is when the two expressions
         use the same amount of stack space, so min = max. *)
      if s1 = s2 then ((c1)@(c2)@[Calculate op], s1+1)

      (* default case similar to compile *)
      else if s1 > s2 then ((c1)@(c2)@[Calculate op], s1)

      (* swapped expressions with and without commutative operations. *)
      else if ((op = Minus) || (op = Divide)) 
           then ((c2)@(c1)@[Swap; Calculate op], s2)
           else ((c2)@(c1)@[Calculate op], s2);;