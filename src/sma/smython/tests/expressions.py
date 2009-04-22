# lambda expression
>>> lambda: 1
Suite[Expr(Lambda([], Lit(1)))]
>>> lambda a: 1
Suite[Expr(Lambda([a], Lit(1)))]
>>> lambda a,: 1
Suite[Expr(Lambda([a], Lit(1)))]
>>> lambda a, b: 1
Suite[Expr(Lambda([a, b], Lit(1)))]
>>> lambda a, b,: 1
Suite[Expr(Lambda([a, b], Lit(1)))]
>>> lambda a, b=1: 1
Suite[Expr(Lambda([a, b=Lit(1)], Lit(1)))]
>>> lambda a, b=1,: 1
Suite[Expr(Lambda([a, b=Lit(1)], Lit(1)))]
>>> lambda a=0, b=1,: 1
Suite[Expr(Lambda([a=Lit(0), b=Lit(1)], Lit(1)))]
>>> lambda a=0, b: 1
SyntaxError
>>> lambda *a: 1
Suite[Expr(Lambda([*a], Lit(1)))]
>>> lambda **a: 1
Suite[Expr(Lambda([**a], Lit(1)))]
>>> lambda *a, **b: 1
Suite[Expr(Lambda([*a, **b], Lit(1)))]
>>> lambda **a, *b: 1
SyntaxError
>>> lambda a, b=0, *c, **d: 1
Suite[Expr(Lambda([a, b=Lit(0), *c, **d], Lit(1)))]

# if/else expression
>>> 2 if a else 1
Suite[Expr(IfElse(Var(a), Lit(2), Lit(1)))]
>>> 2 if a
SyntaxError

# logical expressions
>>> a or b
Suite[Expr(Or(Var(a), Var(b)))]
>>> a or b or c
Suite[Expr(Or(Or(Var(a), Var(b)), Var(c)))]
>>> a and b
Suite[Expr(And(Var(a), Var(b)))]
>>> a and b and c
Suite[Expr(And(And(Var(a), Var(b)), Var(c)))]
>>> not a
Suite[Expr(Not(Var(a)))]
>>> not not a
Suite[Expr(Not(Not(Var(a))))]
>>> a and not b or not b and c
Suite[Expr(Or(And(Var(a), Not(Var(b))), And(Not(Var(b)), Var(c))))]
>>> a or b and not b or c
Suite[Expr(Or(Or(Var(a), And(Var(b), Not(Var(b)))), Var(c)))]

# comparison expressions
>>> a < b
Suite[Expr(Comparison(Var(a) < Var(b)))]
>>> a < b < c
Suite[Expr(Comparison(Var(a) < Var(b) < Var(c)))]
>>> a > b
Suite[Expr(Comparison(Var(a) > Var(b)))]
>>> a > b > c
Suite[Expr(Comparison(Var(a) > Var(b) > Var(c)))]
>>> a <= b
Suite[Expr(Comparison(Var(a) <= Var(b)))]
>>> a <= b <= c
Suite[Expr(Comparison(Var(a) <= Var(b) <= Var(c)))]
>>> a >= b
Suite[Expr(Comparison(Var(a) >= Var(b)))]
>>> a >= b >= c
Suite[Expr(Comparison(Var(a) >= Var(b) >= Var(c)))]
>>> a == b
Suite[Expr(Comparison(Var(a) == Var(b)))]
>>> a == b == c
Suite[Expr(Comparison(Var(a) == Var(b) == Var(c)))]
>>> a != b
Suite[Expr(Comparison(Var(a) != Var(b)))]
>>> a != b != c
Suite[Expr(Comparison(Var(a) != Var(b) != Var(c)))]
>>> a in b
Suite[Expr(Comparison(Var(a) in Var(b)))]
>>> a not in b
Suite[Expr(Comparison(Var(a) not in Var(b)))]
>>> a not b
SyntaxError
>>> a is b
Suite[Expr(Comparison(Var(a) is Var(b)))]
>>> a is not b
Suite[Expr(Comparison(Var(a) is not Var(b)))]
