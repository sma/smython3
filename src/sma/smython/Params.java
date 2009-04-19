/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import sma.smython.Python.*;

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

  Dict bind(Frame f, Obj[] args) {
    Dict locals = new Dict();
    for (int i = 0; i < args.length; i++) {
      Param param = params.get(i);
      locals.setItem(param.name, args[i]);
    }
    for (int i = args.length; i < params.size(); i++) {
      Param param = params.get(i);
      locals.setItem(param.name, param.init.eval(f)); // TODO this should be evaluated on function declaration
    }
    return locals;
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
    final Str name;
    final Expr type;
    final Expr init;

    Param(String name, Expr type, Expr init) {
      this.name = new Str(name);
      this.type = type;
      this.init = init;
    }

    @Override
    public String toString() {
      return name + (type != null ? ":" + type : "") + (init != null ? "=" + init : "");
    }
  }
}
