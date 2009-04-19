/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import java.util.List;
import java.util.ArrayList;

/** Represents a list of expressions as part of the AST. */
class ExprList {
  final List<Expr> exprs = new ArrayList<Expr>();
  boolean single; // TODO this is a hack

  void add(Expr expr) {
    exprs.add(expr);
  }

  @Override
  public String toString() {
    if (single && exprs.size() == 1) {
      return "(" + exprs.get(0) + ")";
    }
    return exprs.toString();
  }

  Python.Obj eval(Frame f) {
    if (single && exprs.size() == 1) {
      return exprs.get(0).eval(f);
    }
    Python.List list = new Python.List(exprs.size());
    for (Expr expr : exprs) {
      list.values.add(expr.eval(f));
    }
    return list;
  }
  
  void set(Frame f, Python.Obj value) {
    throw new UnsupportedOperationException();
  }
}
