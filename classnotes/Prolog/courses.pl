
/* A set of facts about the CS department courses */

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

prereq2(X,Y) :- prereq(X,Z), prereq(Z,Y).

/* search strategy:
   depth-first, left to right */

/* initial query: prereq2(cs32, U)

   unify with the head of the prereq2 rule:
   X=cs32, Y=U
   new subgoals:
   [prereq(cs32,Z),prereq(Z,U)]
   unify first subgoal with prereq facts
   Z=cs33
   [prereq(cs33,U)]
   unify first subgoal with prereq facts
   U=cs111
   []

   pitfall: consider the query 
           prereq2(cs32, X)
    this X is a different X than the one
    in the prereq2 rule.

    every rule has its own scope.
    every query has its own scope.

    how to respect variable scopes?
    before you unify with a rule or fact,
    rename all variables in that rule or
    fact to fresh names.


*/

prereq3(X,Y) :- prereq2(X,Z), prereq(Z,Y).

/* query: [prereq3(cs32,U)]
let's draw the left-most successful path
in the tree...

1 child: [prereq2(cs32,Z), prereq(Z,U)]

(the new subgoals go on the front, to be processed
 in left-to-right order)
1 child: [prereq(cs32,Z'), prereq(Z',Z),
          prereq(Z,U)]

17 children:
  line 5 child: Z'=cs33
  	[prereq(c33,Z), prereq(Z,U)]

17 children:
  line 8 child: Z=cs11
  	[prereq(cs111,U)]

17 children:  
  line 13 child: U=cs118
  	[] (solution)
*/


isANumber(zero).

isANumber(N) :- isANumber(N).


/* tree

			[isANumber(N)]
      /           \
	[] N=zero			[isANumber(N0)] N=succ(N0)
              /                   \
					[] N0=zero        [isANumber(N1)] N0=succ(N1)
*/


len([],Accum,Accum).
len([_|T],Accum, Result) :-
	Accum1 is Accum+1, len(T,Accum1,Result).


/*

				[len(L,0,2)]
/             \
NO		L=[_|T], Accum=0, Result=2
		  Accum1=1
		 [len(T,1,5)]
  /           \
NO		T=[_|T0], Accum'=1, Result'=2
		  Accum1'=2
		  [len(T0,2,2)]
		/               \
[] (solution)			T0=[_|T1]
						      [len(T1,3,2)]			

*/				
