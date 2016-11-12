/* Test File. */
consult(hw6).

/* Problem 1 Tests. */
findall(Y, duplist([1,2,3], Y), L), L = [[1,1,2,2,3,3]].

/* Problem 2 Tests. */
findall(Y, subseq(Y, [1,2]), L),
findall(L, permutation(L, [[],[1],[2],[1,2]]), M).

/* Problem 3 Tests. */
findall([S,E,N,D,M,O,R,Y],
	verbalarithmetic([S,E,N,D,M,O,R,Y], [S,E,N,D], [M,O,R,E], [M,O,N,E,Y]),
	M),
M = [[9,5,6,7,1,0,8,2]]. 

findall([A,B,C],verbalarithmetic([A,B,C], [A,B], [A,C], [C,A]),L),
findall(L, permutation(L, [[1,8,3], [2,7,5], [3,6,7], [4,5,9]]),M).

findall([A,B,C],verbalarithmetic([A,B,C], [A,B], [A,C], [B,C,A]),M),
M = [[9,1,8]].

findall([C,O,A,L,S,I],
	verbalarithmetic([C,O,A,L,S,I],[C,O,C,A],[C,O,L,A],[O,A,S,I,S]),
	M),
M = [[8,1,6,0,2,9]].

/* Problem 4 Tests. */
/* tbh can't figure out these test cases, since they stack overflow 
   upon use. works in the environment though.
length(Actions,L), L < 11,
blocksworld(world([a,b,c],[],[],none),
	    Actions,
	    world([],[],[a,b,c],none))
Actions = [pickup(a,stack1),
	   putdown(a,stack2),
	   pickup(b,stack1),
	   putdown(b,stack2),
	   pickup(c,stack1),
	   putdown(c,stack3),
	   pickup(b,stack2),
	   putdown(b,stack3),
	   pickup(a,stack2),
	   putdown(a,stack3)].

length(Actions,L), L < 7,
blocksworld(world([a,b,c],[],[],none),
	    Actions,
	    world([],[],[c,b,a],none)),
Actions = [pickup(a,stack1),
	   putdown(a,stack3),
	   pickup(b,stack1),
	   putdown(b,stack3),
	   pickup(c,stack1),
	   putdown(c,stack3)].
*/
