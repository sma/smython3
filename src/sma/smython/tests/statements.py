# Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.

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
>>> del
SyntaxError

# return statement (TODO must only occur in functions)
>>> return
Suite[Return(Lit(None))]
>>> return None
Suite[Return(Lit(None))]
>>> return None,
Suite[Return[Lit(None)]]
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
Suite[Raise(Var(SyntaxError), Lit(None))]

# yield statement (TODO must only occur in functions) 
>>> yield
Suite[Yield(Lit(None))]
>>> yield None
Suite[Yield(Lit(None))]
>>> yield None,
Suite[Yield[Lit(None)]]
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
>>> import
SyntaxError
>>> import a.
SyntaxError
>>> import a as
SyntaxError

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
>>> from
SyntaxError
>>> from a
SyntaxError
>>> from a.
SyntaxError
>>> from a.b import
SyntaxError
>>> from a.b import c as
SyntaxError

# global statement
>>> global A
Suite[Global[A]]
>>> global A, B, C
Suite[Global[A, B, C]]
>>> global
SyntaxError

# nonlocal statement
>>> nonlocal A
Suite[Nonlocal[A]]
>>> nonlocal A, B, C
Suite[Nonlocal[A, B, C]]
>>> nonlocal
SyntaxError

# assert statement
>>> assert 1
Suite[Assert(Lit(1))]
>>> assert 1, 2
Suite[Assert(Lit(1), Lit(2))]
>>> assert
SyntaxError

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
Suite[AddAssign(Var(a), (Yield(Lit(None))))]
>>> a += yield 1
Suite[AddAssign(Var(a), (Yield(Lit(1))))]
>>> a += yield 1,
Suite[AddAssign(Var(a), (Yield[Lit(1)]))]
>>> a += yield 1, 2
Suite[AddAssign(Var(a), (Yield[Lit(1), Lit(2)]))]
>>> a += yield 1, 2,
Suite[AddAssign(Var(a), (Yield[Lit(1), Lit(2)]))]
>>> a += b, (yield 1), c
Suite[AddAssign(Var(a), [Var(b), Yield(Lit(1)), Var(c)])]

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
Suite[Assign((Var(a)), (Yield(Lit(None))))]
>>> a = yield 1
Suite[Assign((Var(a)), (Yield(Lit(1))))]
>>> a = yield 1,
Suite[Assign((Var(a)), (Yield[Lit(1)]))]
>>> a = yield 1, 2
Suite[Assign((Var(a)), (Yield[Lit(1), Lit(2)]))]
>>> a = yield 1, 2,
Suite[Assign((Var(a)), (Yield[Lit(1), Lit(2)]))]
>>> a = b, (yield 1), c
Suite[Assign((Var(a)), [Var(b), Yield(Lit(1)), Var(c)])]

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

# try/except statement (try suite alternations)
>>> try: pass
... except: pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])])]
>>> try:
...   pass
... except: pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])])]
>>> try: pass; pass
... except: pass
Suite[Try(Suite[Pass, Pass], [Except(Suite[Pass])])]
>>> try:
...   pass; pass
... except: pass
Suite[Try(Suite[Pass, Pass], [Except(Suite[Pass])])]
>>> try:
...   pass
...   pass
... except: pass
Suite[Try(Suite[Pass, Pass], [Except(Suite[Pass])])]

# try/except statement (except suite alternations)
>>> try: pass
... except: pass; pass
Suite[Try(Suite[Pass], [Except(Suite[Pass, Pass])])]
>>> try: pass
... except:
...   pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])])]
>>> try: pass
... except:
...   pass; pass
Suite[Try(Suite[Pass], [Except(Suite[Pass, Pass])])]
>>> try: pass
... except:
...   pass
...   pass
Suite[Try(Suite[Pass], [Except(Suite[Pass, Pass])])]

