f(X,a) = f(b,Y). /* expected: X=b, Y=a. */

f(W,W) = f(X,g(Z)). /* expected: W=g(Z), X=g(Z). */

f(X,a) = f(b,X). /* expected: no. */

f(X,Y) = f(Y,g(X)). /* cyclical data structure. */

[a] = [X,Y]. /* expected: no. */

[a] = [X|Y]. /* expected: X=a, Y = [] */

[Y,b] = [X,Y]. /* expected: X=b, Y=b. */

[Y,b] = [X|Y]. /* expected: X=[b], Y=[b]. */
