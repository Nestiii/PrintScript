import exception.InterpreterException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
import logic.*;
import org.junit.Assert;
import org.junit.Test;
import statement.Statement;
import token.TokenType;

public class InterpreterTest {

  private final Interpreter interpreter = new InterpreterImpl();
  private final Parser parser = new ParserImpl();
  private final Lexer lexer = new LexerImpl();

  private InputStreamReader getSource(String code) {
    return new InputStreamReader(new ByteArrayInputStream((code).getBytes()));
  }

  @Test
  public void testVariableIsSaved() {
    List<Statement> statements =
        parser.parse(
            lexer.getTokens(getSource("let a: string = \"This is a test!\";"), true, true));

    interpreter.interpret(statements);

    Assert.assertEquals(1, interpreter.getEnvironment().getValues().size());
    Assert.assertEquals(
        TokenType.STRING, interpreter.getEnvironment().getValues().get("a").getType());
    Assert.assertEquals(
        "This is a test!", interpreter.getEnvironment().getValues().get("a").getValue());
  }

  @Test(expected = InterpreterException.class)
  public void constantsCannotBeOverwritten() {
    List<Statement> statements =
        parser.parse(
            lexer.getTokens(getSource("const a: boolean = true;" + "a = false;"), true, true));

    interpreter.interpret(statements);
  }

  @Test(expected = InterpreterException.class)
  public void cannotOverWriteTypes() {
    List<Statement> statements =
        parser.parse(lexer.getTokens(getSource("let a: boolean = true;" + "a = 2;"), true, true));

    interpreter.interpret(statements);
  }

  @Test
  public void blockVariablesLifeCycle() {
    List<Statement> statements =
        parser.parse(
            lexer.getTokens(
                getSource("let a: boolean = false; \n {let b: boolean = true;}"), true, true));

    interpreter.interpret(statements);

    Assert.assertEquals(1, interpreter.getEnvironment().getValues().size());
  }
}
