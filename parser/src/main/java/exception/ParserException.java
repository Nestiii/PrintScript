package exception;

import token.Token;

public class ParserException extends RuntimeException {

  private final String message;
  private final Token token;

  public ParserException(String message, Token token) {
    this.message = message;
    this.token = token;
  }

  @Override
  public String getMessage() {
    return message + " at line " + token.getLine();
  }

  public Token getToken() {
    return token;
  }
}
