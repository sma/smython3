/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/** Represents the Python runtime objects (first attempt). */
public class Python {
  static final Int[] INTS = new Int[1002];
  static {
    for (int i = -2; i < 1000; i++) {
      INTS[i + 2] = new Int(i);
    }
  }

  static final RuntimeException breakException = new RuntimeException();
  static final RuntimeException continueException = new RuntimeException();
  static final RuntimeException returnException = new RuntimeException();

  public static final Obj None = new None();
  public static final Obj True = Int(1);
  public static final Obj False = Int(0);
  public static final Obj Ellipsis = new Ellipsis();

  public static Int Int(int value) {
    return value >= -2 && value < 1000 ? INTS[value + 2] : new Int(value);
  }

  public static Str Str(String value) {
    return new Str(value);
  }

  public static Obj bool(boolean bool) {
    return bool ? True : False;
  }

  // --------------------------------------------------------------------------------

  static abstract class Obj {

    public boolean truish() {
      return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public Str repr() {
      throw new UnsupportedOperationException();
    }

    public Obj getItem(Obj key) {
      throw new UnsupportedOperationException();
    }

    public void setItem(Obj key, Obj value) {
      throw new UnsupportedOperationException();
    }

    public Obj call(Frame f, Obj... args) {
      throw new UnsupportedOperationException();
    }

    public Obj getAttr(Str name) {
      throw new UnsupportedOperationException();
    }

    public Obj setAttr(Str name, Obj value) {
      throw new UnsupportedOperationException();
    }

    public Obj iter() {
      throw new UnsupportedOperationException();
    }

    public Obj next() {
      throw new UnsupportedOperationException();
    }

    public Obj or(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj xor(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj and(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj lshift(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj rshift(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj add(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj sub(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj mul(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj div(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj mod(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj intDiv(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj power(Obj other) {
      throw new UnsupportedOperationException();
    }

    public Obj neg() {
      throw new UnsupportedOperationException();
    }

    public Obj pos() {
      throw new UnsupportedOperationException();
    }

    public Obj invert() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return repr().value;
    }
  }

  static class None extends Obj {
    @Override
    public Str repr() {
      return Str("None");
    }

    @Override
    public String toString() {
      return "None";
    }
  }

  static class Int extends Obj {
    final int value;

    public Int(int value) {
      this.value = value;
    }

    @Override
    public Str repr() {
      return Str(String.valueOf(value));
    }

    @Override
    public Obj add(Obj other) {
      if (other instanceof Int) {
        return Int(value + ((Int) other).value);
      }
      return super.add(other);
    }

    @Override
    public boolean equals(Object o) {
      return o == this || o instanceof Int && ((Int) o).value == value;
    }

    @Override
    public int hashCode() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
  }

  static class Str extends Obj {
    final String value;

    Str(String value) {
      this.value = value;
    }

    @Override
    public Str repr() {
      if (value.indexOf('\'') != -1) {
        if (value.indexOf('"') == -1) {
          return Str('"' + value + "'");
        }
        StringBuilder b = new StringBuilder(value.length() * 2);
        b.append('\'');
        for (int i = 0; i < value.length(); i++) {
          char ch = value.charAt(i);
          if (ch == '\'') {
            b.append('\\');
          }
          b.append(ch);
        }
        b.append('\'');
        return Str(b.toString());
      }
      return Str('\'' + value + '\'');
    }

    @Override
    public boolean equals(Object o) {
      return o == this || o instanceof Str && ((Str) o).value.equals(value);
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }

    @Override
    public String toString() {
      return value;
    }
  }

  static class List extends Obj {
    final ArrayList<Obj> values;

    List() {
      this(0);
    }

    List(int capacity) {
      this(new ArrayList<Obj>(capacity));
    }

    List(Obj... values) {
      this(new ArrayList<Obj>(Arrays.asList(values)));
    }

    List(ArrayList<Obj> values) {
      this.values = values;
    }

    @Override
    public Obj getItem(Obj key) {
      return values.get(((Int) key).value);
    }

    @Override
    public void setItem(Obj key, Obj value) {
      values.set(((Int) key).value, value);
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof List && ((List) o).values.equals(values);
    }
  }

  static class Dict extends Obj {
    final HashMap<Obj, Obj> values;

    Dict() {
      this(0);
    }

    Dict(int capacity) {
      this(new HashMap<Obj, Obj>(capacity));
    }

    Dict(Obj... values) {
      this(convertToHashMap(values));
    }

    private static HashMap<Obj, Obj> convertToHashMap(Obj[] values) {
      HashMap<Obj, Obj> dict = new HashMap<Obj, Obj>(values.length / 2);
      for (int i = 0; i < values.length; i += 2) {
        dict.put(values[i], values[i + 1]);
      }
      return dict;
    }

    Dict(HashMap<Obj, Obj> values) {
      this.values = values;
    }

    @Override
    public Obj getItem(Obj key) {
      return values.get(key);
    }

    @Override
    public void setItem(Obj key, Obj value) {
      values.put(key, value);
    }
  }

  static class Func extends Obj {
    Str name;
    Params params;
    Suite body;
    Dict globals;

    Func(Str name, Params params, Suite body, Dict globals) {
      this.name = name;
      this.params = params;
      this.body = body;
      this.globals = globals;
    }

    @Override
    public Obj call(Frame f, Obj... args) {
      f = new Frame(params.bind(f, args), globals);
      try {
        body.execute(f);
      } catch (RuntimeException e) {
        return f.result;
      }
      return Python.None;
    }
  }

  static class Type extends Obj {
    Str name;
    Obj[] bases;
    Dict dict;

    Type(Str name, Obj[] bases, Dict dict) {
      this.name = name;
      this.bases = bases;
      this.dict = dict;
    }
  }

  static class Ellipsis extends Obj {
    @Override
    public Str repr() {
      return Str("Ellipsis");
    }
  }

}
