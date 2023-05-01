package lox;

import java.util.List;

public class Interpreter implements Expression.Visitor<Object>,
                                    Stmt.Visitor<Void> {
  
  @Override
  public Object visitLiteralExpression(Expression.Literal expr) {
    return expr.value;
  }

  void interpret (List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        execute(statement);
      }
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  // Evaluate unary expressions. Single subexpression to evaluate.
  @Override 
  public Object visitUnaryExpression (Expression.Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return -(double)right;  // Statically Java cannot know this right elem has to be a number. Cast happens
        // at run time. that is the core of dynamically type language.
      case BANG:
        return !isTruthy(right);
    }
    return null;
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands (Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;
    throw new RuntimeError(operator, "Operands must be numbers");
  }

  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean) object;
    return true;
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;
    return a.equals(b);
  }

  private String stringify(Object object) {
    if (object == null) return "nil";

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }

  // Evaluate nodes with parentheses. It recursively evaluates the expression contained in the
  // parentheses subexpressions and return it.
  @Override
  public Object visitGroupingExpression(Expression.Grouping expr) {
    return evaluate(expr.expression);
  }

  private Object evaluate(Expression expr) {
    return expr.accept(this);
  }

  // It is the Statement's analogue to evaluate for expressions. As statements
  // are side effects but generates no value/variable.
  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  @Override
  public Void visitExprStmt(Stmt.Expr stmt) {
    evaluate(stmt.expression);
    return null;
  }

@Override
public Void visitPrintStmt(Stmt.Print stmt) {
  Object value = evaluate(stmt.expression);
  System.out.println(stringify(value));
  return null;
}

  @Override
  public Object visitBinaryExpression (Expression.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case GREATER:
        checkNumberOperands(expr.operator, left, right);
        return (double)left > (double)right;
      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double)left >= (double)right;
      case LESS:
        checkNumberOperands(expr.operator, left, right);
        return (double)left < (double)right;
      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double)left <= (double)right;
      case BANG_EQUAL: return !isEqual(left, right);
      case EQUAL_EQUAL: return isEqual(left, right);
      case MINUS: 
        checkNumberOperands(expr.operator, left, right);
        return (double)left - (double)right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double)left + (double)right;
        }
        if (left instanceof String && right instanceof String) {
          return (double)left + (double)right;
        }
        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");

      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        return (double)left / (double)right;
      case STAR:
        checkNumberOperands(expr.operator, left, right);
        return (double)left * (double)right;

    }

    // Unreachable
    return null;
  }
}
