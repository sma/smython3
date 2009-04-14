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
    assertEquals("Suite[Del[null]]", parse("del\n")); // TODO this MUST be an error
    assertEquals("Suite[Del[Var(a)]]", parse("del a\n"));
    assertEquals("Suite[Del[Var(a), Var(b)]]", parse("del a, b,\n"));
  }

  @Test
  public void parseFromTestsPy() throws IOException {
    InputStream in = ParserTest.class.getResourceAsStream("Tests.py");
    BufferedReader r = new BufferedReader(new InputStreamReader(in));
    try {
      String line = r.readLine();
      String test = "";
      while (line != null) {
        line = line.trim();
        if (line.startsWith("###")) {
        } else if (line.startsWith(">>> ") || line.startsWith("... ")) {
          test += line.substring(4) + "\n";
        } else if (line.length() > 0) {
          assertEquals(line, parse(test));
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
}
