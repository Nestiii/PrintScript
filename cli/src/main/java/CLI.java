import static picocli.CommandLine.*;

import exception.InterpreterException;
import exception.LexerException;
import exception.ParserException;
import java.io.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import logic.*;
import picocli.CommandLine;
import statement.Statement;
import token.Token;

@Command(name = "printscript")
public class CLI implements Callable<Integer> {

  @Option(
      names = {"-v", "--validate"},
      description = "Runs validation only.")
  private boolean validateOnly;

  @Option(
      names = {"-u", "--use"},
      description = "Use specific version.")
  private String version;

  @Parameters(description = "File to run.")
  private File file;

  private final Interpreter interpreter = new InterpreterImpl();
  private final Parser parser = new ParserImpl();
  private final Lexer lexer = new LexerImpl();

  private InputStreamReader getSource(String code) {
    return new InputStreamReader(new ByteArrayInputStream((code).getBytes()));
  }

  @Override
  public Integer call() {
    try {
      if (file == null) file = new File("src/main/resources/test_file.ts");
      BufferedReader br = new BufferedReader(new FileReader(file));
      String lines = br.lines().collect(Collectors.joining("\n"));
      List<Token> tokens;
      if (version.equals("1.0")) tokens = lexer.getTokens(getSource(lines), false, false);
      else tokens = lexer.getTokens(getSource(lines), true, true);
      List<Statement> statements = parser.parse(tokens);
      if (validateOnly) return 0;
      interpreter.interpret(statements);
    } catch (LexerException | ParserException | InterpreterException | FileNotFoundException e) {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }

  public static void main(String[] args) {
    final var exitCode = new CommandLine(new CLI()).execute(args);
    System.exit(exitCode);
  }
}
