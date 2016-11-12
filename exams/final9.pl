/*
merge([],[],[]).
merge([], [Y1|YRest], [Y1|ZRest]) :- merge([], YRest, ZRest).
merge([X1|XRest], [], [X1|ZRest]) :- merge([], XRest, ZRest). */

merge(L, [], L).
merge([], L, L).
merge([X1|XRest], [Y1|YRest], [X1|Z]) :-
    X1 < Y1,
    merge(XRest, [Y1|YRest], Z).
merge([X1|XRest], [Y1|YRest], [Y1|Z]) :-
    Y1 < X1,
    merge([X1|XRest], YRest, Z).
