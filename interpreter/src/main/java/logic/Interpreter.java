package logic;

import java.util.List;
import java.util.function.Consumer;
import statement.Statement;

public interface Interpreter {

  void interpret(List<Statement> statements);

  void interpret(List<Statement> statements, Consumer<String> emitter);

  Environment getEnvironment();
}
