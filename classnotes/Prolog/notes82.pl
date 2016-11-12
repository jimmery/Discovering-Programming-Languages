/* GNU Prolog */

/* A set of facts about the CS department courses. */


/*

lowercase identifiers (cs31, cs32) are *atoms*

 - like the Data values in MOCaml
    - Leaf. 

  - prereq is an atom that is used as a *predicate*
    - an uninterprete function that takes 0 or more terms as arguments and 
      returns a boolean. 

*/


prereq(cs31, cs32).
prereq(cs32, cs33).
prereq(cs31, cs35L).

prereq(cs32, cs111).
prereq(cs33, cs111).
prereq(cs35L, cs111).

prereq(cs32, cs118).
prereq(cs33, cs118).
prereq(cs35L, cs118).
prereq(cs111, cs118).

prereq(cs32, cs131).
prereq(cs33, cs131).
prereq(cs35L, cs131).

prereq(cs32, cs132). 
prereq(cs35L, cs132). 
prereq(cs131, cs132). 
prereq(cs181, cs132). 

/* 

operations in the world of prolog: 

consult(notes82) will load this file. 

prereq(cs31, cs32) returns true. 
prereq(cs31, X) returns X = cs32. 
prereq(X, cs111), prereq(X, cs131) returns all X that are prereqs for both. 
prereq(cs31, X), prereq(X, cs131) returns all X that require cs31 
                                  xsand cs131 requires

*/

/* Rules allow you to derive new facts. */

/* X is a prereq of a prereq of Y */
/* prereq2 is true if there exists Z s.t.prereq(X,Z) and prereq(Z,Y). */
prereq2(X,Y) :- prereq(X,Z), prereq(Z,Y).

/* adds a new rule that says that prereq2 is true if directly related. */
prereq2(X,Y) :- prereq(X,Y).

/* X is required before you can take Y. */
/* recursive definition. */
prereqTransitive(X,Y) :- prereq(X,Y).
prereqTransitive(X,Y) :- prereq(X,Z), prereqTransitive(Z,Y).
/* order actually matters, due to search orders. */
/* requires knowledge of search strategies. */


/*

purely symbolic language. 
+(1,2) = 3 will return false, because they are not symbolically the same. 

so far we have seen two kinds of terms: 

- atoms (lowercase identifiers)
- variables (uppercase identifiers)

- uninterpreted function: 
  atom(term1, term2, ..., termN). 

  (like in in MOCaml): Node(Leaf, 1, Leaf)
  we can do this in Prolog too. 

here are some examples: 
| ?- cons(1,cons(2,cons(2,nil))) = cons(1,X).

X = cons(2,cons(2,nil))

| ?- .(1, .(2, .(3, []))) = [1,2,3].

yes

*/

/* in OCaml. 

let rec append l1 l2 = 
  match l1 with
    [] -> l2
  | h::t -> h::(append t l2);;


in Prolog the . function symbol plays the role of :: in Ocaml.
the list [1,2,3] is really just this term: 
.(1, .(2, .(3, [])))
*/

app([], L2, L2).
app(.(H,T), L2, .(H,L)) :- app(T, L2, L).

/* this kinda makes sense. 
this means that all cases where app([], L2, L2) are true. 
and that app(.(H,T), L2, .(H,L)) is only true if app(T,L2,L). is true. 
so we kinda just use pattern matching again.

so we do not actually define a function that returns a value, 
but simply define an atom that abstractly contains all values that we
want to find, and we use the power of Prolog to find which one we want.
*/

/* in OCaml. 

let rec contains x l = 
  match l with
    [] -> false
  | h::t -> x=h || contains x t;;

notice the only thing that cannot be translated is the fact that 
there is a false case. all of our predicates only have true cases, 
so we do not have a base case. 
*/

contains(X, .(X,_)). 
contains(X, .(_,T)) :- contains(X,T).

/* in OCaml. 

let rec reverse l = 
  match l with 
    [] -> []
  | h::t -> (reverse t)@[h];;

*/

rev([], []).
rev(.(H,T), L2) :- rev(T, L0), app(L0, [H], L2).

/* wolf, goat, cabbage problem: 

   Trying to get from west back to the east bank of a river, 
   with the following constraints: 

   1. Your boat only fits one item at a time. 
   2. You can't leave the wolf and goat alone together. 
   3. You can't leave the goat and cabbage alone together. 

   How do you get across the river? 


   state: [person loc, wolf loc, goat loc, cabbage loc].

   initial state: [west, west, west, west].
   goal: [east, east, east, east]. 

   legal moves: person, wolf, goat, cabbage. 
*/

opposite(west, east).
opposite(east, west).

move([P,W,G,C], person, [Q,W,G,C]) :- opposite(P,Q).
move([P,W,G,C], wolf, [Q,Q,G,C]) :- P=W, opposite(G,C), opposite(P,Q).
move([P,W,G,C], goat, [Q,W,Q,C]) :- P=G, opposite(P,Q).
move([P,W,G,C], cabbage, [Q,W,G,Q]) :- P=C, opposite(W,G), opposite(P,Q).

puzzle(End, End, []).
puzzle(Start, End, .(Move, Moves)) :- 
    move(Start, Move, S), puzzle(S, End, Moves). 

/* 

| ?- length(Moves, X), X #< 8, puzzle([west, west, west, west], [east, east, east, east], Moves).

Moves = [goat,person,wolf,goat,cabbage,person,goat]
X = 7 ? 


*/
