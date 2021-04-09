package logic;

import exception.ParserException;
import java.util.List;
import statement.Statement;
import token.Token;

public interface Parser {
  List<Statement> parse(List<Token> tokens) throws ParserException;
}
