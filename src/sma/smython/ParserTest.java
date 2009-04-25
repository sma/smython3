/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
  public void parseTrailingSemicolon() {
    assertEquals("Suite[Expr(Lit(1))]", parse("1;\n"));
  }

  @Test
  public void parseRelativeImport() {
    assertEquals("Suite[From(.a, [])]", parse("from .a import *\n"));
    assertEquals("Suite[From(..a, [])]", parse("from ..a import *\n"));
    assertEquals("Suite[From(...a, [])]", parse("from ...a import *\n"));
    assertEquals("Suite[From(....a, [])]", parse("from ....a import *\n"));
    assertEquals("Suite[From(., [])]", parse("from . import *\n"));
    assertEquals("Suite[From(.., [])]", parse("from .. import *\n"));
    assertEquals("Suite[From(..., [])]", parse("from ... import *\n"));
    assertEquals("Suite[From(...., [])]", parse("from .... import *\n"));
  }

  @Test
  public void parseComplexArglist() {
    parse("def pos2key2dict(p1, p2, *, k1=100, k2, **kwarg): pass\n");
    parse("def f(a, b:1, c:2, d, e:3=4, f=5, *g:6, h:7, i=8, j:9=10, **k:11) -> 12: pass\n");
  }
}
