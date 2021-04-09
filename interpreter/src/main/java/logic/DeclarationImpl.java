package logic;

import token.TokenType;

public class DeclarationImpl implements Declaration {

  private TokenType keyword;
  private TokenType type;
  private Object value;

  public DeclarationImpl(TokenType keyword, TokenType type, Object value) {
    this.keyword = keyword;
    this.type = type;
    this.value = value;
  }

  @Override
  public TokenType getKeyword() {
    return keyword;
  }

  @Override
  public TokenType getType() {
    return type;
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public void setValue(Object value) {
    this.value = value;
  }
}
