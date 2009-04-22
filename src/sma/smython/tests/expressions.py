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
