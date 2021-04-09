package statement;

import expression.Expression;
import visitor.StatementVisitor;

public interface Statement {

  Expression getExpression();

  void accept(StatementVisitor visitor);
}
