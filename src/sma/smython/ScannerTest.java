/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import org.junit.Test;
import static org.junit.Assert.*;

public class ScannerTest {

  private String scan(String source) {
    StringBuilder b = new StringBuilder();
    Scanner scanner = new Scanner(source);
    while (true) {
      String token = scanner.next();
      if (token == "END") {
        return b.toString();
      }
      b.append(token);
      b.append(' ');
    }
  }

  @Test
  public void nothing() {
    assertEquals("", scan(""));
  }

  @Test
  public void simpleTokens() {
    assertEquals("NUM if NAME : return ", scan("1 if a: return"));
  }

  @Test
  public void parentheses() {
    assertEquals("( ) NEWLINE ", scan("(\n)\n"));
  }

  @Test
  public void indent() {
    assertEquals("def NAME ( ) : NEWLINE INDENT return DEDENT ", scan("def a():\n return"));
  }

  @Test
  public void moreIndent() {
    assertEquals(
        "class NAME : NEWLINE INDENT " +
            "def NAME ( ) : NEWLINE INDENT pass NEWLINE DEDENT " +
            "def NAME ( ) : NEWLINE INDENT pass NEWLINE DEDENT DEDENT ",
        scan("class C:\n def m():\n  pass\n def m():\n  pass\n"));
  }

  @Test
  public void syntax() {
    assertEquals("( ) [ ] { } : ; , . -> @ ... ", scan("()[]{}:;,.->@..."));
  }

  @Test
  public void operators() {
    assertEquals("+ , - , * , / , // , % , ** ", scan("+,-,*,/,//,%,**"));
    assertEquals("& , | , ^ , ~ , << , >> ", scan("&,|,^,~,<<,>>"));
    assertEquals("= , += , -= , *= , /= , %= , &= , |= , ^= , <<= , >>= , **= , //= ",
        scan("=,+=,-=,*=,/=,%=,&=,|=,^=,<<=,>>=,**=,//="));
    assertEquals("< , > , >= , <= , == , != ", scan("<,>,>=,<=,==,!="));
  }
}
