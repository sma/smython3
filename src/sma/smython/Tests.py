### TODO make this tests systematic

>>> break
Suite[Break]

>>> continue
Suite[Continue]

>>> pass
Suite[Pass]

>>> if 1: pass; pass
Suite[If(Lit(1), Suite[Pass, Pass], null)]

>>> if 1:
...   pass
Suite[If(Lit(1), Suite[Pass], null)] 

>>> if 1:
...   pass
... else:
...   break
Suite[If(Lit(1), Suite[Pass], Suite[Break])]

>>> 1
Suite[Expr[Lit(1)]]
>>> 1, 2,
Suite[Expr[Lit(1), Lit(2)]]

>>> a()
Suite[Expr[Call(Var(a), [])]]

>>> a(0,)
Suite[Expr[Call(Var(a), [Lit(0)])]]

>>> a(0, b=1+2, *c, **d)
Suite[Expr[Call(Var(a), [Lit(0), b=Add(Lit(1), Lit(2)), *Var(c), **Var(d)])]]

### don't work yet
### >>> []
### Suite[Expr[ListConstr[]]]
>>> [1]
Suite[Expr[ListConstr[Lit(1)]]]
>>> [a + 1 for a in b if 2 if 3]
Suite[Expr[ListCompr(Add(Var(a), Lit(1)) for [Var(a)] in Var(b) if Lit(2) if Lit(3))]]

>>> class A: pass
Suite[Class(A, Suite[Pass])]

>>> @decorator1
... @decorator2
... class A: pass
Suite[Class(A, Suite[Pass], [@decorator1, @decorator2])]

>>> while 1: pass
Suite[While(Lit(1), Suite[Pass])]
>>> while 1:
...  pass
Suite[While(Lit(1), Suite[Pass])]
>>> while 1: pass
... else: break
Suite[While(Lit(1), Suite[Pass], Suite[Break])]
>>> while 1: pass
... else:
...  break
Suite[While(Lit(1), Suite[Pass], Suite[Break])]

### c shouldn't be a ListExpr
### >>> for a, b in c: pass
### Suite[For([Var(a), Var(b)], Var(c))]

>>> a[0][1:]
Suite[Expr[GetItem(GetItem(Var(a), [Lit(0)]), [Lit(1):])]]

>>> def f(): pass
Suite[Def(f, [], Suite[Pass])]
>>> def f(a, b:str, c:int=1, *d,): pass
Suite[Def(f, [a, b:Var(str), c:Var(int)=Lit(1), *d], Suite[Pass])]

>>> import a, a.b, c as d
Suite[Import[a, a.b, c as d]]
>>> from a.b import a, a as b
Suite[From(a.b, [a, a as b])]

>>> 1 == 2
Suite[Expr[Comparison(Lit(1) == Lit(2))]]
>>> 1 < b < 2
Suite[Expr[Comparison(Lit(1) < Var(b) < Lit(2))]]
>>> 1 < 2 > 3 <= 4 >= 5 == 6 != 7 in 8 not in 9 is 10 is not 11
Suite[Expr[Comparison(Lit(1) < Lit(2) > Lit(3) <= Lit(4) >= Lit(5) == Lit(6) != Lit(7) in Lit(8) not in Lit(9) is Lit(10) is not Lit(11))]]

>>> return
Suite[Return(Lit(null))]
>>> return 1
Suite[Return(Lit(1))]
>>> return 1,
Suite[Return[Lit(1)]]
>>> return 1, 2
Suite[Return[Lit(1), Lit(2)]]