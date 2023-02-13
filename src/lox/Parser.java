package lox;

import java.util.List;
import static lox.TokenType.*;

public class Parser {
  private final List<Token> tokens;
  private int current = 0;

  private static class ParseError extends RuntimeException {}
  Parser (List<Token> tokens) {
    this.tokens = tokens;
  }

  Expression parse() {
    try {
      return expression();
    } catch (ParseError error) {
      return null;
    }
  }

  // Main functions representing each level of the grammar of the language.

  private Expression expression() {
    return equality();
  }

  private Expression equality () {
    Expression expr = comparison();
    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expression right = comparison();
      expr = new Expression.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression comparison() {
    Expression expr = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expression right = term();
      expr = new Expression.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression term() {
    Expression expr = factor();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expression right = factor();
      expr = new Expression.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression factor() {
    Expression expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expression right = unary();
      expr = new Expression.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expression right = unary();
      return new Expression.Unary(operator, right);
    }

    return primary();
  }

  private Expression primary() {
    if (match(FALSE)) return new Expression.Literal(false);
    if (match(TRUE)) return new Expression.Literal(true);
    if (match(NIL)) return new Expression.Literal(null);

    if (match(NUMBER, STRING)) {
      return new Expression.Literal(previous().literal);
    }

    if (match(LEFT_PAREN)) {
      Expression expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expression.Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  // Auxiliary functions needed for main parser steps

  private boolean match (TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }


  private boolean check (TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private boolean isAtEnd () {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private ParseError error(Token token, String message) {
    Lox.error(token, message); // Calls static method error associated to the class, not the object.
    return new ParseError();
  }

  private void synchronize() {
    advance();
    while(!isAtEnd()) {
      if (previous().type == SEMICOLON) return;

      // We look to statement boundaries, in order to find the next one and discard everything
      // from the error until the next statement. The idea is to be able to parse with some
      // corretness the rest of the file after the error, without cascading them.
      // Discards every token until it finds one that usually is the start of a new statement.
      switch (peek().type) {
        case CLASS: case FOR: case FUN: case IF: case PRINT: case RETURN:
        case VAR: case WHILE:
          return;
      }

      advance();
    }
  }
}
