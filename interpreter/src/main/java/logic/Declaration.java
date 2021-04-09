package logic;

import token.TokenType;

public interface Declaration {
  TokenType getKeyword();

  TokenType getType();

  Object getValue();

  void setValue(Object value);
}
