/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class SyntaxTest {
  private int failures, files;

  private void readAndParse(File file) throws Exception {
    System.out.print(file + "...");
    files += 1;
    
    // read file (hopefully, it's UTF-8)
    Reader r = new InputStreamReader(new FileInputStream(file), "utf-8");
    StringBuilder b = new StringBuilder();
    char[] buf = new char[4096];
    int len = r.read(buf);
    while (len != -1) {
      b.append(buf, 0, len);
      len = r.read(buf);
    }
    r.close();
    // now try to parse it
    try {
      parse(b.toString());
      System.out.println("ok.");
    } catch (Exception e) {
      //System.out.println("FAILURE.");
      failures += 1;
    }
  }

  private void parse(String source) throws Exception {
    Parser parser = new Parser(new Scanner(source + "\n"));
    try {
      parser.parseFileInput();
    } catch (Exception e) {
      System.out.println("ERROR in line " + parser.line());
      e.printStackTrace(System.out);
      throw e;
    }
  }

  @Test
  public void example() throws Exception {
    //parse("base = a or b");
  }

  @Test
  public void parseAllFilesOfThePython3Distribution() throws Exception {
    for (File file : new File("/Users/sma/python3/lib/python3.0").listFiles()) {
      if (file.getName().endsWith(".py")) {
        readAndParse(file);
      }
    }
    System.out.println(failures + " failures of " + files + " files");
  }
}
