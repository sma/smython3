/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

// TODO a lot of operators
// TODO strings, string escapes, multiline strings
// TODO floats
// TODO line continuations
public class Scanner {
  private static final String[] keywords = ("and as assert break class continue def del elif else except finally for " +
      "from global if import in is lambda nonlocal not or pass raise return try while with yield None True False").split(" ");
  private final String source;
  private int index;
  private int level; // 0 = no open ([{, 1+ = some open ([{
  private int[] indents = new int[32];
  private int indent;
  private int mode; // 0 = beginning of line, 1 = inside line
  private Object value;

  public Scanner(String source) {
    this.source = source;
  }

  /** Returns the next char from the source or <code>0</code> if no more characters are available. */
  private char get() {
    return index++ < source.length() ? source.charAt(index - 1) : 0;
  }

  /** Returns the next token. For NUM and STR, call <code>value()</code> to get the actual value. */
  public String next() {
    int ci = 0; // tracks the current line's indent
    char ch = get();
    while (Character.isWhitespace(ch)) {
      if (ch == '\n') {
        mode = 0; // with the next char, we're at the beginning of a line
        if (level == 0) {
          return "NEWLINE";
        }
        ci = 0; // reset current line's indent
      } else if (mode == 0) {
        ci += 1; // only track the indent at the beginning of a line
      }
      ch = get();
    }
    if (ch == 0) {
      // end of source, still some DEDENTs to do?
      if (indent > 0) {
        indent -= 1;
        return "DEDENT";
      }
      return "END"; // signal end of source
    }
    if (mode == 0) {
      mode = 1; // with the next char, we aren't at the beginning of a line anymore
      int li = indent > 0 ? indents[indent - 1] : 0; // last indent
      if (ci > li) {
        indents[indent++] = ci;
        index -= 1;
        return "INDENT";
      }
      if (ci < li) {
        indent -= 1;
        index -=  1;
        return "DEDENT";
      }
    }
    if (Character.isDigit(ch)) {
      StringBuilder b = new StringBuilder(32);
      while (Character.isDigit(ch)) {
        b.append(ch);
        ch = get();
      }
      value = new Integer(b.toString());
      index -= 1;
      return "NUM";
    }
    if (Character.isLetter(ch)) {
      StringBuilder b = new StringBuilder(32);
      while (Character.isLetterOrDigit(ch) || ch == '_') {
        b.append(ch);
        ch = get();
      }
      value = b.toString();
      index -= 1;
      for (String keyword : keywords) {
        if (keyword.equals(value)) {
          return keyword;
        }
      }
      return "NAME";
    }
    if (ch == ':') {
      return ":";
    } else if (ch == ',') {
      return ",";
    } else if (ch == ';') {
      return ";";
    } else if (ch == '(') {
      level += 1;
      return "(";
    } else if (ch == ')') {
      level -= 1;
      return ")";
    } else if (ch == '[') {
      level += 1;
      return "[";
    } else if (ch == ']') {
      level -= 1;
      return "]";
    } else if (ch == '{') {
      level += 1;
      return "{";
    } else if (ch == '}') {
      level -= 1;
      return "}";
    } else if (ch == '=') {
      ch = get();
      if (ch == '=') {
        return "==";
      }
      index -= 1;
      return "=";
    } else if (ch == '*') {
      ch = get();
      if (ch == '*') {
        return "**";
      }
      index -= 1;
      return "*";
    } else if (ch == '+') {
      ch = get();
      if (ch == '=') {
        return "+=";
      }
      index -= 1;
      return "+";
    } else if (ch == '@') {
      return "@";
    } else if (ch == '.') {
      ch = get();
      if (ch == '.') {
        ch = get();
        if (ch == '.') {
          return "...";
        }
        index =- 1;
        return "..";
      }
      index -= 1;
      return ".";
    } else if (ch == '<') {
      ch = get();
      if (ch == '=') {
        return "<=";
      } else if (ch == '<') {
        return "<<";
      }
      index -= 1;
      return "<";
    } else if (ch == '>') {
      ch = get();
      if (ch == '=') {
        return ">=";
      } else if (ch == '>') {
        return ">>";
      }
      index -= 1;
      return ">";
    } else if (ch == '!') {
      ch = get();
      if (ch == '=') {
        return "!=";
      }
      index -= 1;
      ch = '!'; // this is an error
    }
    throw new RuntimeException("unexpected " + ch);
  }

  public Object value() {
    return value;
  }
}
