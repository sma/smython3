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

# yield statement (TODO must only occur in functions) 
>>> yield
Suite[Yield(Lit(null))]
>>> yield None
Suite[Yield(Lit(null))]
>>> yield None,
Suite[Yield[Lit(null)]]
>>> yield 1, 2
Suite[Yield[Lit(1), Lit(2)]]
>>> yield 1, 2,
Suite[Yield[Lit(1), Lit(2)]]

# import statement
>>> import a
Suite[Import[a]]
>>> import a as b
Suite[Import[a as b]]
>>> import a, b.c as bc, d.e.f as g
Suite[Import[a, b.c as bc, d.e.f as g]]

# from statement
>>> from a import c
Suite[From(a, [c])]
>>> from a import c as d
Suite[From(a, [c as d])]
>>> from a.b import c, d as e, f as g
Suite[From(a.b, [c, d as e, f as g])]
>>> from a.b import *
Suite[From(a.b, [])]
>>> from a.b.c import (a, b as c)
Suite[From(a.b.c, [a, b as c])]
>>> from a.b.c import (a, b as c,)
Suite[From(a.b.c, [a, b as c])]

# global statement
>>> global A
Suite[Global[A]]
>>> global A, B, C
Suite[Global[A, B, C]]

# nonlocal statement
>>> nonlocal A
Suite[Nonlocal[A]]
>>> nonlocal A, B, C
Suite[Nonlocal[A, B, C]]

# assert statement
>>> assert 1
Suite[Assert(Lit(1))]
>>> assert 1, 2
Suite[Assert(Lit(1), Lit(2))]

# augmented assignment
>>> a += 1
Suite[AddAssign(Var(a), (Lit(1)))]
>>> b -= 1
Suite[SubAssign(Var(b), (Lit(1)))]
>>> c *= 1
Suite[MulAssign(Var(c), (Lit(1)))]
>>> d /= 1
Suite[DivAssign(Var(d), (Lit(1)))]
>>> e //= 1
Suite[IntDivAssign(Var(e), (Lit(1)))]
>>> f %= 1
Suite[ModAssign(Var(f), (Lit(1)))]
>>> g **= 1
Suite[PowerAssign(Var(g), (Lit(1)))]
>>> h >>= 1
Suite[RshiftAssign(Var(h), (Lit(1)))]
>>> i <<= 1
Suite[LshiftAssign(Var(i), (Lit(1)))]
>>> j &= 1
Suite[AndAssign(Var(j), (Lit(1)))]
>>> k ^= 1
Suite[XorAssign(Var(k), (Lit(1)))]
>>> l |= 1
Suite[OrAssign(Var(l), (Lit(1)))]

# augmented assignment with tuples
>>> x += 1,
Suite[AddAssign(Var(x), [Lit(1)])]
>>> y += 1, 2
Suite[AddAssign(Var(y), [Lit(1), Lit(2)])]
>>> z += 1, 2,
Suite[AddAssign(Var(z), [Lit(1), Lit(2)])]

# augmented assignment with yield
>>> a += yield
Suite[AddAssign(Var(a), (Yield(Lit(null))))]
>>> a += yield 1
Suite[AddAssign(Var(a), (Yield(Lit(1))))]
>>> a += yield 1,
Suite[AddAssign(Var(a), (Yield[Lit(1)]))]
>>> a += yield 1, 2
Suite[AddAssign(Var(a), (Yield[Lit(1), Lit(2)]))]
>>> a += yield 1, 2,
Suite[AddAssign(Var(a), (Yield[Lit(1), Lit(2)]))]
#TODO make this work
#>>> a += b, (yield 1), c
#Suite[AddAssign(Var(a), [Var(b), Yield(Lit(1)), Var(c)])]

