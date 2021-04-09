package logic;

import token.TokenType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface LexemeMatcher { Pattern getPattern();TokenType getTokenType();Matcher getMatcher(String input);}