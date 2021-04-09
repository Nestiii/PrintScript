package exception;

import token.Token;

public class InterpreterException extends RuntimeException {
    private Token token;

    public InterpreterException(Token token, String message){
        super(message);
        this.token = token;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at line " + token.getLine();
    }
}
