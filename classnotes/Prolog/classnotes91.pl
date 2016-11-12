

/* Arithmetic

not reversible in general

- solving Diophantine equations is undecidable
   poly(x1,...xn) = 0

- solvable but intractable:
  - factoring large numbers

- solvable efficiently
  - linear inequalities
    - linear programming, simplex algorithm

Upshot: Prolog has regular (non-reversible)
        arithmetic

	Term1 is Term2:
	  - evaluates the right-hand-side using ordinary arithmetic
	  - pattern matches the result with Term1

	Term1 < Term2 [and the same for other comparison operators]:
	  - evaluates both operands using ordinary arithmetic        
      - succeeds or fails by the usual semantics of <
*/

temp(C,F) :- F is 1.8 * C + 32.0.

len([], 0).
len(.(_,T), I) :- len(T, I2), I is I2+1.


/* N-Queens */

/* how to represent each queen?
   
   queen(R,C), where R is a row (1..8)
     and C is a column (1..8)

*/

attacks(queen(R,_), queen(R,_)).

attacks(queen(_,C), queen(_,C)).

attacks(queen(R1,C1), queen(R2,C2)) :-
	RDiff is R1-R2, CDiff is C1-C2, 
	RDiff = CDiff.

attacks(queen(R1,C1), queen(R2,C2)) :-
	RDiff is R2-R1, CDiff is C1-C2, 
	RDiff = CDiff.

noAttack(_, []).
noAttack(Q, .(Q1,QRest)) :- 
	noAttack(Q, QRest), \+(attacks(Q, Q1)).

/* nqueens([queen(1,2), queen(4,5), ...]) */
nqueens([]).

nqueens(.(Q,Qs)) :- 
	nqueens(Qs),
	Q = queen(R,C),
	member(R, [1,2,3,4,5,6,7,8]), 
	member(C, [1,2,3,4,5,6,7,8]), 
	noAttack(Q,Qs).


