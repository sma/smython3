/*
 * Copyright (c) 2009, Stefan Matthias Aust. All rights reserved.
 */
package sma.smython;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

// TODO bytes strings, raw strings
// TODO bigints
// TODO line continuations
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
   * For INT, FLOAT, STR and NAME, call <code>value()</code> to get the actual value.
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
        ch = get();
        if (Character.isDigit(ch)) {
          return parseFloat(0, ch);
        } else if (ch == '.') {
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
      case '\\':
        if (get() == '\n') {
          return next();
        }
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
    char ch = get();
    if (ch == q) {
      if (get() == q) {
        return parseMultilineString(q);
      }
      index -= 1;
    }
    StringBuilder b = new StringBuilder(256);
    while (ch != q) {
      if (ch == 0 || ch == '\n') {
        throw new ParserException("EOL while scanning string literal");
      }
      if (ch == '\\') {
        ch = parseStringEscape();
      }
      b.append(ch);
      ch = get();
    }
    value = b.toString();
    return "STR";
  }

  private String parseMultilineString(char q) {
    StringBuilder b = new StringBuilder(256);
    char ch = get();
    while (true) {
      if (ch == 0) {
        throw new ParserException("EOF while scanning triple-quoted string literal");
      }
      if (ch == q) {
        if (get() == q) {
          if (get() == q) {
            break;
          }
          index -= 1;
        }
        index -= 1;
      }
      if (ch == '\\') {
        ch = parseStringEscape();
      }
      b.append(ch);
      ch = get();
    }
    value = b.toString();
    return "STR";
  }

  private char parseStringEscape() {
    char ch = get();
    switch (ch) {
      case '\n':
        ch = get();
        if (ch == 0) {
          throw new ParserException("EOF in line continuation");
        }
        return ch;
      case 'a':
        return '\007'; // BEL
      case 'b':
        return '\b'; // BS
      case 'f':
        return '\f'; // FF
      case 'n':
        return '\n'; // LF
      case 'r':
        return '\r'; // CR
      case 't':
        return '\t'; // HT
      case 'v':
        return '\013'; //VT
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7': {
        int val = Character.digit(ch, 8);
        for (int i = 0; i < 2; i++) {
          int d = Character.digit(get(), 8);
          if (d == -1) {
            index -= 1;
            break;
          }
          val = val * 8 + d;
        }
        return (char) val;
      }
      case 'x':
        return parseStringEscapeChar(2);
      case 'u':
        return parseStringEscapeChar(4);
      default:
        return ch;
    }
  }

  private char parseStringEscapeChar(int len) {
    int val = 0;
    for (int i = 0; i < len; i++) {
      int d = Character.digit(get(), 16);
      if (d == -1) {
        throw new ParserException("invalid hex digit in string escape");
      }
      val = val * 16 + d;
    }
    return (char) val;
  }

  private String parseNumber(char ch) {
    if (ch == '0') {
      switch (get()) {
        case 'b':
        case 'B':
          return parseInteger(get(), 2);
        case 'o':
        case 'O':
          return parseInteger(get(), 8);
        case 'x':
        case 'X':
          return parseInteger(get(), 16);
        default:
          break;
      }
      index -= 1;
    }
    return parseInteger(ch, 10);
  }

  private String parseInteger(char ch, int radix) {
    int digit = Character.digit(ch, radix);
    if (digit == -1) {
      throw new ParserException("invalid token " + ch);
    }
    int intval = 0;
    while (digit != -1) {
      intval = intval * radix + digit;
      ch = get();
      digit = Character.digit(ch, radix);
    }
    if (radix == 10) {
      if (ch == '.') {
        ch = get();
        if (Character.isDigit(ch) || ch == 'e' || ch == 'E') {
          return parseFloat(intval, ch);
        }
        index -= 1;
        value = new Double(intval);
        return "FLOAT";
      } else if (ch == 'e' || ch == 'E') {
        return parseFloat(intval, ch);
      }
    }
    index -= 1;
    value = new Integer(intval);
    return "INT";
  }

  private String parseFloat(int val, char ch) {
    StringBuilder b = new StringBuilder(32);
    b.append(val);
    while (Character.isDigit(ch)) {
      b.append(ch);
      ch = get();
    }
    if (ch == 'e' || ch == 'E') {
      b.append(ch);
      ch = get();
      if (ch == '-' || ch == '+') {
        b.append(ch);
        ch = get();
      }
      if (!Character.isDigit(ch)) {
        throw new ParserException("invalid token");
      }
      while (Character.isDigit(ch)) {
        b.append(ch);
        ch = get();
      }
    }
    index -= 1;
    value = new Double(b.toString());
    return "FLOAT";
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
