package logic;

import exception.LexerException;
import token.Token;

import java.io.InputStreamReader;
import java.util.List;

public interface Lexer {
    List<Token> getTokens(InputStreamReader source, boolean booleanActive, boolean constActive) throws LexerException;
}
