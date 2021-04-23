import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

import exception.ParserException;
import expression.BinaryExpression;
import expression.LiteralExpression;
import logic.Lexer;
import logic.LexerImpl;
import logic.Parser;
import logic.ParserImpl;
import org.junit.Assert;
import org.junit.Test;
import statement.*;
import token.Token;

public class ParserTest {

  private final Lexer lexer = new LexerImpl();
  private final Parser parser = new ParserImpl();


  @Test
  public void checkIfStatementAndBlockStatement() {
    List<Token> tokens =
            lexer.getTokens(
                    new InputStreamReader(
                            new ByteArrayInputStream(
                                    ("if( a == 5 ) { let a: string = \"This is a test!\"; \n }")
                                            .getBytes())),
                    true,
                    true);
    List<Statement> statements = parser.parse(tokens);
    Assert.assertEquals(statements.get(0).getClass(), IfStatement.class);
    Assert.assertEquals(statements.get(0).getExpression().getClass(), BinaryExpression.class);
    IfStatement statement = (IfStatement) statements.get(0);
    Assert.assertEquals(statement.getThenStatement().getClass(), BlockStatement.class);
  }

  @Test
  public void checkPrintStatement() {
    List<Token> tokens =
            lexer.getTokens(
                    new InputStreamReader(
                            new ByteArrayInputStream(
                                    ("print(\"This is a test\");")
                                            .getBytes())),
                    true,
                    true);
    List<Statement> statements = parser.parse(tokens);
    Assert.assertEquals(statements.get(0).getClass(), PrintStatement.class);
  }

  @Test
  public void checkDeclarationStatementAndLiteralExpression() {
    List<Token> tokens =
            lexer.getTokens(
                    new InputStreamReader(
                            new ByteArrayInputStream(
                                    ("let a: string = \"This is a test!\"; \n")
                                            .getBytes())),
                    true,
                    true);
    List<Statement> statements = parser.parse(tokens);
    Assert.assertEquals(statements.get(0).getClass(), DeclarationStatement.class);
    Assert.assertEquals(statements.get(0).getExpression().getClass(), LiteralExpression.class);
  }

  @Test(expected = ParserException.class)
  public void statementsWithExtraParenthesis() {
    List<Token> tokens =
            lexer.getTokens(
                    new InputStreamReader(
                            new ByteArrayInputStream(
                                    ("if( a == 5 )) { a; }")
                                            .getBytes())),
                    true,
                    true);

    parser.parse(tokens);
  }

  @Test(expected = ParserException.class)
  public void invalidBlockStatementInsideIfStatement() {
    List<Token> tokens =
            lexer.getTokens(
                    new InputStreamReader(
                            new ByteArrayInputStream(
                                    ("if( a == 5 )) { a }")
                                            .getBytes())),
                    true,
                    true);

    parser.parse(tokens);
  }
}