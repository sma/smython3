/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import sma.smython.Python.*;

import java.util.List;

/** Represents an AST statement. */
abstract class Stmt {
  abstract void execute(Frame f);

  Obj eval(Frame f) {
    execute(f);
    return Python.None;
  }

  static String join(List<?> objects, Object separator) {
    StringBuilder b = new StringBuilder();
    for (Object object : objects) {
      if (b.length() > 0) {
        b.append(separator);
      }
      b.append(object);
    }
    return b.toString();
  }

  static class Decorator {
    final List<String> dottedName;
    final Arglist arglist;

    Decorator(List<String> dottedName, Arglist arglist) {
      this.dottedName = dottedName;
      this.arglist = arglist;
    }

    @Override
    public String toString() {
      return "@" + join(dottedName, ".") + (arglist == null ? "" : arglist);
    }

    Obj eval(Frame f) {
      Obj obj = null;
      for (String name : dottedName) {
        if (obj == null) {
          obj = f.get(new Str(name));
        } else {
          obj = obj.getAttr(new Str(name));
        }
      }
      if (arglist != null) {
        obj = obj.call(f, arglist.eval(f));
      }
      return obj;
    }
  }

  static class Break extends Stmt {
    @Override
    public String toString() {
      return "Break";
    }

    void execute(Frame f) {
      throw Python.breakException;
    }
  }

  static class Continue extends Stmt {
    @Override
    public String toString() {
      return "Continue";
    }

    void execute(Frame f) {
      throw Python.continueException;
    }
  }

  static class Del extends Stmt {
    final ExprList exprList;

    public Del(ExprList exprList) {
      this.exprList = exprList;
    }

    void execute(Frame f) {
      // TODO delete all target expressions from Frame
    }

    @Override
    public String toString() {
      return "Del" + exprList;
    }
  }

  static class Pass extends Stmt {
    void execute(Frame f) {
    }

    @Override
    public String toString() {
      return "Pass";
    }
  }

  static class Return extends Stmt {
    final ExprList exprList;

    Return(ExprList exprList) {
      this.exprList = exprList;
    }

    void execute(Frame f) {
      f.result = exprList.eval(f);
      throw Python.returnException;
    }

    @Override
    public String toString() {
      return "Return" + exprList;
    }
  }

  static class Raise extends Stmt {
    final Expr exception;
    final Expr from;

    Raise(Expr exception, Expr from) {
      this.exception = exception;
      this.from = from;
    }

    void execute(Frame f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "Raise(" + (exception == null ? "" : exception + (from == null ? "" : ", " + from)) + ")";
    }
  }

  static class Yield extends Stmt {
    final Expr expr;

    Yield(Expr expr) {
      this.expr = expr;
    }

    void execute(Frame f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return expr.toString();
    }
  }

  static class Import extends Stmt {
    final List<DottedName> dottedNames;

    Import(List<DottedName> dottedNames) {
      this.dottedNames = dottedNames;
    }

    void execute(Frame f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "Import" + dottedNames;
    }
  }

  static class DottedName {
    final List<String> dottedName;
    final String alias;

    DottedName(List<String> dottedName, String alias) {
      this.dottedName = dottedName;
      this.alias = alias;
    }

    @Override
    public String toString() {
      return join(dottedName, ".") + (alias != null ? " as " + alias : "");
    }
  }

  static class From extends Stmt {
    final List<String> dottedName;
    final List<NameAlias> importNames;

    From(List<String> dottedName, List<NameAlias> importNames) {
      this.dottedName = dottedName;
      this.importNames = importNames;
    }

    void execute(Frame f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "From(" + join(dottedName, ".") + ", " + importNames + ")";
    }
  }

  static class NameAlias {
    final String name;
    final String alias;

    NameAlias(String name, String alias) {
      this.name = name;
      this.alias = alias;
    }

    @Override
    public String toString() {
      return name + (alias != null ? " as " + alias : "");
    }
  }

  static class Global extends Stmt {
    final List<String> names;

    Global(List<String> names) {
      this.names = names;
    }

    void execute(Frame f) {
    }
  }

  static class Nonlocal extends Stmt {
    final List<String> names;

    Nonlocal(List<String> names) {
      this.names = names;
    }

    void execute(Frame f) {
    }
  }

  static class Assert extends Stmt {
    final Expr test;
    final Expr message;

    Assert(Expr test, Expr message) {
      this.test = test;
      this.message = message;
    }

    void execute(Frame f) {
      if (!test.eval(f).truish()) {
        throw new AssertionError(message.eval(f));
      }
    }
  }

  static class AddAssign extends Stmt {
    final ExprList left;
    final ExprList right;

    AddAssign(ExprList left, ExprList right) {
      this.left = left;
      this.right = right;
    }

    void execute(Frame f) {
      left.set(f, left.eval(f).add(right.eval(f)));
    }
  }

  static class SubAssign extends Stmt {
    final ExprList left;
    final ExprList right;

    SubAssign(ExprList left, ExprList right) {
      this.left = left;
      this.right = right;
    }

    void execute(Frame f) {
      left.set(f, left.eval(f).sub(right.eval(f)));
    }
  }

  static class Assign extends Stmt {
    final ExprList left;
    final ExprList right;

    Assign(ExprList left, ExprList right) {
      this.left = left;
      this.right = right;
    }

    void execute(Frame f) {
      left.set(f, right.eval(f));
    }
  }

  static class ExprStmt extends Stmt {
    final ExprList exprList;

    ExprStmt(ExprList exprList) {
      this.exprList = exprList;
    }

    void execute(Frame f) {
      exprList.eval(f);
    }

    @Override
    Obj eval(Frame f) {
      return exprList.eval(f);
    }

