
(* Exceptions *)

(* lookup a key's value in a list of key-value pairs
   (lookup 3 [(1,2); (3,4)]) returns 4
 *)


(* a version that uses the option type to signal that the key is not found *)
let rec lookup k l =
  match l with
    [] -> None
  | (k',v')::rest -> if k=k' then Some v' else lookup k rest
                                                 
(* this style of error signaling doesn't compose well... *)

(* this caller has to check for the error, only to re-propagate it to its caller *)                         
let lookupAndDouble k l =
  match (lookup k l) with
    None -> None
  | Some v -> Some (v*2)

(* here's a more complicated example:
   lookup the values for a bunch of keys,
   and signal an error if any of the lookups fails
*)                   
let rec lookupAll ks l =
  match ks with
    [] -> Some []
  | k::rest ->
     match (lookupAll rest l) with
       None -> None
     | Some resList ->
        match (lookup k l) with
          None -> None
        | Some v -> Some (v::resList)

                         
(* exceptions let you separate error handling from ordinary
program logic *)                         

exception Not_found

let rec lookupE k l =
  match l with
    [] -> raise Not_found
  | (k0,v0)::rest -> if k=k0 then v0 else lookupE k rest

(* errors implicitly propagate up the call chain *)                                                 

let lookupAndDoubleE k l =
  (lookupE k l) * 2
  

let rec lookupAllE ks l =
  List.map (fun k -> lookupE k l) ks

(* use the try expression to handle an exception *)        
           
(* try e with p1 -> e1 | ... | pn -> en *)           
let safeLookupAllE ks l =
  try
    lookupAllE ks l
  with
    Not_found -> []

                   
                   (* Parametric Polymorphism *)

let rec length l =
  match l with
    [] -> 0
  | _::t -> 1 + length t

(* type: 'a list -> int
   'a is a *type variable*
   can think of it as an extra parameter to the function
   - the caller gets to choose a type to substitute for 'a

   length [1;2;3] (* implicitly 'a is instantiated as int *)
   length ["hi"; "bye"] (* implicitly 'a is instantiated as string *)

   the type instantiation (passing the implicit type parameter)
   happens at compile time during static type inference

   note: the body of the length function is only typechecked *once*
    - when it is declared
   calls like (length [1;2;3]) are typechecked just based on the already-inferred
     type of length
     - the implementation of length is not re-checked
  
   summary: 1 piece of code, which can be safely passed arguments of
   many different types
*)                       
                       
(* don't confuse parametric polymorphism with static overloading

   static overloading: many different pieces of code, all with
   the same name, each code handling a different type

   e.g., + for ints and + for floats (in C, Java, ...)

   unlike parametric polymorphism, static overloading is just a
   syntactic convenience:  use the same name for different things
*)
   

(* Modules in OCaml *)

(*
   consider homework 1: implementing sets as lists.

   there is a big problem:

   need a way to separate *interface* from *implementation*:
   implementation: a list
   interface: a set

   ensures that sets can't have duplicates
     - and more generally, that the implementation can rely on important properties of the
       data structure being satisfied
   prevents "sets" getting confused with other data structures that are implemented as lists
   lets the implementer upgrade the implementation of sets (e.g., to use hash tables) without
     breaking callers

   in OCaml, modules (also called *structures* ) are implementations
   and module types (also called *signatures* ) are their interfaces

*)                       

module type SET = sig
  (* keeping the type t *abstract* ensures that clients cannot access its implementation details *)
  type 'a t
  val emptyset : 'a t
  val member : 'a -> 'a t -> bool
  val add : 'a -> 'a t -> 'a t
  val union : 'a t -> 'a t -> 'a t
  val toList : 'a t -> 'a list
end
                       
module Set : SET = struct

  type 'a t = 'a list
  
  let emptyset = []
  
  let rec member =
    fun x s ->
    match s with
      [] -> false
    | h::t when h=x -> true
    | h::t -> member x t

  let add =
    fun x s ->
    if member x s then s else x::s
                                   
  let rec union =
    fun s1 s2 ->
    match s1 with
      [] -> s2
    | h::t -> add h (union t s2)

  let toList s = s
end
                       
