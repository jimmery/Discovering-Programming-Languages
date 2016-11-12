/* 
   Going back and forth is the basis of Prolog. 

   This is difficult, when we get into the realm of MATH. 
   Primality testing is actually polynomial time. 
   Factoring is hard (while multiplication is easy). 

   Arithmetic: 
   not reversible in general
   - solving Diophantine equations is undecidable. 
      poly(x1,...,xn) = 0

   - solvable but intractable: 
      - factoring large numbers. 

   - solvable efficiently
      - solving linear inequalities. 
        - linear programming, simplex algorithm. 

   Upshot: Prolog has regular (non-reversible) arithmetic. 

   is is used to evaluate arithmetic. we can also use <, >
   X is 1+2*3. -> X = 7.
   8 is 1+2*3. -> no. 

   X < 4. -> exception. 
   4 < 4*5. -> exception. 
*/

/* this is not reversible. 
   we can go from C to F, but not the other way around. 

| ?- temp(0.0, F).

F = 32.0

yes
| ?- temp(C, 32.0).
uncaught exception: error(instantiation_error,(is)/2)

*/
temp(C,F) :- F is 1.8 * C + 32.0.

len([], 0).
len(.(_,T), L) :- len(T, L2), L is (1 + L2).
/* using L2 is L-1 makes this an error, since, L can be a variable. */
/* this is necessary for determining L2 before our arithmetic statement. */

/* N-Queens */

/* N-Queens problem: 
     how to place N queens on a chessboard where no queen attacks any other? 

   how to represent each queen? 

   queen(R,C), where R is a row (1..8)
     and C is a column (1..8). 
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
noAttack(Q, .(Q1, QRest)) :-
    noAttack(Q, QRest), \+(attacks(Q, Q1)).

/* nqueens([queen(1,2), queen(4,5), ...]) */
nqueens([]).
nqueens(.(Q,Qs)) :-
    nqueens(Qs),
    Q = queen(R,C), 
    member(R, [1,2,3,4,5,6,7,8]),
    member(C, [1,2,3,4,5,6,7,8]),
    noAttack(Q, Qs).
/* findall. */
/* findall(Values, Query, Solutions). 
| ?- findall(X, append(X,Y,[1,2,3]), Solutions).

Solutions = [[],[1],[1,2],[1,2,3]]
useful for homeworks. */

/* Traveling Salesman Problem */
    
    
dist(ucla, ucsd, 124).
dist(ucla, uci, 45).
dist(ucla, ucsb, 97).
dist(ucla, ucb, 338).
dist(ucla, ucd, 392).
dist(ucla, ucsc, 346).
dist(ucsd, ucsb, 203).
dist(ucsd, ucb, 446).
dist(ucsd, ucd, 505).
dist(ucsd, ucsc, 460).
dist(uci, ucsb, 148).
dist(uci, ucb, 382).
dist(uci, ucd, 440).
dist(uci, ucsc, 395).
dist(ucsb, ucb, 323).
dist(ucsb, ucd, 378).
dist(ucsb, ucsc, 260).
dist(udb, ucd, 64).
dist(ucb, ucsc, 79).
dist(ucd, ucsc, 135).

symmetricDist(C1,C2,L) :- dist(C1,C2,L).
symmetricDist(C1,C2,L) :- dist(C2,C1,L).

sumDistances([C1, C2], Length) :- symmetricDist(C1, C2, Length).

/* shorthands for list patterns: 
   .(H,T) gives the head and tail of a list. 
   [H|T] is equivalent. 

   [H1, H2|T] matches a list of two or more items. 
*/

sumDistances([C1, C2|Rest], Length) :-
    symmetricDist(C1, C2, D1),
    sumDistances([C2|Rest], D2),
    Length is C1 + C2.


tsp(Campuses, Tour, Length) :- 
    (Tour = .(First, Rest)),
    /* tour must end where it starts */
    /* length and nth are built in list functions (can be used on HW) */
    length(Tour, L), nth(L, Tour, First),
    /* tour must visit every campus once */
    permutation(Rest, Campuses),
    /* sums distances in a tour.*/
    sumDistances(Tour, Length). 
    
			 
    
	    
	    
