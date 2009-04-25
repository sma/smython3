/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class ParserTest {

  private String parse(String source) {
    Parser parser = new Parser(new Scanner(source));
    return parser.parseFileInput().toString();
  }

  private void testFile(String name) throws IOException {
    InputStream in = ParserTest.class.getResourceAsStream(name);
    BufferedReader r = new BufferedReader(new InputStreamReader(in));
    try {
      String line = r.readLine();
      String test = "";
      while (line != null) {
        line = line.trim();
        if (line.startsWith("#")) {
        } else if (line.startsWith(">>> ") || line.startsWith("... ")) {
          test += line.substring(4) + "\n";
        } else if (line.length() > 0) {
          try {
            assertEquals(line, parse(test));
          } catch (ParserException e) {
            assertEquals(line, "SyntaxError");
          }
          test = "";
        }
        line = r.readLine();
      }
      if (test.length() > 0) {
        fail("missing assert line");
      }
    } finally {
      r.close();
    }
  }

  @Test
  public void parseBreak() {
    assertEquals("Suite[Break]", parse("break\n"));
  }

  @Test
  public void parseContinue() {
    assertEquals("Suite[Continue]", parse("continue\n"));
  }

  @Test
  public void parsePass() {
    assertEquals("Suite[Pass]", parse("pass\n"));
  }

  @Test
  public void parseDel() {
    assertEquals("Suite[Del[Var(a)]]", parse("del a\n"));
    assertEquals("Suite[Del[Var(a), Var(b)]]", parse("del a, b,\n"));
  }

  @Test
  public void parseYieldExpr() {
    assertEquals("Suite[Expr(Yield(Lit(None)))]", parse("(yield)\n"));
  }

  @Test
  public void parseAssignWithOr() {
    assertEquals("Suite[Assign((Var(a)), (Or(Lit(1), Lit(2))))]", parse("a = 1 or 2\n"));
  }

  @Test
  public void parseMultiAssign() {
    assertEquals("Suite[Assign((Var(a)), (Var(b)), (Lit(1)))]", parse("a = b = 1\n"));
  }

  @Test
  public void parseFromTestsPy() throws IOException {
    testFile("Tests.py");
  }

  @Test
  public void parsePythonTests() throws IOException {
    testFile("tests/statements.py");
    testFile("tests/expressions.py");
  }
}
