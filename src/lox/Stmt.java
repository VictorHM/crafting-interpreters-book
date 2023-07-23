package lox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitExprStmt(Expr stmt);
    R visitPrintStmt(Print stmt);
    R visitVarStmt(Var stmt);
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
  static class Var extends Stmt {
    Var(Token name, Expression initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expression initializer;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
