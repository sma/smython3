/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import sma.smython.Python.Obj;
import sma.smython.Python.Str;

import java.util.ArrayList;
import java.util.List;

/** Represents all arguments for a call or class definition. */
class Arglist {
  private final List<Expr> positionals = new ArrayList<Expr>();
  private final List<KwExpr> keywords = new ArrayList<KwExpr>();
  private Expr restPositionals;
  private Expr restKeywords;

  void addPositional(Expr value) {
    positionals.add(value);
  }

  void addKeyword(Str name, Expr value) {
    keywords.add(new KwExpr(name, value));
  }

  void setRestPositionals(Expr value) {
    restPositionals = value;
  }

  void setRestKeywords(Expr value) {
    restKeywords = value;
  }

  @Override
  public String toString() {
    String s = "[";
    for (Expr e : positionals) {
      if (s.length() > 1) {
        s += ", ";
      }
      s += e;
    }
    for (KwExpr kwExpr : keywords) {
      if (s.length() > 1) {
        s += ", ";
      }
      s += kwExpr.name + "=" + kwExpr.value;
    }
    if (restPositionals != null) {
      if (s.length() > 1) {
        s += ", ";
      }
      s += "*" + restPositionals;
    }
    if (restKeywords != null) {
      if (s.length() > 1) {
        s += ", ";
      }
      s += "**" + restKeywords;
    }
    return s + "]";
  }

  Obj[] eval(Frame f) {
    throw new UnsupportedOperationException();
  }

  static class KwExpr {
    final Str name;
    final Expr value;

    public KwExpr(Str name, Expr value) {
      this.name = name;
      this.value = value;
    }
  }
}