# assignment statement
>>> a = 1
Suite[Assign((Var(a)), (Lit(1)))]
>>> a = 1,
Suite[Assign((Var(a)), [Lit(1)])]
>>> a, = 1
Suite[Assign([Var(a)], (Lit(1)))]
>>> a, = 1,
Suite[Assign([Var(a)], [Lit(1)])]
>>> a, b = 1
Suite[Assign([Var(a), Var(b)], (Lit(1)))]
>>> a, b, = 1
Suite[Assign([Var(a), Var(b)], (Lit(1)))]
>>> a, b = 1,
Suite[Assign([Var(a), Var(b)], [Lit(1)])]
>>> a, b, = 1,
Suite[Assign([Var(a), Var(b)], [Lit(1)])]
>>> a = 1, 2
Suite[Assign((Var(a)), [Lit(1), Lit(2)])]
>>> a = 1, 2,
Suite[Assign((Var(a)), [Lit(1), Lit(2)])]
>>> a, b = 1, 2
Suite[Assign([Var(a), Var(b)], [Lit(1), Lit(2)])]
>>> a, b, = 1, 2,
Suite[Assign([Var(a), Var(b)], [Lit(1), Lit(2)])]

# assignment with yield
>>> a = yield
Suite[Assign((Var(a)), (Yield(Lit(null))))]
>>> a = yield 1
Suite[Assign((Var(a)), (Yield(Lit(1))))]
>>> a = yield 1,
Suite[Assign((Var(a)), (Yield[Lit(1)]))]
>>> a = yield 1, 2
Suite[Assign((Var(a)), (Yield[Lit(1), Lit(2)]))]
>>> a = yield 1, 2,
Suite[Assign((Var(a)), (Yield[Lit(1), Lit(2)]))]
#TODO make this work
#>>> a = b, (yield 1), c
#Suite[Assign(Var(a), [Var(b), Yield(Lit(1)), Var(c)])]

# statement expression
>>> print
Suite[Expr(Var(print))]
>>> print,
Suite[Expr[Var(print)]]
>>> print, 1
Suite[Expr[Var(print), Lit(1)]]
>>> print, 2,
Suite[Expr[Var(print), Lit(2)]]

# if statement
>>> if 1: pass
Suite[If(Lit(1), Suite[Pass], Suite[Pass])]
>>> if 1: pass; pass
Suite[If(Lit(1), Suite[Pass, Pass], Suite[Pass])]
>>> if 1:
...   pass
Suite[If(Lit(1), Suite[Pass], Suite[Pass])]
>>> if 1:
...   pass; pass
Suite[If(Lit(1), Suite[Pass, Pass], Suite[Pass])]
>>> if 1:
...   pass
...   pass
Suite[If(Lit(1), Suite[Pass, Pass], Suite[Pass])]

# if/else statement
>>> if 1: pass
... else: pass
Suite[If(Lit(1), Suite[Pass], Suite[Pass])]
>>> if 1: pass
... else: pass; pass
Suite[If(Lit(1), Suite[Pass], Suite[Pass, Pass])]
>>> if 1: pass
... else:
...   pass
Suite[If(Lit(1), Suite[Pass], Suite[Pass])]
>>> if 1: pass
... else:
...   pass; pass
Suite[If(Lit(1), Suite[Pass], Suite[Pass, Pass])]
>>> if 1: pass
... else:
...   pass
...   pass
Suite[If(Lit(1), Suite[Pass], Suite[Pass, Pass])]

# if/elif statement
>>> if 1: pass
... elif 2: pass
Suite[If(Lit(1), Suite[Pass], Suite[If(Lit(2), Suite[Pass], Suite[Pass])])]
>>> if 1: pass
... elif 2: pass; pass
Suite[If(Lit(1), Suite[Pass], Suite[If(Lit(2), Suite[Pass, Pass], Suite[Pass])])]
>>> if 1: pass
... elif 2:
...   pass
Suite[If(Lit(1), Suite[Pass], Suite[If(Lit(2), Suite[Pass], Suite[Pass])])]
>>> if 1: pass
... elif 2:
...   pass; pass
Suite[If(Lit(1), Suite[Pass], Suite[If(Lit(2), Suite[Pass, Pass], Suite[Pass])])]
>>> if 1: pass
... elif 2:
...   pass
...   pass
Suite[If(Lit(1), Suite[Pass], Suite[If(Lit(2), Suite[Pass, Pass], Suite[Pass])])]

