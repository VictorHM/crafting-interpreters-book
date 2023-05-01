package lox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitExprStmt(Expr stmt);
    R visitPrintStmt(Print stmt);
  }
  static class Expr extends Stmt {
    Expr(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExprStmt(this);
    }

    final Expression expression;
  }
  static class Print extends Stmt {
    Print(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expression expression;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
