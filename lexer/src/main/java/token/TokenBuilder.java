package token;

public class TokenBuilder {

    private TokenType type;
    private Integer line;
    private String lexeme;
    private Object literal;

    public TokenBuilder addType(TokenType type) {
        this.type = type;
        return this;
    }

    public TokenBuilder addLine(Integer line) {
        this.line = line;
        return this;
    }

    public TokenBuilder addLexeme(String lexeme) {
        this.lexeme = lexeme;
        return this;
    }

    public TokenBuilder addLiteral(Object literal) {
        this.literal = literal;
        return this;
    }

    public Token buildToken() {
        return new TokenImpl(type, line, lexeme, literal);
    }

    public static TokenBuilder createBuilder() {
        return new TokenBuilder();
    }
}