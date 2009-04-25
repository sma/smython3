/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;

public class SyntaxTest {
  private static final String PYTHON3_LIB = "/Users/sma/python3/lib/python3.0";

  public static TestSuite suite() {
    TestSuite testSuite = new TestSuite("SyntaxTest");
    scan(new File(PYTHON3_LIB), testSuite);
    return testSuite;
  }

  private static void scan(File dir, TestSuite testSuite) {
    testSuite.addTest(createTests(dir));
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        scan(file, testSuite);
      }
    }
  }

  private static TestSuite createTests(File dir) {
    String name = "lib" + dir.getAbsolutePath().substring(PYTHON3_LIB.length());
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
