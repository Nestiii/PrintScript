package statement;

import expression.Expression;
import visitor.StatementVisitor;

public class ExpressionStatement implements Statement {

  private final Expression expression;

  public ExpressionStatement(Expression expression) {
    this.expression = expression;
  }

  public Expression getExpression() {
    return expression;
  }

  @Override
  public void accept(StatementVisitor visitor) {
    visitor.visit(this);
  }
}
