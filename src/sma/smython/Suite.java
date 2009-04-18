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

  @Override
  public String toString() {
    return "Suite" + stmts;
  }
}
