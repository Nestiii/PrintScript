package logic;

import static token.TokenType.*;

import exception.InterpreterException;
import expression.*;

import java.util.List;
import java.util.function.Consumer;

import statement.*;
import token.Token;
import visitor.ExpressionVisitor;
import visitor.StatementVisitor;

public class InterpreterImpl implements Interpreter, ExpressionVisitor, StatementVisitor {

    private Environment environment = new EnvironmentImpl();
    private Consumer<String> emitter;

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void interpret(List<Statement> statements) throws InterpreterException {
        this.emitter = System.out::println;
        for (Statement statement : statements) {
            statement.accept(this);
        }
    }

    @Override
    public void interpret(List<Statement> statements, Consumer<String> emitter)
            throws InterpreterException {
        this.emitter = emitter;
        for (Statement statement : statements) {
            statement.accept(this);
        }
    }

    @Override
    public Object visit(BinaryExpression binaryExpression) throws InterpreterException {
        Object left = evaluate(binaryExpression.getLeft());
        Object right = evaluate(binaryExpression.getRight());
        switch (binaryExpression.getOperand().getType()) {
            case BANGEQUAL:
                return !isEqual(left, right);
            case EQUALEQUAL:
                return isEqual(left, right);
            case GREATER:
                checkNumberOperands(binaryExpression.getOperand(), left, right);
                if (left instanceof Integer && right instanceof Integer) return (int) left > (int) right;
                if (left instanceof Double && right instanceof Integer) return (double) left > (int) right;
                if (left instanceof Integer && right instanceof Double) return (int) left > (double) right;
                return (double) left > (double) right;
            case GREATEREQUAL:
                checkNumberOperands(binaryExpression.getOperand(), left, right);
                if (left instanceof Integer && right instanceof Integer) return (int) left >= (int) right;
                if (left instanceof Double && right instanceof Integer) return (double) left >= (int) right;
                if (left instanceof Integer && right instanceof Double) return (int) left >= (double) right;
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(binaryExpression.getOperand(), left, right);
                if (left instanceof Integer && right instanceof Integer) return (int) left < (int) right;
                if (left instanceof Double && right instanceof Integer) return (double) left < (int) right;
                if (left instanceof Integer && right instanceof Double) return (int) left < (double) right;
                return (double) left < (double) right;
            case LESSEQUAL:
                checkNumberOperands(binaryExpression.getOperand(), left, right);
                if (left instanceof Integer && right instanceof Integer) return (int) left <= (int) right;
                if (left instanceof Double && right instanceof Integer) return (double) left <= (int) right;
                if (left instanceof Integer && right instanceof Double) return (int) left <= (double) right;
                return (double) left <= (double) right;
            case MINUS:
                if (left instanceof Integer && right instanceof Integer) return (int) left - (int) right;
                if (left instanceof Double && right instanceof Integer) return (double) left - (int) right;
                if (left instanceof Integer && right instanceof Double) return (int) left - (double) right;
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Number && right instanceof Number) {
                    if (left instanceof Integer && right instanceof Integer) return (int) left + (int) right;
                    if (left instanceof Double && right instanceof Integer) return (double) left + (int) right;
                    if (left instanceof Integer && right instanceof Double) return (int) left + (double) right;
                    return (double) left + (double) right;
                }
                return left.toString() + right.toString();
            case SLASH:
                checkNumberOperands(binaryExpression.getOperand(), left, right);
                if (left instanceof Integer && right instanceof Integer) return (int) left / (int) right;
                if (left instanceof Double && right instanceof Integer) return (double) left / (int) right;
                if (left instanceof Integer && right instanceof Double) return (int) left / (double) right;
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(binaryExpression.getOperand(), left, right);
                if (left instanceof Integer && right instanceof Integer) return (int) left * (int) right;
                if (left instanceof Double && right instanceof Integer) return (double) left * (int) right;
                if (left instanceof Integer && right instanceof Double) return (int) left * (double) right;
                return (double) left * (double) right;
        }
        return null;
    }

    @Override
    public Object visit(UnaryExpression unaryExpression) {
        Object right = evaluate(unaryExpression.getExpression());

        switch (unaryExpression.getOperand().getType()) {
            case MINUS:
                checkNumberOperands(unaryExpression.getOperand(), right);
                if (right instanceof Integer) return -(int) right;
                return -(double) right;
            case BANG:
                return !isTruthy(right);
        }
        return null;
    }

    @Override
    public Object visit(GroupedExpression groupedExpression) {
        return evaluate(groupedExpression.getExpression());
    }

    @Override
    public Object visit(LiteralExpression literalExpression) {
        return literalExpression.getValue();
    }

    @Override
    public Object visit(VariableExpression variableExpression) {
        return environment.getValue(variableExpression.getName());
    }

    @Override
    public Object visit(AssignmentExpression assignmentExpression) {
        Object value = evaluate(assignmentExpression.getValue());

        environment.assign(assignmentExpression.getName(), value);
        return value;
    }

    private Object evaluate(Expression expr) {
        return expr.accept(this);
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

    private void checkNumberOperands(Token operator, Object left, Object right) {
        System.out.println(left + " " + right);
        if (
                left instanceof Double && right instanceof Double ||
                left instanceof Integer && right instanceof Integer ||
                left instanceof Double && right instanceof Integer ||
                left instanceof Integer && right instanceof Double
        )
            return;
        throw new InterpreterException(operator, "Operands must be numbers");
    }

    private void checkNumberOperands(Token operator, Object object) {
        if (object instanceof Double || object instanceof Integer) return;
        throw new InterpreterException(operator, "Operand must be a number");
    }

    @Override
    public void visit(PrintStatement printStatement) {
        Object value = evaluate(printStatement.getExpression());
        System.out.println(value);
        emitter.accept(value.toString());
    }

    @Override
    public void visit(ExpressionStatement expressionStatement) {
        evaluate(expressionStatement.getExpression());
    }

    @Override
    public void visit(DeclarationStatement declarationStatement) {
        Object value = null;
        if (declarationStatement.getInitializer() != null) {
            value = evaluate(declarationStatement.getInitializer());
        }

        if (value == null) {
            environment.addValue(
                    declarationStatement.getName().getLexeme(),
                    declarationStatement.getKeyword().getType(),
                    declarationStatement.getType(),
                    null);
            return;
        }

        if (declarationStatement.getType() == BOOLEAN) {
            if (!(value instanceof Boolean)) {
                throw new InterpreterException(declarationStatement.getName(), "Expected a boolean");
            }
        }
        if (declarationStatement.getType() == NUMBER) {
            if (!(value instanceof Number)) {
                throw new InterpreterException(declarationStatement.getName(), "Expected a number");
            }
        }
        if (declarationStatement.getType() == STRING) {
            if (!(value instanceof String)) {
                throw new InterpreterException(declarationStatement.getName(), "Expected a string");
            }
        }
        environment.addValue(
                declarationStatement.getName().getLexeme(),
                declarationStatement.getKeyword().getType(),
                declarationStatement.getType(),
                value);
    }

    @Override
    public void visit(BlockStatement blockStatement) {
        executeBlock(blockStatement.getStatements(), new EnvironmentImpl(environment));
    }

    void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Statement statement : statements) {
                statement.accept(this);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public void visit(IfStatement ifStatement) {
        if (isTruthy(evaluate(ifStatement.getCondition()))) {
            ifStatement.getThenStatement().accept(this);
        } else if (ifStatement.getElseStatement() != null) {
            ifStatement.getElseStatement().accept(this);
        }
    }
}
