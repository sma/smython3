/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.fail;

public class ParserDocTest {
  private static String parse(String source) {
    Parser parser = new Parser(new Scanner(source));
    return parser.parseFileInput().toString();
  }

  public static TestSuite suite() throws IOException {
    TestSuite testSuite = new TestSuite("Doctests");
    testSuite.addTest(createTestSuite("statements"));
    testSuite.addTest(createTestSuite("expressions"));
    return testSuite;
  }

  private static TestSuite createTestSuite(String name) throws IOException {
    TestSuite testSuite = new TestSuite(name);
    InputStream in = ParserTest.class.getResourceAsStream("tests/" + name + ".py");
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
          testSuite.addTest(new ParserTestCase(line, test));
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
    return testSuite;
  }

  private static class ParserTestCase extends TestCase {
    private final String line;
    private final String test;

    public ParserTestCase(String line, String test) {
      super(test);
      this.line = line;
      this.test = test;
    }

    @Override
    protected void runTest() throws Throwable {
      try {
        assertEquals(test, line, parse(test));
      } catch (ParserException e) {
        assertEquals(test, line, "SyntaxError");
      }
    }
  }
}
