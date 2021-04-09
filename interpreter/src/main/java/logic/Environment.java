package logic;

import exception.InterpreterException;
import java.util.Map;
import token.Token;
import token.TokenType;

public interface Environment {
  Map<String, Declaration> getValues();

  void addValue(String name, TokenType keyword, TokenType type, Object value);

  void assign(Token name, Object value);

  Object getValue(Token name) throws InterpreterException;

  Environment getEnclosing();
}
