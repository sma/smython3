/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import java.util.List;
import java.util.ArrayList;

/** Represents a sequence of statements as part of the AST. */
class Suite {
  final List<Stmt> stmts = new ArrayList<Stmt>();

  void add(Stmt stmt) {
    stmts.add(stmt);
  }

  void execute(Frame f) {
    for (Stmt stmt : stmts) {
      stmt.execute(f);
    }
  }

  Python.Obj eval(Frame f) {
    Python.Obj result = Python.None;
    for (Stmt stmt : stmts) {
      result = stmt.eval(f);
    }
    return result;
  }

  @Override
  public String toString() {
    return "Suite" + stmts;
  }
}
