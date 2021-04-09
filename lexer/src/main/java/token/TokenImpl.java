package token;

class TokenImpl implements Token {

  private final TokenType type;
  private final Integer line;
  private final String lexeme;
  private final Object literal;

  TokenImpl(TokenType type, Integer line, String lexeme, Object literal) {
    this.type = type;
    this.line = line;
    this.lexeme = lexeme;
    this.literal = literal;
  }

  @Override
  public TokenType getType() {
    return type;
  }

  @Override
  public Object getLiteral() {
    return literal;
  }

  @Override
  public Integer getLine() {
    return line;
  }

  @Override
  public String getLexeme() {
    return lexeme;
  }

  @Override
  public String toString() {
    return "TokenImpl{"
        + "type: "
        + type
        + ", line: "
        + line
        + ", lexeme: "
        + lexeme
        + ", literal: "
        + literal
        + "}";
  }
}
