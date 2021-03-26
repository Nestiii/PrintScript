package token;

public interface Token {

    String toString();
    TokenType getType();
    Object getLiteral();
    Integer getLine();
    String getLexeme();

}