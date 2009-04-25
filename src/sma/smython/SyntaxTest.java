/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import org.junit.Test;

import java.io.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SyntaxTest {
  public static junit.framework.Test suite() {
    TestSuite testSuite = new TestSuite("SyntaxTest");
    testSuite.addTest(createTests("Parse all py files", new File("/Users/sma/python3/lib/python3.0")));
    testSuite.addTest(createTests("Parse all py test files", new File("/Users/sma/python3/lib/python3.0/test")));
    return testSuite;
  }

  private static junit.framework.Test createTests(String name, File dir) {
    TestSuite testSuite = new TestSuite(name);
    for (File file : dir.listFiles(new FilenameFilter() {
      public boolean accept(File file, String name) {
        if (name.equals("bad_coding2.py") || name.equals("badsyntax_3131.py")) {
          // first file contains a chinese character, the other a euro, both cannot be scanned
          return false;
        }
        return name.endsWith(".py");
      }
    })) {
      testSuite.addTest(new SyntaxTestCase(file));
    }
    return testSuite;
  }

  static class SyntaxTestCase extends TestCase {
    private final File file;

    SyntaxTestCase(File file) {
      super(file.getName());
      this.file = file;
    }

    @Override
    protected void runTest() throws Throwable {
      parse(read(file));
    }

    private static String read(File file) throws IOException {
      Reader r = new InputStreamReader(new FileInputStream(file), "utf-8");
      StringBuilder b = new StringBuilder();
      char[] buf = new char[4096];
      int len = r.read(buf);
      while (len != -1) {
        b.append(buf, 0, len);
        len = r.read(buf);
      }
      r.close();
      return b.toString();
    }

    private static void parse(String source) throws Exception {
      Parser parser = new Parser(new Scanner(source + "\n"));
      try {
        parser.parseFileInput();
      } catch (Exception e) {
        System.err.println("ERROR in line " + parser.line());
        throw e;
      }
    }
  }
}
