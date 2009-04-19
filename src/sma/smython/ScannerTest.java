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
      if (token.equals("NAME")) {
        b.append('�').append(scanner.value()).append('�');
      }
      b.append(' ');
    }
  }

  @Test
  public void nothing() {
    assertEquals("", scan(""));
  }

  @Test
  public void simpleTokens() {
    assertEquals("NUM if NAME�a� : return ", scan("1 if a: return"));
  }

  @Test
  public void parentheses() {
    assertEquals("( ) NEWLINE ", scan("(\n)\n"));
  }

  @Test
  public void indent() {
    assertEquals("def NAME�a� ( ) : NEWLINE INDENT return DEDENT ", scan("def a():\n return"));
  }

  @Test
  public void moreIndent() {
    assertEquals(
        "class NAME�C� : NEWLINE INDENT " +
            "def NAME�m� ( ) : NEWLINE INDENT pass NEWLINE DEDENT " +
            "def NAME�m� ( ) : NEWLINE INDENT pass NEWLINE DEDENT DEDENT ",
        scan("class C:\n def m():\n  pass\n def m():\n  pass\n"));
  }

  @Test
  public void syntax() {
    assertEquals("( ) [ ] { } : ; , . -> @ ... ", scan("()[]{}:;,.->@..."));
  }

  @Test
  public void dots() {
    assertEquals(". ", scan("."));
    assertEquals(". . ", scan(".."));
    assertEquals("... ", scan("..."));
  }

  @Test
  public void operators() {
    assertEquals("+ , - , * , / , // , % , ** ", scan("+,-,*,/,//,%,**"));
    assertEquals("& , | , ^ , ~ , << , >> ", scan("&,|,^,~,<<,>>"));
    assertEquals("= , += , -= , *= , /= , %= , &= , |= , ^= , <<= , >>= , **= , //= ",
        scan("=,+=,-=,*=,/=,%=,&=,|=,^=,<<=,>>=,**=,//="));
    assertEquals("< , > , >= , <= , == , != ", scan("<,>,>=,<=,==,!="));
  }

  @Test
  public void names() {
    assertEquals("NAME�__init__� NAME�foo_bar� NAMEǊ���� ", scan("__init__ foo_bar ����"));
  }

  @Test
  public void strings() {
    assertEquals("STR STR STR ", scan("'' 'a' 'abc'"));
    assertEquals("STR STR STR ", scan("\"\" \"a\" \"abc\""));
  }

  @Test
  public void exoticNumber() {
    assertEquals("NUM ", scan("\u0664\u06F2")); // arabic
  }

  @Test(expected = RuntimeException.class)
  public void invalid() {
    scan("!");
  }
}
