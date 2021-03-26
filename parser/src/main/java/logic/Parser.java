package logic;

import exception.ParserException;
import statement.Statement;
import token.Token;

import java.util.List;

public interface Parser {
    List<Statement> parse(List<Token> tokens) throws ParserException;
}