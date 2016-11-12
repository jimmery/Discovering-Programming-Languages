/* 
Homework 6

Name: Jeffrey Jiang
UID: 904255069

*/

/* Problem 1. 
Implement predicate duplist(X,Y), succeeding if Y is list X with each element
duplicated. e.g.
duplist([1,2,3], Y) -> Y = [1, 1, 2, 2, 3, 3].
*/

duplist([], []). /* base case */
duplist([H|T1], [H,H|T2]) :- duplist(T1, T2). /* recursive case. */

/* Problem 2. 
Define predicate subseq(X,Y), succeeding if X is a subsequence of Y. 
i.e. X can be obtained by removing zero or more elements in Y. 

e.g. [1,3] is a subsequence of [1,2,3], but [3,1] is not. 
*/

/* base case: empty sequence subsequence of all sequences. */
subseq([], _).

/* if the heads of two sequences are the same, 
   then subsequence is true if the tail of the first is a subsequence
   of the tail of the second. */
subseq([H|T1], [H|T2]) :- subseq(T1, T2).

/* however, if they are not the same, we simply remove the top of the 
   larger sequence and continue. 
   Piazza @428 suggests that each variable is distinct, so this 
   predicate definition is simplified. Otherwise we would have to 
   also ensure that H1 \= H2, to make sure that this predicate does not 
   return duplicate results, in the case of things like [1,1]. */
subseq([H1|T1], [_|T2]) :- subseq([H1|T1],T2).

/* Problem 3. 
We are given a verbal arithmetic puzzle, verbalarithmetic(L, X, Y, Z), where
X,Y,Z := words (list of letters)
L := unique letters in the problem. 
if we assign values of 0-9 (first letter of each word is not 0), we want
Z = X + Z. 
assign each letter to a value. 

The most brute force solution takes up to 10! different orientations to try. 
Here, we use a couple things to constrain the problem a bit. 
1. we check the lengths of the words to make sure that such a sum can
   possibly exist. 
2. we check constrain each word to be a word, by the definitions we have. 
3. we constrain each letter to be a different digit. 
4. we constrain each digit addition as a constraint equation. 
*/

/* variables provide our mapping from letters to numbers implicitly. 
   thus, we just have to find a set of rules to define this problem. */

/* computes the value of a word, given a list of ints, representing a word.
   currently unused in the actual code, but probably can be used as a test. 
   essentially computes a value from a word. */
wordval([V], V).
wordval([H1,H2|T], V) :-
    H1 #\= 0,
    H2 #< 10, 
    H #= 10*H1+H2,
    wordval([H|T], V).

/* determines whether or not a list of ints is a valid word. 
   by definitions provided in the specs (and Piazza): 
   1. must contain at least one letter. 
   2. the first letter must not represent 0. 
   3. all letters must represent a single digit. */
validWord(L) :-
    L = [H|_], 
    H #\= 0,
    allDigits(L).
allDigits([L]) :- L #< 10.
allDigits([H|T]) :- H #< 10, allDigits(T).    

/* does a check to make sure that all input lengths are possible in a sum. 
   we know that in any sum of positive integers: 
   1. the sum will have at least as many digits as the larger int in the sum.
   2. the sum cannot have more than one more than that number of digits. */
checkLengths(X,Y,Z) :-
    length(X, Lx),
    length(Y, Ly),
    length(Z, Lz),
    max_list([Lx, Ly], Lm),
    Lz #>= Lm, 
    Lz #=< Lm + 1.

/* called to create a sum. 
   reverse essentially allows us to operate from the least significant bit, 
   which is more suitable. */
wordSum(X,Y,Z) :-
    reverse(X, A),
    reverse(Y, B),
    reverse(Z, C),
 
    revSum(A, B, C, 0).

/* does a summing operation: 
   digit by digit, starting from the least significant digit. 
   includes a carry to the next digit sum. */

/* overflow cases. */
revSum([],[],[], 0).
revSum([], [], [1], 1).
/* empty X case. can be merged if we zero-padded. */
revSum([], [Y|T2], [Z|T3], C) :-
    Sum #= Y + C,
    Z #= Sum rem 10, 
    Cout #= Sum // 10,
    revSum([], T2, T3, Cout).
/* empty Y case. can be merged if we zero-padded. */
revSum([X|T1], [], [Z|T3], C) :-
    Sum #= X + C,
    Z #= Sum rem 10,
    Cout #= Sum // 10, 
    revSum(T1, [], T3, Cout).
/* case when both words have not ended yet. */
revSum([X|T1],[Y|T2],[Z|T3], C) :-
    Sum #= X + Y + C,
    Z #= Sum rem 10,
    Carry #= Sum // 10,
    revSum(T1, T2, T3, Carry).

verbalarithmetic(L, X, Y, Z) :-
    checkLengths(X,Y,Z), /* first constraint. check lengths */
    validWord(X), /* check all are valid words. contrains to 0-9. */
    validWord(Y),
    validWord(Z),
    fd_all_different(L), /* constrains letters to all be different. */
    wordSum(X,Y,Z), /* tightly constrains X+Y=Z */
    fd_labeling(L). /* produces results. */

/* Problem 4. Blocks world problem. 

We have 3 stacks of blocks. 
We have a robot that can do two actions: 
1. If a stack is nonempty and the robot's hand is free, 
   we can pick up the top block of a stack. 
2. If the robot's hand is not free, 
   we can place a block from its hand on to a stack. 

We want to implement: 
blocksworld(StartWorld, Actions, GoalWorld). 
Succeeds only when performing the actions takes the start to the goal.

where we also have an intermediate definitions: 
world(Stack1, Stack2, Stack3, RobotBlock). 
to represent the world of blocks at any moment in time. 
all the Stack variables contain a list of blocks, which can be of anything.
RobotBlock will either contain a single block or 'none'.

here are the possible actions: 
pickup(B,S)
putdown(B,S)
B is the block and S is the stack. 
stack1, stack2, and stack3 will be 3 constants to represent stacks. 

*/

/* define all the robot actions possible. */
robotAct(world([B|T],S2,S3,none), pickup(B,stack1), world(T,S2,S3,B)).
robotAct(world(S1,[B|T],S3,none), pickup(B,stack2), world(S1,T,S3,B)).
robotAct(world(S1,S2,[B|T],none), pickup(B,stack3), world(S1,S2,T,B)).
robotAct(world(S1,S2,S3,B), putdown(B,stack1), world([B|S1],S2,S3,none)).
robotAct(world(S1,S2,S3,B), putdown(B,stack2), world(S1,[B|S2],S3,none)).
robotAct(world(S1,S2,S3,B), putdown(B,stack3), world(S1,S2,[B|S3],none)).

/* base case: if our start is our goal with no actions */
blocksworld(Goal, [], Goal).

/* else, apply the action and try to find a new path from that action. */
blocksworld(Start, [Act|Rest], Goal) :-
    robotAct(Start, Act, Diff),
    blocksworld(Diff, Rest, Goal).