    @Override
    public String toString() {
      return "Expr" + exprList;
    }
  }

  static class If extends Stmt {
    final Expr testExpr;
    final Suite thenSuite;
    final Suite elseSuite;

    If(Expr testExpr, Suite thenSuite, Suite elseSuite) {
      this.testExpr = testExpr;
      this.thenSuite = thenSuite;
      this.elseSuite = elseSuite;
    }

    void execute(Frame f) {
      if (testExpr.eval(f).truish()) {
        thenSuite.execute(f);
      } else {
        elseSuite.execute(f);
      }
    }

    @Override
    public String toString() {
      return "If(" + testExpr + ", " + thenSuite + ", " + elseSuite + ")";
    }
  }

  static class While extends Stmt {
    final Expr testExpr;
    final Suite bodySuite;
    final Suite elseSuite;

    While(Expr testExpr, Suite bodySuite, Suite elseSuite) {
      this.testExpr = testExpr;
      this.bodySuite = bodySuite;
      this.elseSuite = elseSuite;
    }

    void execute(Frame f) {
      while (testExpr.eval(f).truish()) {
        try {
          bodySuite.execute(f);
        } catch (RuntimeException e) {
          if (e == Python.breakException) {
            return;
          }
          if (e != Python.continueException) {
            throw e;
          }
        }
      }
      elseSuite.execute(f);
    }

    @Override
    public String toString() {
      return "While(" + testExpr + ", " + bodySuite + (elseSuite == null ? "" : ", " + elseSuite) + ")";
    }
  }

  static class For extends Stmt {
    final ExprList names;
    final ExprList items;
    final Suite bodySuite;
    final Suite elseSuite;

    For(ExprList names, ExprList items, Suite bodySuite, Suite elseSuite) {
      this.names = names;
      this.items = items;
      this.bodySuite = bodySuite;
      this.elseSuite = elseSuite;
    }

    void execute(Frame f) {
      Python.Obj iter = items.eval(f).iter();
      Python.Obj next = iter.next();
      while (next != null) {
        names.set(f, next);
        try {
          bodySuite.execute(f);
        } catch (RuntimeException e) {
          if (e == Python.breakException) {
            return;
          }
          if (e != Python.continueException) {
            throw e;
          }
        }
        next = iter.next();
      }
      elseSuite.execute(f);
    }

    @Override
    public String toString() {
      return "For(" + names + ", " + items + (elseSuite == null ? "" : ", " + elseSuite) + ")";
    }
  }

  static class Try extends Stmt {
    final Suite bodySuite;
    final List<Except> exceptList;
    final Suite elseSuite;
    final Suite finallySuite;

    Try(Suite bodySuite, List<Except> exceptList, Suite elseSuite, Suite finallySuite) {
      this.bodySuite = bodySuite;
      this.exceptList = exceptList;
      this.elseSuite = elseSuite;
      this.finallySuite = finallySuite;
    }

    void execute(Frame f) {
      try {
        boolean doElse = false;
        try {
          bodySuite.execute(f);
          doElse = elseSuite != null;
        } catch (RuntimeException e) {
          for (Except ex : exceptList) {
            // TODO find the matching Except clause
            ex.clause.eval(f);
          }
        }
        if (doElse) {
          elseSuite.execute(f);
        }
      } finally {
        if (finallySuite != null) {
          finallySuite.execute(f);
        }
      }
    }
  }

  static class Except {
    final Expr clause;
    final String name;

    Except(Expr clause, String name) {
      this.clause = clause;
      this.name = name;
    }
  }

  static class With extends Stmt {
    final Expr expr;
    final Expr binding;
    final Suite bodySuite;

    With(Expr expr, Expr binding, Suite bodySuite) {
      this.expr = expr;
      this.binding = binding;
      this.bodySuite = bodySuite;
    }

    void execute(Frame f) {
      Python.Obj obj = expr.eval(f);
      if (binding != null) {
        binding.set(f, obj);
      }
      // TODO call __enter__
      try {
        bodySuite.execute(f);
      } finally {
        // TODO call __exit__
      }
    }
  }

  static class FuncDef extends Stmt {
    final Str name;
    final Params params;
    final Suite body;
    List<Decorator> decorators;

    FuncDef(String name, Params params, Suite body) {
      this.name = new Str(name);
      this.params = params;
      this.body = body;
    }

    void setDecorators(List<Decorator> decorators) {
      this.decorators = decorators;
    }

    void execute(Frame f) {
      Obj func = new Func(name, params, body, f.globals);
      if (decorators != null) {
        for (Decorator decorator : decorators) {
          func = decorator.eval(f).call(f, func);
        }
      }
      f.set(name, func);
    }

    @Override
    public String toString() {
      String s = "Def(" + name + ", " + params + ", " + body;
      if (decorators != null) {
        s += ", " + decorators;
      }
      return s + ")";
    }
  }

  static class ClassDef extends Stmt {
    final Str name;
    final Arglist arglist;
    final Suite body;
    List<Decorator> decorators;

    ClassDef(String name, Arglist arglist, Suite body) {
      this.name = new Str(name);
      this.arglist = arglist;
      this.body = body;
    }

    void setDecorators(List<Decorator> decorators) {
      this.decorators = decorators;
    }

    void execute(Frame f) {
      Dict dict = new Dict();
      body.execute(new Frame(dict, f.globals));
      Obj type = new Type(name, arglist.eval(f), dict);
      for (Decorator decorator : decorators) {
        type = decorator.eval(f).call(f, type);
      }
      f.set(name, type);
    }

    @Override
    public String toString() {
      String s = "Class(" + name + ", " + body;
      if (decorators != null) {
        s += ", " + decorators;
      }
      return s + ")";
    }
  }

}
