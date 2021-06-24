import exception.InterpreterException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
import logic.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.*;
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

  @Test
  public void testNullSaved() {
    List<Statement> statements =
        parser.parse(lexer.getTokens(getSource("let a: string;"), true, true));
    interpreter.interpret(statements);
    Assert.assertEquals(1, interpreter.getEnvironment().getValues().size());
    Assert.assertNull(interpreter.getEnvironment().getValues().get("a").getValue());
  }

  @Test
  public void constantsCannotBeOverwritten() {
    List<Statement> statements =
        parser.parse(
            lexer.getTokens(getSource("const a: boolean = true;" + "a = false;"), true, true));
    Assertions.assertThrows(InterpreterException.class, () -> interpreter.interpret(statements));
  }

  @Test
  public void cannotOverWriteTypes() {
    List<Statement> statements =
        parser.parse(lexer.getTokens(getSource("let a: boolean = true;" + "a = 2;"), true, true));
    Assertions.assertThrows(InterpreterException.class, () -> interpreter.interpret(statements));
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

  @Test
  public void ifElse() {
    List<Statement> statements =
        parser.parse(
            lexer.getTokens(
                getSource(
                    """
                                                let a: number = 2.0;
                                                let b: string = "Empty";
                                                if (a >= 3.0) {
                                                    b = "GreaterEqual";
                                                } else {
                                                    b = "Less";
                                                }
                                                """),
                true,
                true));
    interpreter.interpret(statements);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("b").getValue(), "Less");
  }

  @Test
  public void testConditionals() {
    List<Statement> statements =
        parser.parse(
            lexer.getTokens(
                getSource(
                    """
                                                let a: number = 2;
                                                let b: number = 2;
                                                let c: boolean = a == b;
                                                let d: boolean = a != b;
                                                let e: boolean = a > b;
                                                let f: boolean = a >= b;
                                                let g: boolean = a < b;
                                                let h: boolean = a <= b;
                                                """),
                true,
                true));
    interpreter.interpret(statements);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), true);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), false);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), false);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), true);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("g").getValue(), false);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("h").getValue(), true);
  }

  @Test
  public void testOperations() {
    List<Statement> statements =
        parser.parse(
            lexer.getTokens(
                getSource(
                    """
                                                let a: number = 2;
                                                let b: number = 2;
                                                let c: number = a + b;
                                                let d: number = a - b;
                                                let e: number = a / b;
                                                let f: number = a * b;
                                                """),
                true,
                true));
    interpreter.interpret(statements);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), 1.0);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), 4.0);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), 4.0);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), 0.0);
  }

  @Test
  public void testUnary() {
    List<Statement> statements =
        parser.parse(
            lexer.getTokens(
                getSource(
                    """
                                                let a: number = -2;
                                                let b: boolean = !true;
                                                print(a);
                                                print(b);
                                                """),
                true,
                true));
    interpreter.interpret(statements);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("a").getValue(), -2.0);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("b").getValue(), false);
  }
}
