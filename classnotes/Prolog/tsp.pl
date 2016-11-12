
dist(ucla, ucsd, 124).
dist(ucla, uci, 45).
dist(ucla, ucsb, 97).
dist(ucla, ucb, 338).
dist(ucla, ucd, 392).
dist(ucla, ucsc, 346).
dist(ucsd, uci, 72).
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
dist(ucb, ucd, 64).
dist(ucb, ucsc, 79).
dist(ucd, ucsc, 135).

/* shorthands for list patterns:
    .(H,T) gives you the head and tail of a list
    [H|T] is equivalent

    [H1,H2|T] matches a list of two or more items

*/

symmetricDist(C1,C2,L) :- dist(C1,C2,L).
symmetricDist(C1,C2,L) :- dist(C2,C1,L).

sumDistances([C1,C2], Length) :-
	symmetricDist(C1,C2,Length).

sumDistances([C1,C2|Rest], Length)  :-
	symmetricDist(C1,C2, D1),
	sumDistances([C2|Rest], D2),
	Length is D1+D2.

tsp(Campuses, Tour, Length) :-
	Tour = .(First, Rest),
	length(Tour, L), nth(L, Tour, Last),
	/* a tour must end where it starts */
	First=Last,
	/* a tour must visit every campus once */
	permutation(Rest, Campuses),
	/* compute the length of the tour */
	sumDistances(Tour, Length).





