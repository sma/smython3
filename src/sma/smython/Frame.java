/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import sma.smython.Python.*;

/** Represents an activation record for a user defined function. */
public class Frame {
  Dict locals;
  Dict globals;
  Obj result;

  Frame(Dict locals, Dict globals) {
    this.locals = locals;
    this.globals = globals;
  }

  Obj get(Str name) {
    Obj value = locals.getItem(name);
    if (value != null) {
      return value;
    }
    return getGlobal(name);
  }

  Obj getGlobal(Str name) {
    Obj value = globals.getItem(name);
    if (value != null) {
      return value;
    }
    return null;  //TODO check builtins, raise exception
  }

  void set(Str name, Obj value) {
    locals.setItem(name, value);
  }

  void setGlobal(Str name, Obj value) {
    globals.setItem(name, value);
  }
}
