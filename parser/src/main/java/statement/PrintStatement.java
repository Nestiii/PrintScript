package statement;

import expression.Expression;
import visitor.StatementVisitor;

public class PrintStatement implements Statement {

  private final Expression expression;

  public PrintStatement(Expression expression) {
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
