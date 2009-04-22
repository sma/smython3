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

# star expression
>>> *a, b = 1, 2, 3
Suite[Assign([Star(Var(a)), Var(b)], [Lit(1), Lit(2), Lit(3)])]
# >>> *a
# SyntaxError
# >>> *a = 1, 2
# SyntaxError

# bit operations
>>> a | b
Suite[Expr(BitOr(Var(a), Var(b)))]
>>> a | b | c
Suite[Expr(BitOr(BitOr(Var(a), Var(b)), Var(c)))]
>>> a ^ b
Suite[Expr(BitXor(Var(a), Var(b)))]
>>> a ^ b ^ c
Suite[Expr(BitXor(BitXor(Var(a), Var(b)), Var(c)))]
>>> a & b
Suite[Expr(BitAnd(Var(a), Var(b)))]
>>> a & b & c
Suite[Expr(BitAnd(BitAnd(Var(a), Var(b)), Var(c)))]
>>> a << b
Suite[Expr(BitShiftLeft(Var(a), Var(b)))]
>>> a << b << c
Suite[Expr(BitShiftLeft(BitShiftLeft(Var(a), Var(b)), Var(c)))]
>>> a >> b
Suite[Expr(BitShiftRight(Var(a), Var(b)))]
>>> a >> b >> c
Suite[Expr(BitShiftRight(BitShiftRight(Var(a), Var(b)), Var(c)))]
>>> a & b | c & d
Suite[Expr(BitOr(BitAnd(Var(a), Var(b)), BitAnd(Var(c), Var(d))))]
>>> a | b & c | d
Suite[Expr(BitOr(BitOr(Var(a), BitAnd(Var(b), Var(c))), Var(d)))]
>>> a << b ^ c
Suite[Expr(BitXor(BitShiftLeft(Var(a), Var(b)), Var(c)))]
>>> a ^ b << c
Suite[Expr(BitXor(Var(a), BitShiftLeft(Var(b), Var(c))))]

>>> ~a
Suite[Expr(Invert(Var(a)))]
>>> ~~a
Suite[Expr(Invert(Invert(Var(a))))]
>>> ~a&b
Suite[Expr(BitAnd(Invert(Var(a)), Var(b)))]

# arithmic operations
>>> a + b
Suite[Expr(Add(Var(a), Var(b)))]
>>> a + b + c
Suite[Expr(Add(Add(Var(a), Var(b)), Var(c)))]
>>> a - b
Suite[Expr(Sub(Var(a), Var(b)))]
>>> a - b - c
Suite[Expr(Sub(Sub(Var(a), Var(b)), Var(c)))]
>>> a + b - c
Suite[Expr(Sub(Add(Var(a), Var(b)), Var(c)))]

>>> a * b
Suite[Expr(Mul(Var(a), Var(b)))]
>>> a * b * c
Suite[Expr(Mul(Mul(Var(a), Var(b)), Var(c)))]
>>> a / b
Suite[Expr(Div(Var(a), Var(b)))]
>>> a / b / c
Suite[Expr(Div(Div(Var(a), Var(b)), Var(c)))]
>>> a // b
Suite[Expr(IntDiv(Var(a), Var(b)))]
>>> a // b // c
Suite[Expr(IntDiv(IntDiv(Var(a), Var(b)), Var(c)))]
>>> a % b
Suite[Expr(Mod(Var(a), Var(b)))]
>>> a % b % c
Suite[Expr(Mod(Mod(Var(a), Var(b)), Var(c)))]
>>> a * b / c % d
Suite[Expr(Mod(Div(Mul(Var(a), Var(b)), Var(c)), Var(d)))]

>>> a + b * c
Suite[Expr(Add(Var(a), Mul(Var(b), Var(c))))]
>>> a / b - c
Suite[Expr(Sub(Div(Var(a), Var(b)), Var(c)))]

>>> -a
Suite[Expr(Neg(Var(a)))]
>>> --a
Suite[Expr(Neg(Neg(Var(a))))]
>>> +a
Suite[Expr(Pos(Var(a)))]
>>> ++a
Suite[Expr(Pos(Pos(Var(a))))]
>>> +-a
Suite[Expr(Pos(Neg(Var(a))))]
>>> -a+-b
Suite[Expr(Add(Neg(Var(a)), Neg(Var(b))))]

# power expression
>>> a ** b
Suite[Expr(Power(Var(a), Var(b)))]
>>> a ** b ** c
Suite[Expr(Power(Var(a), Power(Var(b), Var(c))))]
