package logic;

import static token.TokenType.*;

import exception.ParserException;
import expression.*;
import expression.Expression;
import java.util.ArrayList;
import java.util.List;
import statement.*;
import statement.Statement;
import token.Token;
import token.TokenType;

public class ParserImpl implements Parser {

  private Integer current = 0;
  private List<Token> tokens;

  public ParserImpl() {}

  @Override
  public List<Statement> parse(List<Token> tokens) throws ParserException {
    this.tokens = tokens;
    List<Statement> statements = new ArrayList<>();
    while (!isAtEnd()) statements.add(declaration());
    return statements;
  }

  private Statement declaration() throws ParserException {
    Token currentToken = getCurrent();
    if (checkAndAdvance(CONST, LET)) return declarationStatement(currentToken);
    return statement();
  }

  private Statement statement() throws ParserException {
    if (checkAndAdvance(IF)) return ifStatement();
    if (checkAndAdvance(PRINT)) return printStatement();
    if (checkAndAdvance(LEFTBRACE)) return new BlockStatement(block());
    return expressionStatement();
  }

  private Statement ifStatement() throws ParserException {
    consume(LEFTPAREN, "Expect '(' after 'if'");
    Expression condition = expression();
    consume(RIGHTPAREN, "Expect ')' after if condition");
    Statement thenBranch = statement();
    Statement elseBranch = null;
    if (checkAndAdvance(ELSE)) elseBranch = statement();
    return new IfStatement(condition, thenBranch, elseBranch);
  }

  private List<Statement> block() throws ParserException {
    List<Statement> statements = new ArrayList<>();
    while (!check(RIGHTBRACE) && !isAtEnd()) statements.add(declaration());
    consume(RIGHTBRACE, "Expect '}' after block");
    return statements;
  }

  private Statement declarationStatement(Token currentToken) throws ParserException {
    Token name = consume(IDENTIFIER, "Expect variable name");
    TokenType type = null;
    Expression initializer = null;
    if (checkAndAdvance(COLON)) {
      if (checkAndAdvance(STRING)) {
        type = STRING;
      } else if (checkAndAdvance(NUMBER)) {
        type = NUMBER;
      } else if (checkAndAdvance(BOOLEAN)) {
        type = BOOLEAN;
      }
    } else throw new ParserException("Cannot declare without a type", getCurrent());
    if (checkAndAdvance(EQUAL)) initializer = expression();
    consume(SEMICOLON, "Expect ';' after variable declaration");
    return new DeclarationStatement(currentToken, name, type, initializer);
  }

  private Statement printStatement() throws ParserException {
    Expression value = expression();
    consume(SEMICOLON, "Expect ';' after value");
    return new PrintStatement(value);
  }

  private Statement expressionStatement() throws ParserException {
    Expression expr = expression();
    consume(SEMICOLON, "Expect ';' after expression");
    return new ExpressionStatement(expr);
  }

  private Expression expression() throws ParserException {
    return assignment();
  }

  private Expression assignment() throws ParserException {
    Expression expr = equality();
    if (checkAndAdvance(EQUAL)) {
      Token equals = getPrevious();
      Expression value = assignment();
      if (expr instanceof VariableExpression) {
        Token name = ((VariableExpression) expr).getName();
        return new AssignmentExpression(name, value);
      }
      throw new ParserException("Invalid assignment target", equals);
    }
    return expr;
  }

  private Expression equality() throws ParserException {
    Expression expr = comparison();
    while (checkAndAdvance(BANGEQUAL, EQUALEQUAL)) {
      Token operator = getPrevious();
      Expression right = comparison();
      expr = new BinaryExpression(expr, operator, right);
    }
    return expr;
  }

  private boolean checkAndAdvance(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advanceAndReturnCurrent();
        return true;
      }
    }
    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return getCurrent().getType() == type;
  }

  private Token advanceAndReturnCurrent() {
    if (!isAtEnd()) current++;
    return getPrevious();
  }

  private boolean isAtEnd() {
    return getCurrent().getType() == EOF;
  }

  private Token getCurrent() {
    return tokens.get(current);
  }

  private Token getPrevious() {
    return tokens.get(current - 1);
  }

  private Expression comparison() throws ParserException {
    Expression expr = addition();
    while (checkAndAdvance(GREATER, GREATEREQUAL, LESS, LESSEQUAL)) {
      Token operator = getPrevious();
      Expression right = addition();
      expr = new BinaryExpression(expr, operator, right);
    }
    return expr;
  }

  private Expression addition() throws ParserException {
    Expression expr = multiplication();
    while (checkAndAdvance(MINUS, PLUS)) {
      Token operator = getPrevious();
      Expression right = multiplication();
      expr = new BinaryExpression(expr, operator, right);
    }
    return expr;
  }

  private Expression multiplication() throws ParserException {
    Expression expr = unary();
    while (checkAndAdvance(SLASH, STAR)) {
      Token operator = getPrevious();
      Expression right = unary();
      expr = new BinaryExpression(expr, operator, right);
    }
    return expr;
  }

  private Expression unary() throws ParserException {
    if (checkAndAdvance(BANG, MINUS)) {
      Token operator = getPrevious();
      Expression right = unary();
      return new UnaryExpression(operator, right);
    }
    return primary();
  }

  private Expression primary() throws ParserException {
    if (checkAndAdvance(FALSE)) return new LiteralExpression(false);
    if (checkAndAdvance(TRUE)) return new LiteralExpression(true);
    if (checkAndAdvance(NUMBER, STRING, BOOLEAN))
      return new LiteralExpression(getPrevious().getLiteral());
    if (checkAndAdvance(IDENTIFIER)) return new VariableExpression(getPrevious());
    if (checkAndAdvance(LEFTPAREN)) {
      Expression expr = expression();
      consume(RIGHTPAREN, "Expect ')' after expression.");
      return new GroupedExpression(expr);
    }
    throw new ParserException("Expect expression ", getCurrent());
  }

  private Token consume(TokenType type, String message) throws ParserException {
    if (check(type)) return advanceAndReturnCurrent();
    throw new ParserException(message, getCurrent());
  }
}
