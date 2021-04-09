package logic;

import exception.InterpreterException;
import token.Token;
import token.TokenType;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentImpl implements Environment {

    private Map<String, Declaration> values;
    private Environment enclosing;

    public EnvironmentImpl(){
        this.values = new HashMap<>();
        enclosing = null;
    }

    public EnvironmentImpl(Environment enclosing){
        this.values = new HashMap<>();
        this.enclosing = enclosing;
    }

    @Override
    public Map<String, Declaration> getValues() {
        return values;
    }

    @Override
    public void addValue(String name, TokenType keyword, TokenType type, Object value) {
        values.put(name, new DeclarationImpl(keyword, type, value));
    }

    @Override
    public void assign(Token name, Object value) {
        if (values.containsKey(name.getLexeme())) {
            Declaration declaration = values.get(name.getLexeme());
            if (declaration.getKeyword() == TokenType.LET) {
                if (declaration.getType() == TokenType.BOOLEAN) {
                    if (!(value instanceof Boolean)) {
                        throw new InterpreterException(name, "Expected a boolean");
                    }
                } else if (declaration.getType() == TokenType.NUMBER) {
                    if (!(value instanceof Number)) {
                        throw new InterpreterException(name, "Expected a number");
                    }
                } else if (declaration.getType() == TokenType.STRING) {
                    if (!(value instanceof String)) {
                        throw new InterpreterException(name, "Expected a strnig");
                    }
                }
                declaration.setValue(value);
                values.put(name.getLexeme(), declaration);
                return;
            } else {
                throw new InterpreterException(name, "Constant cannot be charged");
            }
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new InterpreterException(name, "Undefined variable '" + name.getLexeme() + "'.");
    }

    @Override
    public Object getValue(Token name) throws InterpreterException {
        if (values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme()).getValue();
        }
        if (enclosing != null) return enclosing.getValue(name);

        throw new InterpreterException(name, "Undefined variable '" + name.getLexeme() + "'.");
    }


    @Override
    public Environment getEnclosing() {
        return enclosing;
    }
}
