package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while(!isAtEnd()) {
      // we are at the beinning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': 
        // We check if it is a simple dot or it is a decimal point for a number.
        if (!isDigit(peek())) {
          addToken(DOT); 
          break;
        } else {
          number();
          break;
        }
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
      case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
      case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
      case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
      case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line.
          // At the end, we don't call addToken as we want to discard the entire comment.
          // We use peek() so if it is a newline char, does not consume the character. It will stay
          // in char c to be checked the next loop and processed by case '\n', jumping to the next line.
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
        break;

      // Let's ignore other characters we are not interested in.
      case ' ':
      case '\r':
      case '\t':
        break;
      
      case '\n':
        line++;
        break;
      
      // We start processing string literals here.
      case '"': string(); break;

      default:
        if (isDigit(c)) {
          number();
        } else {
          Lox.error(line, "Unexpected character.");
        }
        break;
    }
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    advance(); // The closing ". When the above while breaks, it means we found '"' or '/n'.
    // As it is using peek() that doesn't consume the character, we advance now to do so. And due to
    // the above code that deals with unfinished strings, we only have the option of finding '"' 
    // and that is managed in the following code.

    // Trim the surrounding quotes.
    String value = source.substring(start + 1, current - 1);  // This takes the string in between quotes.
    // the one we have been looking into.
    addToken(STRING, value);
  }

  private void number() {
    // Where does this start? in the '.' in case that is the situation? 
    // Then we must be sure the next char is a digit. How do we add the leading 0?
    boolean isDotNotation = false;
    if (peek() == '.') isDotNotation = true;
    while (isDigit(peek())) advance();

    // Look for fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the .
      advance();
      while (isDigit(peek())) advance();
    }

    // Let's try to add leading zero if that's the case
    if (isDotNotation) {
      addToken(NUMBER, Double.parseDouble("0" + source.substring(start, current)));
    } else {
      addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }
  }
  private boolean match(char expected) {
    if(isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken (TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
