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
    public void testEquals() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.2;
                                                let c: boolean = a == a;
                                                let d: boolean = a == b;
                                                let e: boolean = b == b;
                                                let f: boolean = b == a;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), false);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), false);
    }

    @Test
    public void testNotEquals() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.2;
                                                let c: boolean = a != a;
                                                let d: boolean = a != b;
                                                let e: boolean = b != a;
                                                let f: boolean = b != b;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), false);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), false);
    }

    @Test
    public void testGreater() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.2;
                                                let c: boolean = a > b;
                                                let d: boolean = a > a;
                                                let e: boolean = b > a;
                                                let f: boolean = b > b;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), false);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), false);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), false);
    }

    @Test
    public void testGraterEqual() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.2;
                                                let c: boolean = a >= b;
                                                let d: boolean = a >= a;
                                                let e: boolean = b >= a;
                                                let f: boolean = b >= b;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), false);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), true);
    }

    @Test
    public void testLess() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.2;
                                                let c: boolean = a < b;
                                                let d: boolean = a < a;
                                                let e: boolean = b < a;
                                                let f: boolean = b < b;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), false);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), false);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), false);
    }

    @Test
    public void testLessEqual() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.2;
                                                let c: boolean = a <= b;
                                                let d: boolean = a <= a;
                                                let e: boolean = b <= b;
                                                let f: boolean = b <= a;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), true);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), false);
    }

    @Test
    public void testSum() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.2;
                                                let c: number = a + a;
                                                let d: number = a + b;
                                                let e: number = b + a;
                                                let f: number = b + b;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), 4);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), 4.2);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), 4.2);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), 4.4);
    }

    @Test
    public void testSub() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.5;
                                                let c: number = a - a;
                                                let d: number = a - b;
                                                let e: number = b - a;
                                                let f: number = b - b;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), 0);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), -0.5);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), 0.5);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), 0.0);
    }

    @Test
    public void testMult() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.5;
                                                let c: number = a * a;
                                                let d: number = a * b;
                                                let e: number = b * a;
                                                let f: number = b * b;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), 4);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), 5.0);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), 5.0);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), 6.25);
    }

    @Test
    public void testDiv() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let a: number = 2;
                                                let b: number = 2.5;
                                                let c: number = a / a;
                                                let d: number = a / b;
                                                let e: number = b / a;
                                                let f: number = b / b;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("c").getValue(), 1);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("d").getValue(), 0.8);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("e").getValue(), 1.25);
        Assert.assertEquals(interpreter.getEnvironment().getValues().get("f").getValue(), 1.0);
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
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("a").getValue(), -2);
    Assert.assertEquals(interpreter.getEnvironment().getValues().get("b").getValue(), false);
  }

    @Test
    public void tck() {
        List<Statement> statements =
                parser.parse(
                        lexer.getTokens(
                                getSource(
                                        """
                                                let pi: number;
                                                pi = 3.14;
                                                const a: number = pi / 2;
                                                """),
                                true,
                                true));
        interpreter.interpret(statements);
        System.out.println(interpreter.getEnvironment().getValues().get("a").getValue());
    }
}
