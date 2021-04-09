package expression;

import visitor.ExpressionVisitor;

public interface Expression {
  Object accept(ExpressionVisitor visitor);
}
