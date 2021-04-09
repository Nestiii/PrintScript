package expression;

import token.Token;
import visitor.ExpressionVisitor;

public class VariableExpression implements Expression {

  private final Token name;

  public VariableExpression(Token name) {
    this.name = name;
  }

  public Token getName() {
    return name;
  }

  @Override
  public Object accept(ExpressionVisitor visitor) {
    return visitor.visit(this);
  }
}