# if/elif/else statement
>>> if 1: pass
... elif 2: pass
... else: pass
Suite[If(Lit(1), Suite[Pass], Suite[If(Lit(2), Suite[Pass], Suite[Pass])])]

# while statement
>>> while 1: pass
Suite[While(Lit(1), Suite[Pass])]
>>> while 1: pass; break
Suite[While(Lit(1), Suite[Pass, Break])]
>>> while 1:
...   pass
Suite[While(Lit(1), Suite[Pass])]
>>> while 1:
...   pass; break
Suite[While(Lit(1), Suite[Pass, Break])]
>>> while 1:
...   pass
...   break
Suite[While(Lit(1), Suite[Pass, Break])]

# while/else statement
>>> while 1: pass
... else: pass
Suite[While(Lit(1), Suite[Pass], Suite[Pass])]
>>> while 1: pass
... else: pass; pass
Suite[While(Lit(1), Suite[Pass], Suite[Pass, Pass])]
>>> while 1: pass
... else:
...   pass
Suite[While(Lit(1), Suite[Pass], Suite[Pass])]
>>> while 1: pass
... else:
...   pass; pass
Suite[While(Lit(1), Suite[Pass], Suite[Pass, Pass])]
>>> while 1: pass
... else:
...   pass
...   pass
Suite[While(Lit(1), Suite[Pass], Suite[Pass, Pass])]

# for statement (single items)
>>> for a in items: pass
Suite[For([Var(a)], (Var(items)), Suite[Pass])]
>>> for a, in items: pass
Suite[For([Var(a)], (Var(items)), Suite[Pass])]
>>> for a, b in items: pass
Suite[For([Var(a), Var(b)], (Var(items)), Suite[Pass])]
>>> for a, b, in items: pass
Suite[For([Var(a), Var(b)], (Var(items)), Suite[Pass])]

# for statement (multiple items)
>>> for a in 1,: pass
Suite[For([Var(a)], [Lit(1)], Suite[Pass])]
>>> for a in 1, 2: pass
Suite[For([Var(a)], [Lit(1), Lit(2)], Suite[Pass])]
>>> for a in 1, 2,: pass
Suite[For([Var(a)], [Lit(1), Lit(2)], Suite[Pass])]

# for statement (suite alterations)
>>> for a in items:
...   pass
Suite[For([Var(a)], (Var(items)), Suite[Pass])]
>>> for a in items: pass; break
Suite[For([Var(a)], (Var(items)), Suite[Pass, Break])]
>>> for a in items:
...   pass; break
Suite[For([Var(a)], (Var(items)), Suite[Pass, Break])]
>>> for a in items:
...   pass
...   break
Suite[For([Var(a)], (Var(items)), Suite[Pass, Break])]

# for/else statement
>>> for a in items: pass
... else: pass
Suite[For([Var(a)], (Var(items)), Suite[Pass], Suite[Pass])]
>>> for a in items: pass
... else:
...   pass
Suite[For([Var(a)], (Var(items)), Suite[Pass], Suite[Pass])]
>>> for a in items: pass
... else: pass; pass
Suite[For([Var(a)], (Var(items)), Suite[Pass], Suite[Pass, Pass])]
>>> for a in items: pass
... else:
...   pass; pass
Suite[For([Var(a)], (Var(items)), Suite[Pass], Suite[Pass, Pass])]
>>> for a in items: pass
... else:
...   pass
...   pass
Suite[For([Var(a)], (Var(items)), Suite[Pass], Suite[Pass, Pass])]
