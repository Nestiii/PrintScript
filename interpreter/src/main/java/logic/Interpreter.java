package logic;

import statement.Statement;

import java.util.List;

public interface Interpreter {

    void interpret(List<Statement> statements);
    Environment getEnvironment();
}
