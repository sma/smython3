/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import java.util.List;
import java.util.ArrayList;

/** Presents the (optionall typed) arguments list of functions and lambda expressions. */
class Params {
  final List<Param> params = new ArrayList<Param>();

  Param restPositional;
  Param restKeyword;
  Expr returnType;

  void add(Param param) {
    params.add(param);
  }

  void setReturnType(Expr returnType) {
    this.returnType = returnType;
  }

  @Override
  public String toString() {
    String s = "[";
    for (Param p : params) {
      if (s.length() > 1) {
        s += ", ";
      }
      s += p;
    }
    if (restPositional != null) {
      if (s.length() > 1) {
        s += ", ";
      }
      s += "*" + restPositional;
    }
    if (restKeyword != null) {
      if (s.length() > 1) {
        s += ", ";
      }
      s += "**" + restKeyword;
    }
    return s + "]";
  }

  static class Param {
    final String name;
    final Expr type;
    final Expr init;

    Param(String name, Expr type, Expr init) {
      this.name = name;
      this.type = type;
      this.init = init;
    }

    @Override
    public String toString() {
      return name + (type != null ? ":" + type : "") + (init != null ? "=" + init : "");
    }
  }
}
