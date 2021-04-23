import exception.InterpreterException;
import exception.LexerException;
import exception.ParserException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import logic.Interpreter;
import logic.InterpreterImpl;
import logic.Lexer;
import logic.LexerImpl;
import logic.Parser;
import logic.ParserImpl;
import picocli.CommandLine;
import statement.Statement;
import token.Token;

public class Tar implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-f", "--file"},
      paramLabel = "ARCHIVE",
      description = "the archive file")
  File archive;

  @CommandLine.Option(
      names = {"-v", "--validate"},
      description = "Activates validation only")
  private boolean onlyValidate = false;

  @CommandLine.Option(
      names = {"-nb", "--noBoolean"},
      description = "Deactivates boolean feature")
  private boolean booleanActive = true;

  @CommandLine.Option(
      names = {"-nc", "--noConst"},
      description = "Deactivates const feature")
  private boolean constActive = true;

  private final Lexer lexer = new LexerImpl();
  private final Parser parser = new ParserImpl();
  private final Interpreter interpreter = new InterpreterImpl();

  @Override
  public Integer call() {
    List<Token> tokens;
    List<Statement> statements;
    try {
      if (archive == null) archive = new File("src/main/resources/test_file.ts");
      tokens =
          lexer.getTokens(
              new InputStreamReader(new FileInputStream(archive)), booleanActive, constActive);
      statements = parser.parse(tokens);
      if (onlyValidate) {
        return 0;
      }
      interpreter.interpret(statements);
    } catch (LexerException | ParserException | InterpreterException | FileNotFoundException e) {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }
}
