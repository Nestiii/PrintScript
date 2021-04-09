package statement;

import expression.Expression;
import visitor.StatementVisitor;

public class IfStatement implements Statement {

  private final Expression condition;
  private final Statement thenStatement;
  private final Statement elseStatement;

  public IfStatement(Expression condition, Statement thenStatement, Statement elseStatement) {
    this.condition = condition;
    this.thenStatement = thenStatement;
    this.elseStatement = elseStatement;
  }

  @Override
  public Expression getExpression() {
    return condition;
  }

  @Override
  public void accept(StatementVisitor visitor) {
    visitor.visit(this);
  }

  public Expression getCondition() {
    return condition;
  }

  public Statement getThenStatement() {
    return thenStatement;
  }

  public Statement getElseStatement() {
    return elseStatement;
  }
}
