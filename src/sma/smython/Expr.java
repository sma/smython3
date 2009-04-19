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
  }

  static class Not extends Expr {
    final Expr test;

    Not(Expr test) {
      this.test = test;
    }

    Obj eval(Frame f) {
      return Python.bool(test.eval(f).truish());
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
  }

  static class UnaryMinus extends Expr {
    final Expr expr;

    UnaryMinus(Expr expr) {
      this.expr = expr;
    }

    Obj eval(Frame f) {
      return expr.eval(f).neg();
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
  }

  static class BitNeg extends Expr {
    final Expr expr;

    BitNeg(Expr expr) {
      this.expr = expr;
    }

    Obj eval(Frame f) {
      return expr.eval(f).invert();
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

  static class GetItem extends Expr {
    final Expr obj;
    final ExprList index;

    GetItem(Expr obj, ExprList index) {
      this.obj = obj;
      this.index = index;
    }

    Obj eval(Frame f) {
      return obj.eval(f).getItem(index.eval(f));
    }

    @Override
    public String toString() {
      return "GetItem(" + obj + ", " + index + ")"; 
    }
  }

  static class GetAttr extends Expr {
    final Expr obj;
    final String name;

    GetAttr(Expr obj, String name) {
      this.obj = obj;
      this.name = name;
    }

    Obj eval(Frame f) {
      return obj.eval(f).getAttr(new Str(name));
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
      return "Lit(" + value + ")";
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
      return " for " + vars + " in " + items;
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

  /** Represents a key-value pair for dict comprehensions and dict makers. */
  static class KV extends Expr {
    final Expr key;
    final Expr value;

    KV(Expr key, Expr value) {
      this.key = key;
      this.value = value;
    }

    Obj eval(Frame f) {
      return new Python.List(key.eval(f), value.eval(f));
    }
  }

  static class DictCompr extends Expr {
    final KV kv;
    final Compr compr;

    DictCompr(KV kv, Compr compr) {
      this.kv = kv;
      this.compr = compr;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a generator type
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
  }

  static class SetConstr extends Expr {
    final ExprList exprList;

    SetConstr(ExprList exprList) {
      this.exprList = exprList;
    }

    Obj eval(Frame f) {
      throw new UnsupportedOperationException(); // TODO create a set type
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
