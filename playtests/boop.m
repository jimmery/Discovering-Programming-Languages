x = [1;2;3;4];

d = x'*x;

A = [x.^0 x.^1 x.^2 x.^3];
y = [3;1;4;1];

z = A\y;

disp(z);
