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
        b.append('Ç').append(scanner.value()).append('È');
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
    assertEquals("INT if NAMEÇaÈ : return ", scan("1 if a: return"));
  }

  @Test
  public void parentheses() {
    assertEquals("( ) ", scan("(\n)"));
    assertEquals("[ ] ", scan("[\n]"));
    assertEquals("{ } ", scan("{\n}"));
  }

  @Test
  public void moreParentheses() {
    assertEquals("{ INT INT } ", scan("{1\n  2\n}"));
    assertEquals(
        ": NEWLINE INDENT { INT INT } NEWLINE INT DEDENT ",
        scan(":\n  {1\n  2\n}\n  3"));
  }

  @Test
  public void indent() {
    assertEquals("def NAMEÇaÈ ( ) : NEWLINE INDENT return DEDENT ", scan("def a():\n return"));
  }

  @Test
  public void moreIndent() {
    assertEquals(
        "class NAMEÇCÈ : NEWLINE INDENT " +
            "def NAMEÇmÈ ( ) : NEWLINE INDENT pass NEWLINE DEDENT " +
            "def NAMEÇmÈ ( ) : NEWLINE INDENT pass NEWLINE DEDENT DEDENT ",
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
    assertEquals("NAMEÇ__init__È NAMEÇfoo_barÈ NAMEÇŠ…•èÈ ", scan("__init__ foo_bar Š…•è"));
  }

  @Test
  public void strings() {
    assertEquals("STR STR STR ", scan("'' 'a' 'abc'"));
    assertEquals("STR STR STR ", scan("\"\" \"a\" \"abc\""));
  }

  @Test
  public void exoticNumber() {
    assertEquals("INT ", scan("\u0664\u06F2")); // arabic
  }

  @Test
  public void newlines() {
    assertEquals("", scan("\n\n")); 
    assertEquals("INT NEWLINE ", scan("1\n"));
    assertEquals("INT NEWLINE INT ", scan("1\n\n2"));
  }

  @Test
  public void comments() {
    assertEquals("", scan("# just a comment\n"));
    assertEquals("INT NEWLINE INT ", scan("1 #\n2"));
    assertEquals("INDENT NAMEÇaÈ NEWLINE NAMEÇbÈ NEWLINE DEDENT ", scan("\n  a\n  #\n  b\n"));
    assertEquals("INDENT NAMEÇaÈ NEWLINE NAMEÇbÈ NEWLINE DEDENT ", scan("\n  a\n#\n  b\n"));
  }

  @Test(expected = RuntimeException.class)
  public void invalid() {
    scan("!");
  }

  @Test
  public void multilineString() {
    assertEquals("STR ", scan("'''a'a''a'''"));
    assertEquals("STR ", scan("\"\"\"a\"a\"\"a\"\"\""));
  }

  @Test
  public void integers() {
    assertEquals("INT INT INT INT ", scan("0 0b10100 0o777 0x01AbCdEf"));
  }

  @Test
  public void floats() {
    assertEquals("FLOAT ", scan(".1"));
    assertEquals("FLOAT ", scan("2."));
    assertEquals("FLOAT ", scan("3.0"));
    assertEquals("FLOAT ", scan("12.345678"));
    assertEquals("FLOAT ", scan("1e10"));
    assertEquals("FLOAT ", scan("2E20"));
    assertEquals("FLOAT ", scan(".1e-20"));
    assertEquals("FLOAT ", scan(".1e+30"));
    assertEquals("FLOAT ", scan("12.E30"));
    assertEquals("FLOAT ", scan("12.E-45"));
    assertEquals("FLOAT ", scan("1.79769313486231e+308"));
  }

  @Test
  public void stringEscapes() {
    assertEquals("STR ", scan("'\\''"));
    assertEquals("STR ", scan("'\\a\\b\\f\\n\\n\\r\\t\\v\\777\\xFf\\u20aC\\\\'"));
    assertEquals("STR ", scan("'\\x00'"));
    assertEquals("STR ", scan("'''\\x00'''"));
    assertEquals("STR ", scan("'a\\\nb'"));
    assertEquals("STR ", scan("'''a\\\nb'''"));
    assertEquals("STR ", scan("'\\\n'"));
    assertEquals("STR ", scan("'''\\\n'''"));
  }

  @Test(expected = ParserException.class)
  public void unterminatedSingleQuotedString() {
    scan("'abc");
  }

  @Test(expected = ParserException.class)
  public void unterminatedTrippleQuotedString() {
    scan("'''abc");
  }

  @Test(expected = ParserException.class)
  public void invalidStringEscape() {
    scan("'''\\xQ'''");
  }

  @Test
  public void rawStrings() {
    assertEquals("STR ", scan("r'\\''"));
    assertEquals("STR ", scan("r\"\\\"\""));
    assertEquals("STR ", scan("'\\\n'"));
  }

  @Test
  public void complex() {
    assertEquals("INT ", scan("1j"));
    assertEquals("FLOAT ", scan(".1j"));
    assertEquals("FLOAT ", scan("1.j"));
    assertEquals("FLOAT ", scan("1.2j"));
  }

  @Test
  public void testTab() {
    assertEquals("def NEWLINE INDENT INT NEWLINE INT DEDENT ", scan("def\n  \t1\n        2"));
  }
}
