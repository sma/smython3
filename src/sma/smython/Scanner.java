/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

// TODO string escapes, bytes strings, raw strings
// TODO floats, bigints
// TODO line continuations
// TODO hex constants
public class Scanner {
  private static final Set<String> keywords = new HashSet<String>(Arrays.asList((
      "and as assert break class continue def del elif else except finally for from global if import in is " +
      "lambda nonlocal not or pass raise return try while with yield None True False").split(" ")));
  private final String source;
  private int index; // index to "source"
  private int level; // 0 = no open ([{, 1+ = some open ([{
  private int[] indents = new int[32];
  private int indent; // point behind the last indentation in "indents"
  private int dedent; // target indentation while dedenting
  private boolean beginOfLine = true;
  private Object value;

  /**
   * Constructs a new scanner for the given source.
   * Call <code>next()</code> to generate the token.
   */
  public Scanner(String source) {
    this.source = source;
  }

  /**
   * Returns the next char from the source or <code>0</code> if no more characters are available.
   */
  private char get() {
    return index++ < source.length() ? source.charAt(index - 1) : 0;
  }

  /**
   * Returns the next token. END marks the end of the source.
   * For NUM, STR and NAME, call <code>value()</code> to get the actual value.
   */
  public String next() {
    // if we're currently dedenting and haven't reached the final indentation level, continue to dedent
    if (indent > 0 && indents[indent - 1] > dedent) {
      indent -= 1;
      return "DEDENT";
    }

    // skip white spaces and count them if at the begin of a logical line
    int ci = 0; // tracks the current line's indent
    char ch = get();
    while (Character.isWhitespace(ch) || ch == '#') {
      if (level == 0) {
        if (ch == '\t') throw new RuntimeException("TAB"); // TODO
        if (ch == '\n') {
          if (!beginOfLine) {
            beginOfLine= true; // with the next char, we're at the beginning of a line
            return "NEWLINE";
          }
          ci = 0; // reset current line's indent
        } else if (ch == '#') {
          while (ch != 0 && ch != '\n') {
            ch = get();
          }
          continue;
        } else if (beginOfLine) {
          ci += 1; // only track the indent at the beginning of a line
        }
      } else if (ch == '#') {
        while (ch != 0 && ch != '\n') {
          ch = get();
        }
        continue;
      }
      ch = get();
    }

    if (ch == 0) {
      // end of source, still some DEDENTs to do?
      dedent = 0;
      if (indent > 0) {
        indent -= 1;
        return "DEDENT";
      }
      return "END"; // signal end of source
    }

    if (beginOfLine && level == 0) {
      beginOfLine = false; // with the next char, we aren't at the beginning of a line anymore
      int li = indent > 0 ? indents[indent - 1] : 0; // last indent
      if (ci > li) {
        dedent = ci;
        indents[indent++] = ci;
        index -= 1;
        return "INDENT";
      }
      if (ci < li) {
        dedent = ci;
        indent -= 1;
        index -= 1;
        return "DEDENT";
      }
    }

    // now dispatch based on the character read
    switch (ch) {
      case '!':
        if (get() == '=') {
          return "!=";
        }
        index -= 1;
        ch = '!'; // invalid character
        break;
      case '"':
        return parseString(ch);
      case '%':
        if (get() == '=') {
          return "%=";
        }
        index -= 1;
        return "%";
      case '&':
        if (get() == '=') {
          return "&=";
        }
        index -= 1;
        return "&";
      case '\'':
        return parseString(ch);
      case '(':
        level += 1;
        return "(";
      case ')':
        level -= 1;
        return ")";
      case '*':
        ch = get();
        if (ch == '=') {
          return "*=";
        } else if (ch == '*') {
          if (get() == '=') {
            return "**=";
          }
          index -= 1;
          return "**";
        }
        index -= 1;
        return "*";
      case '+':
        if (get() == '=') {
          return "+=";
        }
        index -= 1;
        return "+";
      case ',':
        return ",";
      case '-':
        ch = get();
        if (ch == '=') {
          return "-=";
        } else if (ch == '>') {
          return "->";
        }
        index -= 1;
        return "-";
      case '.':
        if (get() == '.') {
          if (get() == '.') {
            return "...";
          }
          index -= 1;
        }
        index -= 1;
        return ".";
      case '/':
        ch = get();
        if (ch == '/') {
          if (get() == '=') {
            return "//=";
          }
          index -= 1;
          return "//";
        } else if (ch == '=') {
          return "/=";
        }
        index -= 1;
        return "/";
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return parseNumber(ch);
      case ':':
        return ":";
      case ';':
        return ";";
      case '<':
        ch = get();
        if (ch == '=') {
          return "<=";
        } else if (ch == '<') {
          if (get() == '=') {
            return "<<=";
          }
          index -= 1;
          return "<<";
        }
        index -= 1;
        return "<";
      case '=':
        if (get() == '=') {
          return "==";
        }
        index -= 1;
        return "=";
      case '>':
        ch = get();
        if (ch == '=') {
          return ">=";
        } else if (ch == '>') {
          if (get() == '=') {
            return ">>=";
          }
          index -= 1;
          return ">>";
        }
        index -= 1;
        return ">";
      case '@':
        return "@";
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
        return parseName(ch);
      case '[':
        level += 1;
        return "[";
      case ']':
        level -= 1;
        return "]";
      case '^':
        if (get() == '=') {
          return "^=";
        }
        index -= 1;
        return "^";
      case '_':
        return parseName(ch);
      case 'a':
        return parseName(ch);
      case 'b':
        ch = get();
        if (ch == '\'' || ch == '"') {
          return parseString(ch); // TODO return BYTES not STR
        }
        index -= 1;
        return parseName('b');
      case 'c':
      case 'd':
      case 'e':
      case 'f':
      case 'g':
      case 'h':
      case 'i':
      case 'j':
      case 'k':
      case 'l':
      case 'm':
      case 'n':
      case 'o':
      case 'p':
      case 'q':
        return parseName(ch);
      case 'r':
        ch = get();
        if (ch == '\'' || ch == '"') {
          return parseString(ch); // TODO support raw
        }
        index -= 1;
        return parseName('r');
      case 's':
      case 't':
      case 'u':
      case 'v':
      case 'w':
      case 'x':
      case 'y':
      case 'z':
        return parseName(ch);
      case '{':
        level += 1;
        return "{";
      case '|':
        if (get() == '=') {
          return "|=";
        }
        index -= 1;
        return "|";
      case '}':
        level -= 1;
        return "}";
      case '~':
        return "~";
      default:
        if (Character.isDigit(ch)) {
          return parseNumber(ch);
        } else if (Character.isLetter(ch)) {
          return parseName(ch);
        }
    }
    throw new RuntimeException("unexpected " + ch);
  }

