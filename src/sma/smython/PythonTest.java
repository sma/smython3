/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import org.junit.Test;
import static org.junit.Assert.*;

public class PythonTest {
  private Python.Obj eval(String source) {
    Parser parser = new Parser(new Scanner(source));
    ExprList exprList = parser.parseEvalInput();
    Frame frame = new Frame(new Python.Dict(), new Python.Dict());
    Python.Obj result = exprList.eval(frame);
    return result.getItem(Python.Int(0));
  }

  private Python.Obj exec(String source) {
    Parser parser = new Parser(new Scanner(source));
    Suite suite = parser.parseFileInput();
    Frame frame = new Frame(new Python.Dict(), new Python.Dict());
    Python.Obj result = suite.eval(frame);
    return result.getItem(Python.Int(0));
  }


  @Test
  public void addTwoNumbers() {
    assertEquals(Python.Int(7), eval("3+4"));
  }

  @Test
  public void callAFunction() {
    assertEquals(Python.None, exec("def f(): return\nf()\n"));
    assertEquals(Python.Int(42), exec("def f(): return 42\nf()\n"));
    assertEquals(new Python.List(Python.Int(0)), exec("def f(): return 0,\nf()\n"));
    assertEquals(new Python.List(Python.Int(3), Python.Int(4)), exec("def f(): return 3, 4\nf()\n"));
  } 
}
