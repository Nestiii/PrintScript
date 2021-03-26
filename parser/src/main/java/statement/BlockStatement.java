package statement;

import expression.Expression;
import visitor.StatementVisitor;

import java.util.List;

public class BlockStatement implements Statement {

    private final List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public Expression getExpression() {
        return null;
    }

    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}