# try/except/else statement
>>> try: pass
... except: pass
... else: pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])], Suite[Pass])]
>>> try: pass
... except: pass
... else: pass; pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])], Suite[Pass, Pass])]
>>> try: pass
... except: pass
... else:
...   pass; pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])], Suite[Pass, Pass])]
>>> try: pass
... except: pass
... else:
...   pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])], Suite[Pass])]
>>> try: pass
... except: pass
... else:
...   pass
...   pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])], Suite[Pass, Pass])]

# try/finally statement
>>> try: pass
... finally: pass
Suite[Try(Suite[Pass], [], null, Suite[Pass])]
>>> try: pass
... finally:
...   pass
Suite[Try(Suite[Pass], [], null, Suite[Pass])]
>>> try: pass
... finally: pass; pass
Suite[Try(Suite[Pass], [], null, Suite[Pass, Pass])]
>>> try: pass
... finally:
...   pass; pass
Suite[Try(Suite[Pass], [], null, Suite[Pass, Pass])]
>>> try: pass
... finally:
...   pass
...   pass
Suite[Try(Suite[Pass], [], null, Suite[Pass, Pass])]

# try/except/finally statement
>>> try: pass
... except: pass
... finally: pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])], null, Suite[Pass])]

# try/except/else/finally statement
>>> try: pass
... except: pass
... else: pass
... finally: pass
Suite[Try(Suite[Pass], [Except(Suite[Pass])], Suite[Pass], Suite[Pass])]

# try/except statement (multiple except clauses)
>>> try: pass
... except 1: pass
... except 2: pass
Suite[Try(Suite[Pass], [Except(Lit(1), Suite[Pass]), Except(Lit(2), Suite[Pass])])]
>>> try: pass
... except 1 as a: pass
... except 2 as b: pass
Suite[Try(Suite[Pass], [Except(Lit(1), a, Suite[Pass]), Except(Lit(2), b, Suite[Pass])])]

>>> try: pass
SyntaxError

# with statement
>>> with a: pass
Suite[With(Var(a), Suite[Pass])]
>>> with a as b: pass
Suite[With(Var(a), Var(b), Suite[Pass])]
>>> with: pass
SyntaxError

# def statement (body suite alternations)
>>> def a(): pass
Suite[Def(a, [], Suite[Pass])]
>>> def a():
...   pass
Suite[Def(a, [], Suite[Pass])]
>>> def a(): pass; pass
Suite[Def(a, [], Suite[Pass, Pass])]
>>> def a():
...   pass; pass
Suite[Def(a, [], Suite[Pass, Pass])]
>>> def a():
...   pass
...   pass
Suite[Def(a, [], Suite[Pass, Pass])]

# def statement inside a def statement
>>> def a():
...   def b(): pass
Suite[Def(a, [], Suite[Def(b, [], Suite[Pass])])]

# decorated def statement
>>> @deco
... def a(): pass
Suite[Def(a, [], Suite[Pass], [@deco])]
>>> @deco1
... @deco2
... def a(): pass
Suite[Def(a, [], Suite[Pass], [@deco1, @deco2])]
>>> @deco(1, 2)
... def a(): pass
Suite[Def(a, [], Suite[Pass], [@deco[Lit(1), Lit(2)]])]

# def statement (parameter list alternations)
>>> def a(x): pass
Suite[Def(a, [x], Suite[Pass])]
>>> def a(x,): pass
Suite[Def(a, [x], Suite[Pass])]
>>> def a(x, y): pass
Suite[Def(a, [x, y], Suite[Pass])]
>>> def a(x, y,): pass
Suite[Def(a, [x, y], Suite[Pass])]

>>> def a(x=0): pass
Suite[Def(a, [x=Lit(0)], Suite[Pass])]
>>> def a(x=0,): pass
Suite[Def(a, [x=Lit(0)], Suite[Pass])]
>>> def a(x, y=1): pass
Suite[Def(a, [x, y=Lit(1)], Suite[Pass])]
>>> def a(x, y=1,): pass
Suite[Def(a, [x, y=Lit(1)], Suite[Pass])]
>>> def a(x=0, y=1): pass
Suite[Def(a, [x=Lit(0), y=Lit(1)], Suite[Pass])]
>>> def a(x=0, y=1,): pass
Suite[Def(a, [x=Lit(0), y=Lit(1)], Suite[Pass])]
>>> def a(x=0, y): pass
SyntaxError

