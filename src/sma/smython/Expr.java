/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import sma.smython.Python.*;

import java.util.ArrayList;
import java.util.List;

/** Represents an AST expression. */
abstract class Expr {
  abstract Obj eval(Frame f);

  void set(Frame f, Obj value) {
    throw new UnsupportedOperationException();
  }

  static class Lambda extends Expr {
    final Params params;
    final Expr test;

    Lambda(Params params, Expr test) {
      this.params = params;
      this.test = test;
    }

    Obj eval(Frame f) {
      ExprList exprList = new ExprList();
      exprList.add(test);
      Suite suite = new Suite();
      suite.add(new Stmt.Return(exprList));
      Str name = new Str("<lambda>");
      return new Func(name, params, suite, f.globals);
    }

    @Override
    public String toString() {
      return "Lambda(" + params + ", " + test + ")";
    }
  }

  static class IfElse extends Expr {
    final Expr condition;
    final Expr consequence;
    final Expr alternative;

    IfElse(Expr condition, Expr consequence, Expr alternative) {
      this.condition = condition;
      this.consequence = consequence;
      this.alternative = alternative;
    }

    Obj eval(Frame f) {
      return (condition.eval(f).truish() ? consequence : alternative).eval(f);
    }

    @Override
    public String toString() {
      return "IfElse(" + condition + ", " + consequence + ", " + alternative + ")";
    }
  }

  static class And extends Expr {
    final Expr left;
    final Expr right;

    And(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      Obj result = left.eval(f);
      return result.truish() ? right.eval(f) : result;
    }

    @Override
    public String toString() {
      return "And(" + left + ", " + right + ")";
    }
  }

  static class Or extends Expr {
    final Expr left;
    final Expr right;

    Or(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      Obj result = left.eval(f);
      return result.truish() ? result : right.eval(f);
    }

    @Override
    public String toString() {
      return "Or(" + left + ", " + right + ")";
    }
  }

  static class Not extends Expr {
    final Expr test;

    Not(Expr test) {
      this.test = test;
    }

    Obj eval(Frame f) {
      return Python.bool(test.eval(f).truish());
    }

    @Override
    public String toString() {
      return "Not(" + test + ")";
    }
  }

  static class Comparison extends Expr {
    final Expr left;
    final List<Comp> comps = new ArrayList<Comp>();

    Comparison(Expr left) {
      this.left = left;
    }

    void add(Comp comp) {
      comps.add(comp);
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "Comparison(" + left + Stmt.join(comps, "") + ")";
    }
  }

  static abstract class Comp {
    final Expr right;

    Comp(Expr right) {
      this.right = right;
    }

    @Override
    public String toString() {
      return " " + op() + " " + right;
    }

    abstract String op();

    static class LT extends Comp {
      LT(Expr right) {
        super(right);
      }

      String op() {
        return "<";
      }
    }

    static class GT extends Comp {
      GT(Expr right) {
       super(right);
      }

      String op() {
        return ">";
      }
    }

    static class LE extends Comp {
      LE(Expr right) {
        super(right);
      }

      String op() {
        return "<=";
      }
    }

    static class GE extends Comp {
      GE(Expr right) {
        super(right);
      }

      String op() {
        return ">=";
      }
    }

    static class EQ extends Comp {
      EQ(Expr right) {
        super(right);
      }

      String op() {
        return "==";
      }
    }

    static class NE extends Comp {
      NE(Expr right) {
        super(right);
      }

      String op() {
        return "!=";
      }
    }

    static class In extends Comp {
      In(Expr right) {
        super(right);
      }

      String op() {
        return "in";
      }
    }

    static class NotIn extends Comp {
      NotIn(Expr right) {
        super(right);
      }

      String op() {
        return "not in";
      }
    }

    static class Is extends Comp {
      Is(Expr right) {
        super(right);
      }

      String op() {
        return "is";
      }
    }

    static class IsNot extends Comp {
      IsNot(Expr right) {
        super(right);
      }

      String op() {
        return "is not";
      }
    }
  }

  static class Star extends Expr {
    final Expr expr;

    Star(Expr expr) {
      this.expr = expr;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "Star(" + expr + ")";
    }
  }

  static class BitOr extends Expr {
    final Expr left;
    final Expr right;

    BitOr(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).or(right.eval(f));
    }

