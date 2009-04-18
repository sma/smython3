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

  @Test
  public void addTwoNumbers() {
    assertEquals(Python.Int(7), eval("3+4"));
  }
}