>>> def a(*x): pass
Suite[Def(a, [*x], Suite[Pass])]
>>> def a(**y): pass
Suite[Def(a, [**y], Suite[Pass])]
>>> def a(*x): pass
Suite[Def(a, [*x], Suite[Pass])]
>>> def a(*x, **y): pass
Suite[Def(a, [*x, **y], Suite[Pass])]
>>> def a(*x, *y): pass
SyntaxError
>>> def a(**x, **y): pass
SyntaxError
>>> def a(**x, *y): pass
SyntaxError
>>> def a(*x=0): pass
SyntaxError
>>> def a(**x=0): pass
SyntaxError

>>> def a(x, *y): pass
Suite[Def(a, [x, *y], Suite[Pass])]
>>> def a(x, y, *z): pass
Suite[Def(a, [x, y, *z], Suite[Pass])]
>>> def a(x, *y, **z): pass
Suite[Def(a, [x, *y, **z], Suite[Pass])]
>>> def a(x, y=0, *z): pass
Suite[Def(a, [x, y=Lit(0), *z], Suite[Pass])]
>>> def a(x=0, y=0, *z): pass
Suite[Def(a, [x=Lit(0), y=Lit(0), *z], Suite[Pass])]

# TODO support bare "*"
# >>> def a(*): pass
# SyntaxError
# >>> def a(*, a): pass
# Suite[Def(a, [*, a], Suite[Pass])]

>>> def a(x:int, y:int=0, *z:str) -> str: pass
Suite[Def(a, [x:Var(int), y:Var(int)=Lit(0), *z:Var(str)]:Var(str), Suite[Pass])]

# class statement (body suite alternations)
>>> class A: pass
Suite[Class(A, [], Suite[Pass])]
>>> class A:
...   pass
Suite[Class(A, [], Suite[Pass])]
>>> class A: pass; pass
Suite[Class(A, [], Suite[Pass, Pass])]
>>> class A:
...   pass; pass
Suite[Class(A, [], Suite[Pass, Pass])]
>>> class A:
...   pass
...   pass
Suite[Class(A, [], Suite[Pass, Pass])]

# decorated class statement
>>> @deco
... class A: pass
Suite[Class(A, [], Suite[Pass], [@deco])]
>>> @deco1
... @deco2
... class A: pass
Suite[Class(A, [], Suite[Pass], [@deco1, @deco2])]
>>> @deco(1, 2)
... class A: pass
Suite[Class(A, [], Suite[Pass], [@deco[Lit(1), Lit(2)]])]

# class statement (argument alternations)
>>> class A(): pass
Suite[Class(A, [], Suite[Pass])]
>>> class A(object): pass
Suite[Class(A, [Var(object)], Suite[Pass])]
>>> class A(object,): pass
Suite[Class(A, [Var(object)], Suite[Pass])]
>>> class A(object,): pass
Suite[Class(A, [Var(object)], Suite[Pass])]
>>> class A(int, str): pass
Suite[Class(A, [Var(int), Var(str)], Suite[Pass])]
>>> class A(int, str,): pass
Suite[Class(A, [Var(int), Var(str)], Suite[Pass])]
>>> class A(metaclass=type): pass
Suite[Class(A, [metaclass=Var(type)], Suite[Pass])]
>>> class A(metaclass=type,): pass
Suite[Class(A, [metaclass=Var(type)], Suite[Pass])]
>>> class A(object, metaclass=type,): pass
Suite[Class(A, [Var(object), metaclass=Var(type)], Suite[Pass])]

# decorated statement
>>> @deco
... while 1: pass
SyntaxError