  private String parseString(char q) {
    StringBuilder b = new StringBuilder(256);
    char ch = get();
    if (ch == q && get() == q) {
      // tripple quote
      ch = get();
      while (true) {
        if (ch == q) {
          if (get() == q) {
            if (get() == q) {
              break;
            }
            index -= 1;
          }
          index -= 1;
        }
        b.append(ch);
        ch = get();
      }
    } else {
      index -= 1;
      while (ch != q) {
        b.append(ch);
        ch = get();
      }
    }
    value = b.toString();
    return "STR";
  }

  private String parseNumber(char ch) {
    StringBuilder b = new StringBuilder(32);
    while (Character.isDigit(ch)) {
      b.append(ch);
      ch = get();
    }
    value = new Integer(b.toString());
    index -= 1;
    return "NUM";
  }

  private String parseName(char ch) {
    StringBuilder b = new StringBuilder(32);
    while (Character.isLetterOrDigit(ch) || ch == '_') {
      b.append(ch);
      ch = get();
    }
    index -= 1;
    String keyword = b.toString();
    value = keyword;
    if (keywords.contains(keyword)) {
      return keyword;
    }
    return "NAME";
  }

  public Object value() {
    return value;
  }

  public int line() {
    int line = 1;
    for (int i = 0; i < index; i++) {
      if (source.charAt(i) == '\n') {
        line += 1;
      }
    }
    return line;
  }
}
