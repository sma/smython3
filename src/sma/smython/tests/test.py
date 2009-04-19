# Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.

# TODO None shouldn't render as "null"

# pass statement
>>> pass
Suite[Pass]

# break & continue (TODO must only occur in loops)
>>> break
Suite[Break]
>>> continue
Suite[Continue]

# del statement
>>> del a
Suite[Del[Var(a)]]
>>> del a,
Suite[Del[Var(a)]]
>>> del a, b
Suite[Del[Var(a), Var(b)]]
>>> del a, b,
Suite[Del[Var(a), Var(b)]]

# return statement (TODO must only occur in functions)
>>> return
Suite[Return(Lit(null))]
>>> return None
Suite[Return(Lit(null))]
>>> return None,
Suite[Return[Lit(null)]]
>>> return 1, 2
Suite[Return[Lit(1), Lit(2)]]
>>> return 1, 2,
Suite[Return[Lit(1), Lit(2)]]

# raise statement
>>> raise
Suite[Raise()]
>>> raise SyntaxError
Suite[Raise(Var(SyntaxError))]
>>> raise SyntaxError from None
Suite[Raise(Var(SyntaxError), Lit(null))]