    @Override
    public String toString() {
      return "BitOr(" + left + ", " + right + ")";
    }
  }

  static class BitXor extends Expr {
    final Expr left;
    final Expr right;

    BitXor(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).xor(right.eval(f));
    }

    @Override
    public String toString() {
      return "BitXor(" + left + ", " + right + ")";
    }
  }

  static class BitAnd extends Expr {
    final Expr left;
    final Expr right;

    BitAnd(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).and(right.eval(f));
    }

    @Override
    public String toString() {
      return "BitAnd(" + left + ", " + right + ")";
    }
  }

  static class BitShiftLeft extends Expr {
    final Expr left;
    final Expr right;

    BitShiftLeft(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).lshift(right.eval(f));
    }

    @Override
    public String toString() {
      return "BitShiftLeft(" + left + ", " + right + ")";
    }
  }

  static class BitShiftRight extends Expr {
    final Expr left;
    final Expr right;

    BitShiftRight(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).rshift(right.eval(f));
    }

    @Override
    public String toString() {
      return "BitShiftRight(" + left + ", " + right + ")";
    }
  }

  static class Add extends Expr {
    final Expr left;
    final Expr right;

    Add(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).add(right.eval(f));
    }

    @Override
    public String toString() {
      return "Add(" + left + ", " + right + ")";
    }
  }

  static class Sub extends Expr {
    final Expr left;
    final Expr right;

    Sub(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).sub(right.eval(f));
    }

    @Override
    public String toString() {
      return "Sub(" + left + ", " + right + ")";
    }
  }

  static class Mul extends Expr {
    final Expr left;
    final Expr right;

    Mul(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).mul(right.eval(f));
    }

    @Override
    public String toString() {
      return "Mul(" + left + ", " + right + ")";
    }
  }

  static class Div extends Expr {
    final Expr left;
    final Expr right;

    Div(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).div(right.eval(f));
    }

    @Override
    public String toString() {
      return "Div(" + left + ", " + right + ")";
    }
  }

  static class Mod extends Expr {
    final Expr left;
    final Expr right;

    Mod(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).mod(right.eval(f));
    }

    @Override
    public String toString() {
      return "Mod(" + left + ", " + right + ")";
    }
  }

  static class IntDiv extends Expr {
    final Expr left;
    final Expr right;

    IntDiv(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).intDiv(right.eval(f));
    }

    @Override
    public String toString() {
      return "IntDiv(" + left + ", " + right + ")";
    }
  }

  static class UnaryMinus extends Expr {
    final Expr expr;

    UnaryMinus(Expr expr) {
      this.expr = expr;
    }

    Obj eval(Frame f) {
      return expr.eval(f).neg();
    }

    @Override
    public String toString() {
      return "Neg(" + expr + ")";
    }
  }

  static class UnaryPlus extends Expr {
    final Expr expr;

    UnaryPlus(Expr expr) {
      this.expr = expr;
    }

    Obj eval(Frame f) {
      return expr.eval(f).pos();
    }

    @Override
    public String toString() {
      return "Pos(" + expr + ")";
    }
  }

  static class BitNeg extends Expr {
    final Expr expr;

    BitNeg(Expr expr) {
      this.expr = expr;
    }

    Obj eval(Frame f) {
      return expr.eval(f).invert();
    }

    @Override
    public String toString() {
      return "Invert(" + expr + ")";
    }
  }

  static class Call extends Expr {
    final Expr callable;
    final Arglist arglist;

    Call(Expr callable, Arglist arglist) {
      this.callable = callable;
      this.arglist = arglist;
    }

    Obj eval(Frame f) {
      return callable.eval(f).call(f, arglist.eval(f));
    }

    @Override
    public String toString() {
      return "Call(" + callable + ", " + arglist + ")";
    }
  }

  static class Index extends Expr {
    final Expr obj;
    final ExprList index;

    Index(Expr obj, ExprList index) {
      this.obj = obj;
      this.index = index;
    }

    Obj eval(Frame f) {
      return obj.eval(f).getItem(index.eval(f));
    }

    @Override
    public String toString() {
      return "Index(" + obj + ", " + index + ")";
    }
  }

  static class Attr extends Expr {
    final Expr obj;
    final String name;

    Attr(Expr obj, String name) {
      this.obj = obj;
      this.name = name;
    }

    Obj eval(Frame f) {
      return obj.eval(f).getAttr(new Str(name));
    }

    @Override
    public String toString() {
      return "Attr(" + obj + ", " + name + ")";
    }
  }

  static class Power extends Expr {
    final Expr left;
    final Expr right;

    Power(Expr left, Expr right) {
      this.left = left;
      this.right = right;
    }

    Obj eval(Frame f) {
      return left.eval(f).power(right.eval(f));
    }

    @Override
    public String toString() {
      return "Power(" + left + ", " + right + ")";
    }
  }

  static class Lit extends Expr {
    final Obj value;

    Lit(Obj value) {
      this.value = value;
    }

    Obj eval(Frame f) {
      return value;
    }

    @Override
    public String toString() {
      return "Lit(" + value.repr() + ")";
    }
  }

  static class Var extends Expr {
    final Str name;

    Var(String name) {
      this.name = new Str(name); // TODO parser needs to generate Str not String
    }

    Obj eval(Frame f) {
      return f.get(name); // TODO parser needs to distinguish locals and globals
    }

    @Override
    public String toString() {
      return "Var(" + name + ")";
    }
  }

  static class Yield extends Expr {
    final ExprList exprList;

    Yield(ExprList exprList) {
      this.exprList = exprList;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "Yield" + exprList;
    }
  }

  /** Represents the iterator and condition part of a comprehension. */
  static abstract class Compr {
  }

  static class ComprFor extends Compr {
    final ExprList vars;
    final Expr items;
    final Compr compr;

    ComprFor(ExprList vars, Expr items, Compr compr) {
      this.vars = vars;
      this.items = items;
      this.compr = compr;
    }

    @Override
    public String toString() {
      return (compr == null ? "" : compr) + " for " + vars + " in " + items;
    }
  }

  static class ComprIf extends Compr {
    final Expr cond;
    final Compr compr;

    ComprIf(Expr cond, Compr compr) {
      this.cond = cond;
      this.compr = compr;
    }

    @Override
    public String toString() {
      return compr + " if " + cond;
    }
  }

  static class DictCompr extends Expr {
    final Expr key;
    final Expr value;
    final Compr compr;

    public DictCompr(Expr key, Expr value, Compr compr) {
      this.key = key;
      this.value = value;
      this.compr = compr;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a generator type
    }

    @Override
    public String toString() {
      return "DictCompr(" + key + ", " + value + compr + ")";
    }
  }

  static class DictConstr extends Expr {
    final ExprList exprList;

    DictConstr(ExprList exprList) {
      this.exprList = exprList;
    }

    Obj eval(Frame f) {
      Obj dict = new Dict();
      Obj iter = exprList.eval(f).iter();
      Obj next = iter.next();
      while (next != null) {
        dict.setItem(next.getItem(Python.Int(0)), next.getItem(Python.Int(1)));
        next = iter.next();
      }
      return dict;
    }

    @Override
    public String toString() {
      return "DictConstr" + exprList;
    }
  }

  static class SetCompr extends Expr {
    final Expr expr;
    final Compr compr;

    SetCompr(Expr expr, Compr compr) {
      this.expr = expr;
      this.compr = compr;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a generator type
    }

    @Override
    public String toString() {
      return "SetCompr(" + expr + compr + ")";
    }
  }

  static class SetConstr extends Expr {
    final ExprList exprList;

    SetConstr(ExprList exprList) {
      this.exprList = exprList;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a set type
    }

    @Override
    public String toString() {
      return "SetConstr" + exprList;
    }
  }

  static class ListCompr extends Expr {
    final Expr expr;
    final Compr compr;

    ListCompr(Expr expr, Compr compr) {
      this.expr = expr;
      this.compr = compr;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a generator type
    }

    @Override
    public String toString() {
      return "ListCompr(" + expr + compr + ")";
    }
  }

  static class ListConstr extends Expr {
    final ExprList exprList;

    ListConstr(ExprList exprList) {
      this.exprList = exprList;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a set type
    }

    @Override
    public String toString() {
      return "ListConstr" + exprList;
    }
  }

  static class GeneratorCompr extends Expr {
    final Expr expr;
    final Compr compr;

    GeneratorCompr(Expr expr, Compr compr) {
      this.expr = expr;
      this.compr = compr;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a generator type
    }

    @Override
    public String toString() {
      return "GeneratorCompr(" + expr + compr + ")";
    }
  }

  static class TupleConstr extends Expr {
    final ExprList exprList;

    TupleConstr(ExprList exprList) {
      this.exprList = exprList;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "TupleConstr" + exprList;
    }
  }

  static class Slice extends Expr {
    final Expr start;
    final Expr stop;
    final Expr step;

    Slice(Expr start, Expr stop, Expr step) {
      this.start = start;
      this.stop = stop;
      this.step = step;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a slice type
    }

    @Override
    public String toString() {
      return (start == null ? "" : start) + ":" + (stop == null ? "" : stop) + (step == null ? "" : ":" + step);
    }
  }
}
