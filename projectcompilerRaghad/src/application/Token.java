package application;


public class Token {
    private String token;
    private int lineNumber;

    public Token(String token, int lineNumber) {
        this.token = token;
        this.lineNumber = lineNumber;
    }

    public String getToken() {
        return token;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return token + "[" + lineNumber + "]";
    }
}

