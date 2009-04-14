/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import java.util.List;

/** Represents an AST statement. */
abstract class Stmt {
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
  }

  static class Break extends Stmt {
    @Override
    public String toString() {
      return "Break";
    }
  }

  static class Continue extends Stmt {
    @Override
    public String toString() {
      return "Continue";
    }
  }

  static class Del extends Stmt {
    private final ExprList exprList;

    public Del(ExprList exprList) {
      this.exprList = exprList;
    }

    @Override
    public String toString() {
      return "Del" + exprList;
    }
  }

  static class Pass extends Stmt {
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
  }

  static class Raise extends Stmt {
    final Expr exception;
    final Expr from;

    Raise(Expr exception, Expr from) {
      this.exception = exception;
      this.from = from;
    }
  }

  static class Yield extends Stmt {
    final Expr expr;

    Yield(Expr expr) {
      this.expr = expr;
    }
  }

  static class Import extends Stmt {
    final List<DottedName> dottedNames;

    Import(List<DottedName> dottedNames) {
      this.dottedNames = dottedNames;
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
  }

  static class Nonlocal extends Stmt {
    final List<String> names;

    Nonlocal(List<String> names) {
      this.names = names;
    }
  }

  static class Assert extends Stmt {
    final Expr test;
    final Expr message;

    Assert(Expr test, Expr message) {
      this.test = test;
      this.message = message;
    }
  }

  static class AddAssign extends Stmt {
    final ExprList left;
    final ExprList right;

    AddAssign(ExprList left, ExprList right) {
      this.left = left;
      this.right = right;
    }
  }

  static class SubAssign extends Stmt {
    final ExprList left;
    final ExprList right;

    SubAssign(ExprList left, ExprList right) {
      this.left = left;
      this.right = right;
    }
  }

  static class Assign extends Stmt {
    final ExprList left;
    final ExprList right;

    Assign(ExprList left, ExprList right) {
      this.left = left;
      this.right = right;
    }
  }

  static class ExprStmt extends Stmt {
    final ExprList exprList;

    ExprStmt(ExprList exprList) {
      this.exprList = exprList;
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
  }

  static class FuncDef extends Stmt {
    final String name;
    final Params params;
    final Suite body;
    List<Decorator> decorators;

    FuncDef(String name, Params params, Suite body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    void setDecorators(List<Decorator> decorators) {
      this.decorators = decorators;
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
    final String name;
    final Arglist arglist;
    final Suite body;
    List<Decorator> decorators;

    ClassDef(String name, Arglist arglist, Suite body) {
      this.name = name;
      this.arglist = arglist;
      this.body = body;
    }

    void setDecorators(List<Decorator> decorators) {
      this.decorators = decorators;
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
