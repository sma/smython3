/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// TODO optional arguments for return and yield
// TODO expression lists add a null
// TODO add messages to exceptions
// TODO implement import statements
// TODO check that break/continue occur only in loops
// TODO check that return occurs only in functions
// TODO implement global and nonlocal so that Var becomes GlobalVar or NonLocalVar
// TODO check whether Var is a LocalVar
// TODO "test" for assignment is way to general in the syntax
// TODO need to distinguish 3, and 3 for ExprStmt and others!

/** Parses a sequence of tokens provided by a scanner into AST nodes. */
public class Parser {
  private final Scanner scanner;
  private String token;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
    this.token = scanner.next();
  }

  /** Returns <code>true</code> if the the currrent token is "t". */
  private boolean is(String t) {
    return token.equals(t);
  }

  /** Returns <code>true</code> if the the currrent token is "t" and consumes it. */
  private boolean at(String t) {
    if (token.equals(t)) {
      token = scanner.next();
      return true;
    }
    return false;
  }

  /** Returns the current token's value and consumes it. */
  private Object value() {
    Object value = scanner.value();
    token = scanner.next();
    return value;
  }

  /** Throws an exception if the current token isn't "t" and otherwise consumes it. */
  private void expect(String token) {
    if (!at(token)) {
      throw new ParserException("expected " + token + " but found " + this.token);
    }
  }

  // file_input: (NEWLINE | stmt)* ENDMARKER
  Suite parseFileInput() {
    Suite suite = new Suite();
    while (!at("END")) {
      if (at("NEWLINE")) {
        continue;
      }
      parseStmt(suite);
    }
    return suite;
  }

  // eval_input: testlist NEWLINE* ENDMARKER
  ExprList parseEvalInput() {
    ExprList exprList = parseExprList();
    while (at("NEWLINE"));
    expect("END");
    return exprList;
  }

  // stmt: simple_stmt | compound_stmt
  void parseStmt(Suite suite) {
    Stmt stmt = parseCompoundStmt();
    if (stmt != null) {
      suite.add(stmt);
    } else {
      parseSimpleStmt(suite);
    }
  }

  // simple_stmt: small_stmt (';' small_stmt)* [';'] NEWLINE
  void parseSimpleStmt(Suite suite) {
    suite.add(parseSmallStmt());
    while (at(";")) {
      suite.add(parseSmallStmt());
    }
    at(";");
    expect("NEWLINE");
  }

  // small_stmt: (expr_stmt | del_stmt | pass_stmt | flow_stmt | import_stmt | global_stmt | nonlocal_stmt | assert_stmt)
  Stmt parseSmallStmt() {
    if (at("del")) {
      // del_stmt: 'del' exprlist
      return new Stmt.Del(parseExprList());
    }
    if (at("pass")) {
      // pass_stmt: 'pass'
      return new Stmt.Pass();
    }
    // flow_stmt: break_stmt | continue_stmt | return_stmt | raise_stmt | yield_stmt
    if (at("break")) {
      // break_stmt: 'break'
      return new Stmt.Break();
    }
    if (at("continue")) {
      // continue_stmt: 'continue'
      return new Stmt.Continue();
    }
    if (at("return")) {
      // return_stmt: 'return' [testlist]
      return new Stmt.Return(parseOptionalTestList());
    }
    if (at("raise")) {
      // raise_stmt: 'raise' [test ['from' test]]
      Expr test1 = parseTest();
      Expr test2 = null;
      if (test1 != null && at("from")) {
        test2 = parseTest();
      }
      return new Stmt.Raise(test1, test2);
    }
    if (at("yield")) {
      // yield_stmt: yield_expr
      return new Stmt.Yield(parseYieldExpr());
    }
    // import_stmt: import_name | import_from
    if (at("import")) {
      // import_name: 'import' dotted_as_names
      return new Stmt.Import(parseDottedAsNames());
    }
    if (at("from")) {
      // import_from: ('from' (('.' | '...')* dotted_name | ('.' | '...')+) 'import' ('*' | '(' import_as_names ')' | import_as_names))
      List<String> dottedName = parseDottedName();
      expect("import");
      List<Stmt.NameAlias> names;
      if (at("*")) {
        names = Collections.emptyList();
      } else if (at("(")) {
        names = parseImportAsNames();
        expect(")");
      } else {
        names = parseImportAsNames();
      }
      return new Stmt.From(dottedName, names); // TODO support . and ..
    }
    if (at("global")) {
      // global_stmt: 'global' NAME (',' NAME)*
      return new Stmt.Global(parseNames());
    }
    if (at("nonlocal")) {
      // nonlocal_stmt: 'nonlocal' NAME (',' NAME)*
      return new Stmt.Nonlocal(parseNames());
    }
    if (at("assert")) {
      // assert_stmt: 'assert' test [',' test]
      Expr test1 = parseTest();
      Expr test2 = at(",") ? parseTest() : null;
      return new Stmt.Assert(test1, test2);
    }
    return parseExprStmt();
  }

  // expr_stmt: testlist (augassign (yield_expr|testlist) | ('=' (yield_expr|testlist))*)
  Stmt parseExprStmt() {
    ExprList tl1 = parseTestList(true);
    Expr left = tl1.exprs.get(0);
    if (at("+=")) {
      return new Stmt.AddAssign(left, parseAssign());
    } else if (at("-=")) {
      return new Stmt.SubAssign(left, parseAssign());
    } else if (at("*=")) {
      return new Stmt.MulAssign(left, parseAssign());
    } else if (at("/=")) {
      return new Stmt.DivAssign(left, parseAssign());
    } else if (at("//=")) {
      return new Stmt.IntDivAssign(left, parseAssign());
    } else if (at("%=")) {
      return new Stmt.ModAssign(left, parseAssign());
    } else if (at("**=")) {
      return new Stmt.PowerAssign(left, parseAssign());
    } else if (at(">>=")) {
      return new Stmt.RshiftAssign(left, parseAssign());
    } else if (at("<<=")) {
      return new Stmt.LshiftAssign(left, parseAssign());
    } else if (at("&=")) {
      return new Stmt.AndAssign(left, parseAssign());
    } else if (at("^=")) {
      return new Stmt.XorAssign(left, parseAssign());
    } else if (at("|=")) {
      return new Stmt.OrAssign(left, parseAssign());
    }
    if (at("=")) {
      return new Stmt.Assign(tl1, parseAssign()); // TODO support a = b = c
    }
    return new Stmt.ExprStmt(tl1);
  }

  // dotted_as_names: dotted_as_name (',' dotted_as_name)*
  List<Stmt.DottedName> parseDottedAsNames() {
    List<Stmt.DottedName> names = new ArrayList<Stmt.DottedName>();
    names.add(parseDottedAsName());
    while (at(",")) {
      names.add(parseDottedAsName());
    }
    return names;
  }

  // dotted_as_name: dotted_name ['as' NAME]
  Stmt.DottedName parseDottedAsName() {
    List<String> names = parseDottedName();
    String alias = at("as") ? parseName() : null;
    return new Stmt.DottedName(names, alias);
  }

  // import_as_names: import_as_name (',' import_as_name)*
  List<Stmt.NameAlias> parseImportAsNames() {
    List<Stmt.NameAlias> names = new ArrayList<Stmt.NameAlias>();
    names.add(parseImportAsName());
    while (at(",")) {
      if (is(")")) {
        break;
      }
      names.add(parseImportAsName());
    }
    return names;
  }

  // import_as_name: NAME ['as' NAME]
  Stmt.NameAlias parseImportAsName() {
    String name = parseName();
    String alias = at("as") ? parseName() : null;
    return new Stmt.NameAlias(name, alias);
  }

  ExprList parseAssign() {
    if (at("yield")) {
      ExprList exprList = new ExprList();
      exprList.single = true;
      exprList.add(parseYieldExpr());
      return exprList;
    }
    return parseExprList(true);
  }

  // compound_stmt: if_stmt | while_stmt | for_stmt | try_stmt | with_stmt | funcdef | classdef | decorated
  Stmt parseCompoundStmt() {
    if (at("if")) {
      // if_stmt: 'if' test ':' suite ('elif' test ':' suite)* ['else' ':' suite]
      Expr cond = parseTest();
      expect(":");
      Suite thenSuite = parseSuite();
      return new Stmt.If(cond, thenSuite, parseIfCont());
    }
    if (at("while")) {
      // while_stmt: 'while' test ':' suite ['else' ':' suite]
      Expr test = parseTest();
      expect(":");
      Suite bodySuite = parseSuite();
      Suite elseSuite = null;
      if (at("else")) {
        expect(":");
        elseSuite = parseSuite();
      }
      return new Stmt.While(test, bodySuite, elseSuite);
    }
    if (at("for")) {
      // for_stmt: 'for' exprlist 'in' testlist ':' suite ['else' ':' suite]
      ExprList names = parseExprList();
      expect("in");
      ExprList items = parseExprList(true);
      expect(":");
      Suite bodySuite = parseSuite();
      Suite elseSuite = null;
      if (at("else")) {
        expect(":");
        elseSuite = parseSuite();
      }
      return new Stmt.For(names, items, bodySuite, elseSuite);
    }
    if (at("try")) {
      // ('try' ':' suite ((except_clause ':' suite)+ ['else' ':' suite] ['finally' ':' suite] | 'finally' ':' suite))
      expect(":");
      Suite bodySuite = parseSuite();
      List<Stmt.Except> exceptList = new ArrayList<Stmt.Except>();
      while (at("except")) {
        // except_clause: 'except' [test ['as' NAME]]
        Expr clause = parseTest();
        String name = at("as") ? parseName() : null;
        expect(":");
        exceptList.add(new Stmt.Except(clause, name, parseSuite()));
      }
      Suite elseSuite = null;
      if (exceptList.size() > 0) {
        if (at("else")) {
          expect(":");
          elseSuite = parseSuite();
        }
      }
      Suite finallySuite = null;
      if (at("finally")) {
        expect(":");
        finallySuite = parseSuite();
      }
      if (exceptList.size() == 0 && finallySuite == null) {
        throw new ParserException();
      }
      // TODO split into TryExcept and TryFinally
      return new Stmt.Try(bodySuite, exceptList, elseSuite, finallySuite);
    }
    if (at("with")) {
      // with_stmt: 'with' test [ with_var ] ':' suite
      Expr expr = parseTest();
      if (expr == null) {
        throw new ParserException();
      }
      Expr binding = at("as") ? parseExpr() : null;
      expect(":");
      return new Stmt.With(expr, binding, parseSuite());
    }
    if (at("def")) {
      return parseFuncDef();
    }
    if (at("class")) {
      return parseClassDef();
    }
    if (at("@")) {
      // decorated: decorators (classdef | funcdef)
      List<Stmt.Decorator> decorators = parseDecorators();
      if (at("def")) {
        Stmt.FuncDef funcDef = parseFuncDef();
        funcDef.setDecorators(decorators);
        return funcDef;
      }
      if (at("class")) {
        Stmt.ClassDef classDef = parseClassDef();
        classDef.setDecorators(decorators);
        return classDef;
      }
      throw new ParserException();
    }
    return null;
  }

  Suite parseIfCont() {
    if (at("elif")) {
      Expr test = parseTest();
      expect(":");
      Suite thenSuite = parseSuite();
      Suite suite = new Suite();
      suite.add(new Stmt.If(test, thenSuite, parseIfCont()));
      return suite;
    }
    if (at("else")) {
      expect(":");
      return parseSuite();
    }
    Suite suite = new Suite();
    suite.add(new Stmt.Pass());
    return suite;
  }

  // decorators: decorator+
  List<Stmt.Decorator> parseDecorators() {
    List<Stmt.Decorator> decorators = new ArrayList<Stmt.Decorator>();
    decorators.add(parseDecorator());
    while (at("@")) {
      decorators.add(parseDecorator());
    }
    return decorators;
  }
  
  // decorator: '@' dotted_name [ '(' [arglist] ')' ] NEWLINE
  Stmt.Decorator parseDecorator() {
    List<String> dottedName = parseDottedName();
    Arglist arglist = null;
    if (at("(")) {
      arglist = parseArglist();
      expect(")");
    }
    expect("NEWLINE");
    return new Stmt.Decorator(dottedName, arglist);
  }

  // dotted_name: NAME ('.' NAME)*
  List<String> parseDottedName() {
    List<String> names = new ArrayList<String>(1);
    names.add(parseName());
    while (at(".")) {
      names.add(parseName());
    }
    return names;
  }

  // funcdef: 'def' NAME parameters ':' suite
  Stmt.FuncDef parseFuncDef() {
    String name = parseName();
    Params parameters = parseParameters();
    expect(":");
    return new Stmt.FuncDef(name, parameters, parseSuite());
  }

  // parameters: '(' [typedargslist] ')' ['->' test]
  Params parseParameters() {
    expect("(");
    Params params = parseTypedArgsList();
    expect(")");
    if (at("->")) {
      params.returnType = parseTest();
    }
    return params;
  }

  // typedargslist: ((tfpdef ['=' test] ',')*
  //                 ('*' [tfpdef] (',' tfpdef ['=' test])* [',' '**' tfpdef] | '**' tfpdef)
  //                 | tfpdef ['=' test] (',' tfpdef ['=' test])* [','])
  Params parseTypedArgsList() {
    return parseArgsList(true);
  }

  // varargslist: ((vfpdef ['=' test] ',')*
  //               ('*' [vfpdef] (',' vfpdef ['=' test])*  [',' '**' vfpdef] | '**' vfpdef)
  //               | vfpdef ['=' test] (',' vfpdef ['=' test])* [','])
  Params parseVarArgsList() {
    return parseArgsList(false);
  }

  private Params parseArgsList(boolean typed) {
    Params params = new Params();
    boolean kwseen = false;
    while (true) {
      if (is("NAME")) {
        Params.Param param = parseParam(typed, true);
        if (kwseen) {
          if (param.init == null) {
            throw new ParserException();
          }
        } else {
          kwseen = param.init != null;
        }
        params.add(param);
      } else if (at("*")) {
        if (params.restPositional != null || params.restKeyword != null) {
          throw new ParserException();
        }
        if (is("NAME")) {
          params.restPositional = parseParam(typed, false);
        } else {
          params.restPositional = new Params.Param(null, null, null);
          throw new UnsupportedOperationException();
        }
      } else if (at("**")) {
        if (params.restKeyword != null) {
          throw new ParserException();
        }
        params.restKeyword = parseParam(typed, false);
      } else {
        break;
      }
      if (!at(",")) {
        break;
      }
    }
    return params;
  }

  private Params.Param parseParam(boolean typed, boolean kwed) {
    String name = parseName();
    Expr type = typed && at(":") ? parseTest() : null;
    Expr init = kwed && at("=") ? parseTest() : null;
    return new Params.Param(name, type, init);
  }

  // classdef: 'class' NAME ['(' [arglist] ')'] ':' suite
  Stmt.ClassDef parseClassDef() {
    String name = parseName();
    Arglist arglist;
    if (at("(")) {
      arglist = parseArglist();
      expect(")");
    } else {
      arglist = new Arglist();
    }
    expect(":");
    return new Stmt.ClassDef(name, arglist, parseSuite());
  }

  // suite: simple_stmt | NEWLINE INDENT stmt+ DEDENT
  Suite parseSuite() {
    if (at("NEWLINE")) {
      expect("INDENT");
      Suite suite = new Suite();
      while (!at("DEDENT")) {
        parseStmt(suite);
      }
      return suite;
    }
    Suite suite = new Suite();
    parseSimpleStmt(suite);
    return suite;
  }

  // ...

  List<String> parseNames() {
    List<String> names = new ArrayList<String>();
    names.add(parseName());
    while (at(",")) {
      names.add(parseName());
    }
    return names;
  }

  String parseName() {
    Object value = scanner.value();
    expect("NAME");
    return (String) value;
  }

  // ....

  // test: or_test ['if' or_test 'else' test] | lambdef
  Expr parseTest() {
    if (at("lambda")) {
      // lambdef: 'lambda' [varargslist] ':' test
      Params params = parseVarArgsList();
      expect(":");
      return new Expr.Lambda(params, parseTest());
    }
    Expr expr = parseOrTest();
    if (at("if")) {
      Expr cond = parseOrTest();
      expect("else");
      expr = new Expr.IfElse(cond, expr, parseTest());
    }
    return expr;
  }

  // or_test: and_test ('or' and_test)*
  Expr parseOrTest() {
    Expr t = parseAndTest();
    while (at("or")) {
      t = new Expr.Or(t, parseAndTest());
    }
    return t;
  }

  // and_test: not_test ('and' not_test)*
  Expr parseAndTest() {
    Expr e = parseNotTest();
    while (at("and")) {
      e = new Expr.And(e, parseNotTest());
    }
    return e;
  }

  // not_test: 'not' not_test | comparison 
  Expr parseNotTest() {
    if (at("not")) {
      return new Expr.Not(parseNotTest());
    }
    return parseComparison();
  }

  // comparison: star_expr (comp_op star_expr)*
  Expr parseComparison() {
    Expr e = parseStarExpr();
    if (isCompOp()) {
      Expr.Comparison comparison = new Expr.Comparison(e);
      while (isCompOp()) {
        if (at("<")) {
          comparison.add(new Expr.Comp.LT(parseStarExpr()));
        } else if (at(">")) {
          comparison.add(new Expr.Comp.GT(parseStarExpr()));
        } else if (at("<=")) {
          comparison.add(new Expr.Comp.LE(parseStarExpr()));
        } else if (at(">=")) {
          comparison.add(new Expr.Comp.GE(parseStarExpr()));
        } else if (at("==")) {
          comparison.add(new Expr.Comp.EQ(parseStarExpr()));
        } else if (at("!=")) {
          comparison.add(new Expr.Comp.NE(parseStarExpr()));
        } else if (at("in")) {
          comparison.add(new Expr.Comp.In(parseStarExpr()));
        } else if (at("is")) {
          if (at("not")) {
            comparison.add(new Expr.Comp.IsNot(parseStarExpr()));
          } else {
            comparison.add(new Expr.Comp.Is(parseStarExpr()));
          }
        } else if (at("not")) {
          if (at("in")) {
            comparison.add(new Expr.Comp.NotIn(parseStarExpr()));
          } else {
            throw new ParserException();
          }
        } else {
          throw new ParserException();
        }
      }
      e = comparison;
    }
    return e;
  }

  // comp_op: '<'|'>'|'=='|'>='|'<='|'<>'|'!='|'in'|'not' 'in'|'is'|'is' 'not'
  boolean isCompOp() {
    return is("<") || is(">") || is("<=") || is(">=") || is("==") || is("!=") || is("in") || is("is") || is("not");
  }

  // star_expr: ['*'] expr
  Expr parseStarExpr() {
    if (at("*")) {
      return new Expr.Star(parseExpr());
    }
    return parseExpr();
  }

  // expr: xor_expr ('|' xor_expr)*
  Expr parseExpr() {
    Expr e = parseXorExpr();
    while (at("|")) {
      e = new Expr.BitOr(e, parseXorExpr());
    }
    return e;
  }

  // xor_expr: and_expr ('^' and_expr)*
  Expr parseXorExpr() {
    Expr e = parseAndExpr();
    while (at("^")) {
      e = new Expr.BitXor(e, parseAndExpr());
    }
    return e;
  }

  // and_expr: shift_expr ('&' shift_expr)*
  Expr parseAndExpr() {
    Expr e = parseShiftExpr();
    while (at("&")) {
      e = new Expr.BitAnd(e, parseShiftExpr());
    }
    return e;
  }

  // shift_expr: arith_expr (('<<'|'>>') arith_expr)*
  Expr parseShiftExpr() {
    Expr e = parseArithExpr();
    String t = token;
    while (at("<<") || at(">>")) {
      if (t.equals("<<")) {
        e = new Expr.BitShiftLeft(e, parseArithExpr());
      } else {
        e = new Expr.BitShiftRight(e, parseArithExpr());
      }
      t = token;
    }
    return e;
  }

  // arith_expr: term (('+'|'-') term)*
  Expr parseArithExpr() {
    Expr e = parseTerm();
    String t = token;
    while (at("+") || at("-")) {
      if (t.equals("+")) {
        e = new Expr.Add(e, parseTerm());
      } else {
        e = new Expr.Sub(e, parseTerm());
      }
      t = token;
    }
    return e;
  }

  // term: factor (('*'|'/'|'%'|'//') factor)*
  Expr parseTerm() {
    Expr e = parseFactor();
    String t = token;
    while (at("*") || at("/") || at("%") || at("//")) {
      if (t.equals("*")) {
        e = new Expr.Mul(e, parseFactor());
      } else if (t.equals("/")) {
        e = new Expr.Div(e, parseFactor());
      } else if (t.equals("%")) {
        e = new Expr.Mod(e, parseFactor());
      } else {
        e = new Expr.IntDiv(e, parseFactor());
      }
      t = token;
    }
    return e;
  }

  // factor: ('+'|'-'|'~') factor | power
  Expr parseFactor() {
    if (at("+")) {
      return new Expr.UnaryPlus(parseFactor());
    }
    if (at("-")) {
      return new Expr.UnaryMinus(parseFactor());
    }
    if (at("~")) {
      return new Expr.BitNeg(parseFactor());
    }
    return parsePower();
  }

  // power: atom trailer* ['**' factor]
  Expr parsePower() {
    Expr a = parseAtom();
    // trailer: '(' [arglist] ')' | '[' subscriptlist ']' | '.' NAME
    while (true) {
      if (at("(")) {
        Arglist arglist = parseArglist();
        a = new Expr.Call(a, arglist);
        expect(")");
        continue;
      }
      if (at("[")) {
        ExprList subscriptlist = parseSubscriptList();
        a = new Expr.Index(a, subscriptlist);
        expect("]");
        continue;
      }
      if (at(".")) {
        a = new Expr.Attr(a, parseName());
        continue;
      }
      break;
    }
    if (at("**")) {
      return new Expr.Power(a, parseFactor());
    }
    return a;
  }

  // atom: ('(' [yield_expr|testlist_comp] ')' | '[' [testlist_comp] ']' | '{' [dictorsetmaker] '}' |
  //       NAME | NUMBER | STRING+ | '...' | 'None' | 'True' | 'False')
  Expr parseAtom() {
    if (at("(")) {
      Expr expr = parseYieldOrTupleOrGenerator();
      expect(")");
      return expr;
    } else if (at("[")) {
      Expr expr = parseTestListComp();
      expect("]");
      return expr;
    } else if (at("{")) {
      Expr expr = parseDictOrSetMaker();
      expect("}");
      return expr;
    } else if (is("NAME")) {
      return new Expr.Var(parseName());
    } else if (is("NUM")) {
      return new Expr.Lit(Python.Int((Integer) value()));
    } else if (is("STR")) {
      String s = (String) value();
      while (is("STR")) {
        s += (String) value();
      }
      return new Expr.Lit(Python.Str(s));
    } else if (at("...")) {
      return new Expr.Lit(Python.Ellipsis);
    } else if (at("None")) {
      return new Expr.Lit(Python.None);
    } else if (at("True")) {
      return new Expr.Lit(Python.True);
    } else if (at("False")) {
      return new Expr.Lit(Python.False);
    }
    return null;
  }

  // subscriptlist: subscript (',' subscript)* [',']
  ExprList parseSubscriptList() {
    ExprList subscriptlist = new ExprList();
    subscriptlist.add(parseSubscript());
    while (at(",")) {
      Expr t = parseSubscript();
      if (t == null) {
        break;
      }
      subscriptlist.add(t);
    }
    return subscriptlist;
  }

  // subscript: test | [test] ':' [test] [sliceop]
  Expr parseSubscript() {
    Expr start = isTestStart() ? parseTest() : null;
    if (at(":")) {
      Expr stop = isTestStart() ? parseTest() : null;
      Expr step = null;
      // sliceop: ':' [test]
      if (at(":")) {
        if (isTestStart()) {
          step = parseTest();
        }
      }
      return new Expr.Slice(start, stop, step);
    }
    return start;
  }

  boolean isTestStart() {
    return !is(":") && !is(",") && !is("]");
  }

  // exprlist
  // testlist

  // dictorsetmaker: ( (test ':' test (comp_for | (',' test ':' test)* [','])) |
  //                   (test (comp_for | (',' test)* [','])) )
  Expr parseDictOrSetMaker() {
    Expr key = parseTest();
    if (key == null) {
      return new Expr.DictConstr(new ExprList());
    }
    if (at(":")) {
      // dict
      Expr value = parseTest();
      if (value == null) {
          throw new ParserException();
        }
      if (at("for")) {
        return new Expr.DictCompr(key, value, parseCompFor(null));
      }
      ExprList testList = new ExprList();
      testList.add(key);
      testList.add(value);
      while (at(",")) {
        key = parseTest();
        if (key == null) {
          break;
        }
        testList.add(key);
        expect(":");
        value = parseTest();
        if (value == null) {
          throw new ParserException();
        }
        testList.add(value);
      }
      return new Expr.DictConstr(testList);
    } else {
      // set
      if (at("for")) {
        return new Expr.SetCompr(key, parseCompFor(null));
      }
      ExprList testList = new ExprList();
      testList.add(key);
      while (at(",")) {
        key = parseTest();
        if (key == null) {
          break;
        }
        testList.add(key);
      }
      return new Expr.SetConstr(testList);
    }
  }

  // yield_expr: 'yield' [testlist]
  Expr parseYieldExpr() {
    return new Expr.Yield(parseOptionalTestList());
  }

  // [yield_expr|testlist_comp]
  Expr parseYieldOrTupleOrGenerator() {
    if (at("yield")) {
      return parseYieldExpr();
    }
    //
    Expr t = parseTest();
    if (t == null) {
      return new Expr.Lit(new Python.Tuple());
    }
    if (at("for")) {
      return new Expr.GeneratorCompr(t, parseCompFor(null));
    }
    if (is(")")) {
      return t;
    }
    ExprList testList = new ExprList();
    testList.add(t);
    while (at(",")) {
      if (is(")")) {
        break;
      }
      testList.add(parseTest());
    }
    return new Expr.TupleConstr(testList);
  }

  // testlist_comp: test ( comp_for | (',' test)* [','] )
  Expr parseTestListComp() {
    Expr t = parseTest();
    if (t == null) {
      return new Expr.ListConstr(new ExprList());
    }
    if (at("for")) {
      return new Expr.ListCompr(t, parseCompFor(null));
    }
    ExprList testList = new ExprList();
    testList.add(t);
    while (at(",")) {
      if (is("]")) {
        break;
      }
      testList.add(parseTest());
    }
    return new Expr.ListConstr(testList);
  }

  // comp_for: 'for' exprlist 'in' or_test [comp_iter]
  Expr.Compr parseCompFor(Expr.Compr compr) {
    ExprList vars = parseExprList();
    expect("in");
    Expr items = parseOrTest();
    return  parseCompIter(new Expr.ComprFor(vars, items, compr));

  }

  // comp_iter: comp_for | comp_if
  Expr.Compr parseCompIter(Expr.Compr compr) {
    if (at("for")) {
      return parseCompFor(compr);
    }
    if (at("if")) {
      return parseCompIf(compr);
    }
    return compr;
  }

  // comp_if: 'if' test_nocond [comp_iter]
  Expr.Compr parseCompIf(Expr.Compr compr) {
    return parseCompIter(new Expr.ComprIf(parseTestNoCond(), compr));
  }

  // test_nocond: or_test | lambdef_nocond
  Expr parseTestNoCond() {
    if (at("lambda")) {
      return new Expr.Lambda(parseLambdaVarargsList(), parseTestNoCond());
    }
    return parseOrTest();
  }

  Params parseLambdaVarargsList() {
    Params params = is("NAME") ? parseVarArgsList() : null;
    expect(":");
    return params;
  }

  // exprlist: star_expr (',' star_expr)* [',']
  ExprList parseExprList() {
    return parseExprList(false);
  }

  ExprList parseExprList(boolean single) {
    ExprList exprList = new ExprList();
    exprList.single = single;
    exprList.add(parseStarExpr());
    while (at(",")) {
      exprList.single = false;
      Expr e = parseStarExpr();
      if (e == null) {
        break;
      }
      exprList.add(e);
    }
    return exprList;
  }

  // testlist: test (',' test)* [',']
  ExprList parseTestList(boolean single) {
    ExprList testlist = new ExprList();
    testlist.single = single;
    Expr test = parseTest();
    if (single && test == null) {
      return testlist;
    }
    testlist.add(test);
    while (at(",")) {
      testlist.single = false;
      Expr t = parseTest();
      if (t == null) {
        break;
      }
      testlist.add(t);
    }
    return testlist;
  }

  ExprList parseOptionalTestList() {
    ExprList testList = parseTestList(true);
    if (testList.exprs.size() == 0) {
      testList.add(new Expr.Lit(Python.None));
    }
    return testList;
  }

  // arglist: (argument ',')* (argument [',']
  //                          |'*' test (',' argument)* [',' '**' test]
  //                          |'**' test)
  Arglist parseArglist() {
    Arglist arglist = new Arglist();
    while (true) {
      if (at("*")) {
        if (arglist.restPositionals != null || arglist.restKeywords != null) {
          throw new ParserException();
        }
        arglist.setRestPositionals(parseTest());
      } else if (at("**")) {
        if (arglist.restKeywords != null) {
          throw new ParserException();
        }
        arglist.setRestKeywords(parseTest());
      } else {
        if (is(",") || is(")")) {
          break;
        }
        parseArgument(arglist);
      }
      if (!at(",")) {
        break;
      }
    }
    return arglist;
  }

  // argument: test [comp_for] | test '=' test
  void parseArgument(Arglist arglist) {
    Expr test = parseTest();
    if (at("for")) {
      arglist.addPositional(new Expr.GeneratorCompr(test, parseCompFor(null)));
    } else if (at("=")) {
      if (!(test instanceof Expr.Var)) {
        throw new ParserException();
      }
      Expr value = parseTest();
      if (value == null) {
        throw new ParserException();
      }
      arglist.addKeyword(((Expr.Var) test).name, value);
    } else {
      arglist.addPositional(test);
    }
  }
}
