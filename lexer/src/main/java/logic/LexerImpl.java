package logic;

import exception.LexerException;
import token.Token;
import token.TokenBuilder;
import token.TokenType;

import static token.TokenType.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LexerImpl implements Lexer {

    private final List<Token> tokens = new ArrayList<>();
    private Integer line = 1;
    private final EnumMap<TokenType, String> lexemeMatchers = new EnumMap<TokenType, String>(TokenType.class);

    public LexerImpl() {
        lexemeMatchers.put(IF, "if");
        lexemeMatchers.put(ELSE, "else");
        lexemeMatchers.put(PRINT, "print");
        lexemeMatchers.put(TRUE, "true");
        lexemeMatchers.put(FALSE, "false");
        lexemeMatchers.put(CONST, "const");
        lexemeMatchers.put(LET, "let");
        lexemeMatchers.put(STRING, "string|\\\"([_a-zA-Z0-9 !\\\\/.])*\\\"|'([_a-zA-Z0-9 !\\\\/.])*'");
        lexemeMatchers.put(BOOLEAN, "boolean");
        lexemeMatchers.put(NUMBER, "number|-?[0-9.]+");
        lexemeMatchers.put(LEFTBRACE, "[{]");
        lexemeMatchers.put(RIGHTBRACE, "[}]");
        lexemeMatchers.put(LEFTPAREN, "[(]");
        lexemeMatchers.put(RIGHTPAREN, "[)]");
        lexemeMatchers.put(SEMICOLON, ";");
        lexemeMatchers.put(COLON, ":");
        lexemeMatchers.put(EQUALEQUAL, "==");
        lexemeMatchers.put(EQUAL, "[=]");
        lexemeMatchers.put(BANGEQUAL, "!=");
        lexemeMatchers.put(BANG, "[!]");
        lexemeMatchers.put(GREATER, "[>]");
        lexemeMatchers.put(GREATEREQUAL, ">=");
        lexemeMatchers.put(LESS, "[<]");
        lexemeMatchers.put(LESSEQUAL, "<=");
        lexemeMatchers.put(MINUS, "[-]");
        lexemeMatchers.put(SLASH, "[/]");
        lexemeMatchers.put(STAR, "[*]");
        lexemeMatchers.put(PLUS, "[+]");
        lexemeMatchers.put(NEWLINE, "\n");
        lexemeMatchers.put(IDENTIFIER, "(?:\\b[_a-zA-Z]|\\B\\$)[_$a-zA-Z0-9]*+");
    }

    @Override
    public List<Token> getTokens(InputStreamReader source, boolean booleanActive, boolean constActive) throws LexerException {
        Matcher matcher = getMatcher(new BufferedReader(source).lines().collect(Collectors.joining("\n")));
        while (matcher.find()) {
            if (matcher.group().equals("\n")) {
                line++;
                continue;
            }
            lexemeMatchers.keySet().stream()
                    .filter(tokenType -> matcher.group(tokenType.name()) != null)
                    .findFirst()
                    .map(tokenType -> {
                        if(tokenType == NUMBER) {
                            if(!matcher.group().equals("number")) {
                                return addToken(tokenType, matcher.group(), this.line, Double.parseDouble(matcher.group()));
                            } else {
                                return addToken(tokenType, matcher.group(), this.line,null);
                            }
                        } else if(tokenType == STRING) {
                            if(!matcher.group().equals("string")) {
                                return addToken(tokenType, matcher.group(), this.line, matcher.group().replaceAll("[\"']", ""));
                            } else {
                                return addToken(tokenType, matcher.group(), this.line,null);
                            }
                        } else {
                            return addToken(tokenType, matcher.group(), this.line,null);
                        }
                    })
                    .map(token -> this.checkDisabledFeatures(token, constActive, booleanActive))
                    .orElseThrow(() -> new LexerException("Lexer Error", this.line));
        }
        tokens.add(
                TokenBuilder.createBuilder()
                        .addType(EOF)
                        .addLine(this.line)
                        .addLexeme("")
                        .addLiteral(null)
                        .buildToken());
        return tokens;
    }

    private Token checkDisabledFeatures(Token token, boolean booleanActive, boolean constActive) {
        if(token.getType() == CONST && !constActive) throw new LexerException("Const is not supported.", line);
        if((token.getType() == BOOLEAN |
            token.getType() == TRUE |
            token.getType() == FALSE |
            token.getType() == GREATER |
            token.getType() == GREATEREQUAL |
            token.getType() == LESS |
            token.getType() == LESSEQUAL) && !booleanActive
        ) throw new LexerException("Boolean is not supported", line);

        return token;
    }

    private Matcher getMatcher(String input) {
        return Pattern.compile(Arrays.stream(TokenType.values())
                        .map(tokenType -> String.format("|(?<%s>%s)", tokenType.name(), lexemeMatchers.get(tokenType)))
                        .collect(Collectors.joining())
                        .substring(1)
        ).matcher(input);
    }

    private Token addToken(TokenType type, String lexeme, Integer line, Object literal) {
        Token token = TokenBuilder
                .createBuilder()
                .addType(type)
                .addLine(line)
                .addLexeme(lexeme)
                .addLiteral(literal)
                .buildToken();
        tokens.add(token);
        return token;
    }
}